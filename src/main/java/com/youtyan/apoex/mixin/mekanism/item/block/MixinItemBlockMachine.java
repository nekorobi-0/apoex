package com.youtyan.apoex.mixin.mekanism.item.block;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.item.block.machine.ItemBlockMachine;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(ItemBlockMachine.class)
public abstract class MixinItemBlockMachine extends ItemBlockTooltip<BlockTile<?, ?>> {

    private static final ThreadLocal<Boolean> APOEX_GUARD = ThreadLocal.withInitial(() -> false);

    public MixinItemBlockMachine(BlockTile<?, ?> block) {
        super(block);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (APOEX_GUARD.get()) {
            return super.getAttributeModifiers(slot, stack);
        }
        APOEX_GUARD.set(true);
        try {
            Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
            ArrayListMultimap<Attribute, AttributeModifier> mutableModifiers = ArrayListMultimap.create(modifiers);

            if (slot == EquipmentSlot.MAINHAND) {
                Map<dev.shadowsoffire.placebo.reload.DynamicHolder<? extends Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);
                for (AffixInstance inst : affixes.values()) {
                    inst.addModifiers(slot, mutableModifiers::put);
                }
                SocketHelper.getGems(stack).gems().forEach(gem -> gem.addModifiers(slot, mutableModifiers::put));
            }
            return mutableModifiers;
        } finally {
            APOEX_GUARD.set(false);
        }
    }
}