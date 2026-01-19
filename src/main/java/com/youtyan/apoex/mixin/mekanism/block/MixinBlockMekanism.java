package com.youtyan.apoex.mixin.mekanism.block;

import com.youtyan.apoex.ApoEXMod;
import com.youtyan.apoex.IApoExGenerator;
import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.IApoExMultiblock;
import com.youtyan.apoex.affix.MekanismStatAffix;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mekanism.common.block.BlockMekanism;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.ITileUpgradable;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = BlockMekanism.class, remap = true)
public class MixinBlockMekanism {

    @Inject(method = "setPlacedBy", at = @At("TAIL"))
    private void apoex_setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityMekanism self && tile instanceof IApoExMekanism apoExTile) {

            CompoundTag apoexData = apoExTile.getApoExData();
            apoexData.remove("tick_speed_mult");
            apoexData.remove("energy_capacity_mult");
            apoexData.remove("energy_efficiency_mult");
            apoexData.remove("output_multiplier");
            apoexData.remove("input_reduction");

            apoExTile.setTickSpeedMult(0);
            apoExTile.setEnergyCapacityMultiplier(0);
            apoExTile.setEnergyEfficiencyMultiplier(0);
            apoExTile.setOutputMultiplier(0);
            apoExTile.setInputReduction(0);

            if (apoExTile instanceof IApoExGenerator genTile) {
                apoexData.remove("generation_multiplier");
                apoexData.remove("fuel_efficiency");
                apoexData.remove("heat_efficiency");
                apoexData.remove("fuel_capacity_multiplier");
                apoexData.remove("heat_capacity_multiplier");
                genTile.setGenerationMultiplier(0);
                genTile.setFuelEfficiency(0);
                genTile.setHeatEfficiency(0);
                genTile.setFuelCapacityMultiplier(0);
                genTile.setHeatCapacityMultiplier(0);
            }

            if (stack.hasTag() && stack.getTag().contains(AffixHelper.AFFIX_DATA)) {
                CompoundTag affixData = stack.getTag().getCompound(AffixHelper.AFFIX_DATA);
                apoexData.put(AffixHelper.AFFIX_DATA, affixData.copy());

                Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);

                float tickSpeedMult = 0;
                float capacityMult = 0;
                float efficiencyMult = 0;
                float outputMultiplier = 0;
                float inputReduction = 0;

                float generationMultiplier = 0;
                float fuelEfficiency = 0;
                float heatEfficiency = 0;
                float fuelCapacityMultiplier = 0;
                float heatCapacityMultiplier = 0;

                for (AffixInstance inst : affixes.values()) {
                    Affix affix = inst.affix().get();
                    if (affix instanceof MekanismStatAffix mekAffix) {
                        ResourceLocation id = inst.affix().getId();
                        float modifier = mekAffix.getModifier(inst.rarity().get(), inst.level());

                        if (id.getNamespace().equals(ApoEXMod.MODID)) {
                            String path = id.getPath();
                            if (path.contains("tickspeed") || path.contains("tick_speed")) {
                                tickSpeedMult += modifier;
                            } else if (path.contains("energy_capacity")) {
                                capacityMult += modifier;
                            } else if (path.contains("energy_efficiency")) {
                                efficiencyMult += modifier;
                            } else if (path.contains("output_multiplier")) {
                                outputMultiplier += modifier;
                            } else if (path.contains("input_reduction")) {
                                inputReduction += modifier;
                            } else if (path.contains("generation_multiplier")) {
                                generationMultiplier += modifier;
                            } else if (path.contains("fuel_efficiency")) {
                                fuelEfficiency += modifier;
                            } else if (path.contains("heat_efficiency")) {
                                heatEfficiency += modifier;
                            } else if (path.contains("fuel_capacity")) {
                                fuelCapacityMultiplier += modifier;
                            } else if (path.contains("heat_capacity")) {
                                heatCapacityMultiplier += modifier;
                            } else if (path.contains("multiblock_generation_multiplier")) {
                                generationMultiplier += modifier;
                            } else if (path.contains("multiblock_energy_capacity")) {
                                capacityMult += modifier;
                            }
                        }
                    }
                }

                apoExTile.setTickSpeedMult(tickSpeedMult);
                apoExTile.setEnergyCapacityMultiplier(capacityMult);
                apoExTile.setEnergyEfficiencyMultiplier(efficiencyMult);
                apoExTile.setOutputMultiplier(outputMultiplier);
                apoExTile.setInputReduction(inputReduction);

                if (apoExTile instanceof IApoExGenerator genTile) {
                    genTile.setGenerationMultiplier(generationMultiplier);
                    genTile.setFuelEfficiency(fuelEfficiency);
                    genTile.setHeatEfficiency(heatEfficiency);
                    genTile.setFuelCapacityMultiplier(fuelCapacityMultiplier);
                    genTile.setHeatCapacityMultiplier(heatCapacityMultiplier);
                }

                if (self instanceof ITileUpgradable) {
                    ITileUpgradable updatable = (ITileUpgradable) self;
                    updatable.recalculateUpgrades(mekanism.api.Upgrade.ENERGY);
                }

                self.setChanged();
                if (!world.isClientSide && !self.isRemoved()) {
                    apoExTile.sendUpdatePacket();
                }

                if (self instanceof TileEntityMultiblock<?> multiblockTile) {
                    MultiblockData multiblock = multiblockTile.getMultiblock();
                    if (multiblock != null && multiblock.isFormed() && multiblock instanceof IApoExMultiblock apoExMultiblock) {
                        apoExMultiblock.recalculate(world, multiblock.locations);
                    }
                }
            }
        }
    }

    @Inject(method = "getDrops", at = @At("RETURN"))
    private void apoex_getDrops(BlockState state, LootParams.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> drops = cir.getReturnValue();
        BlockEntity tile = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (tile instanceof TileEntityMekanism self && tile instanceof IApoExMekanism apoExTile && !drops.isEmpty()) {
            if (self instanceof TileEntityMultiblock<?> multiblockTile) {
                MultiblockData multiblock = multiblockTile.getMultiblock();
                if (multiblock != null && multiblock.isFormed() && multiblock instanceof IApoExMultiblock apoExMultiblock) {
                    apoExMultiblock.recalculate(self.getLevel(), multiblock.locations);
                }
            }

            CompoundTag apoexData = apoExTile.getApoExData();
            if (apoexData.contains(AffixHelper.AFFIX_DATA)) {
                CompoundTag affixData = apoexData.getCompound(AffixHelper.AFFIX_DATA);

                for (ItemStack drop : drops) {
                    if (drop.getItem() instanceof mekanism.common.item.block.ItemBlockMekanism) {
                        drop.getOrCreateTag().put(AffixHelper.AFFIX_DATA, affixData.copy());
                    }
                }
            }

            if (!self.getLevel().isClientSide && !self.isRemoved()) {
                apoExTile.sendUpdatePacket();
            }
        }
    }
}
