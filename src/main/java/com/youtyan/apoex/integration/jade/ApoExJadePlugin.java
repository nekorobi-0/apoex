package com.youtyan.apoex.integration.jade;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.IApoExMultiblock;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@WailaPlugin
public class ApoExJadePlugin implements IWailaPlugin {

    public static final ResourceLocation UID = new ResourceLocation("apoex", "affixes");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ApoExJadeProvider.INSTANCE, BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ApoExJadeProvider.INSTANCE, Block.class);
    }

    public static class ApoExJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

        public static final ApoExJadeProvider INSTANCE = new ApoExJadeProvider();

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (accessor.getBlockEntity() instanceof TileEntityMekanism) {
                CompoundTag data = accessor.getServerData();
                if (data.contains("apoex_data")) {
                    CompoundTag apoexData = data.getCompound("apoex_data");
                    ItemStack stack = new ItemStack(accessor.getBlockState().getBlock());
                    stack.setTag(apoexData);

                    Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);
                    if (!affixes.isEmpty()) {
                        tooltip.add(Component.literal(""));
                        tooltip.add(Component.translatable("text.apoex.affixes").withStyle(ChatFormatting.GOLD));

                        Set<ResourceLocation> baseAffixIds = affixes.keySet().stream()
                            .map(DynamicHolder::getId)
                            .filter(id -> id.getPath().endsWith("_additions"))
                            .map(id -> new ResourceLocation(id.getNamespace(), id.getPath().replace("_additions", "")))
                            .collect(Collectors.toSet());

                        for (AffixInstance inst : affixes.values()) {
                            if (baseAffixIds.contains(inst.affix().getId())) {
                                continue;
                            }
                            Component name = inst.affix().get().getName(true).copy().withStyle(Style.EMPTY.withColor(inst.rarity().get().getColor()));
                            tooltip.add(name);
                            Component desc = inst.affix().get().getDescription(stack, inst.rarity().get(), inst.level());
                            if (!desc.getString().isEmpty()) {
                                tooltip.add(Component.literal("  ").append(desc.copy().withStyle(ChatFormatting.GRAY)));
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            BlockEntity be = accessor.getBlockEntity();
            if (be instanceof IApoExMekanism mek) {
                CompoundTag apoexData = mek.getApoExData();
                if (apoexData != null && !apoexData.isEmpty()) {
                    data.put("apoex_data", apoexData.copy());
                }
            } else if (be instanceof TileEntityMultiblock<?> multiblock) {
                if (multiblock.getMultiblock() instanceof IApoExMultiblock apoExMulti) {
                    Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = apoExMulti.getAffixes();
                    if (affixes != null && !affixes.isEmpty()) {
                        ItemStack stack = new ItemStack(be.getBlockState().getBlock());
                        AffixHelper.setAffixes(stack, affixes);
                        data.put("apoex_data", stack.getOrCreateTag());
                    }
                }
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }
}
