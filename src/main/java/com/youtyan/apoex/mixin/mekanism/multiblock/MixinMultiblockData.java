package com.youtyan.apoex.mixin.mekanism.multiblock;

import com.youtyan.apoex.IApoExGenerator;
import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.IApoExMultiblock;
import mekanism.api.Action;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(value = MultiblockData.class, remap = false)
public abstract class MixinMultiblockData implements IApoExMultiblock {

    @Shadow public List<IEnergyContainer> energyContainers;
    @Shadow public List<IExtendedFluidTank> fluidTanks;
    @Shadow public List<IGasTank> gasTanks;

    @Unique
    private float apoex_multiblock_consumptionAccumulator = 100F;

    @Override
    public float getConsumptionAccumulator() {
        return this.apoex_multiblock_consumptionAccumulator;
    }

    @Override
    public void setConsumptionAccumulator(float value) {
        this.apoex_multiblock_consumptionAccumulator = value;
    }

    @Inject(method = "shouldCap", at = @At("HEAD"), cancellable = true)
    private void apoex_preventCapping(MultiblockCache.CacheSubstance<?, ?> type, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IApoExMultiblock) {
            cir.setReturnValue(false);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MixinMultiblockData.class);

    @Unique private float generationMultiplier = 0;
    @Unique private float fuelEfficiency = 0;
    @Unique private float heatEfficiency = 0;
    @Unique private float storedFuelFraction = 0;
    @Unique private float fuelCapacityMultiplier = 0;
    @Unique private float energyCapacityMultiplier = 0;
    @Unique private float tickSpeedMultiplier = 0;
    @Unique private float damageResistance = 0;
    @Unique private float heatCapacityMultiplier = 0;

    @Unique private boolean isRecalculating = false;
    @Unique private long lastRecalculationTime = -1;

    //<editor-fold desc="Getters and Setters">
    @Override
    public float getGenerationMultiplier() { return generationMultiplier; }
    @Override
    public void setGenerationMultiplier(float value) { this.generationMultiplier = value; }
    @Override
    public float getFuelEfficiency() { return fuelEfficiency; }
    @Override
    public void setFuelEfficiency(float value) { this.fuelEfficiency = value; }
    @Override
    public float getHeatEfficiency() { return heatEfficiency; }
    @Override
    public void setHeatEfficiency(float value) { this.heatEfficiency = value; }
    @Override
    public float getStoredFuelFraction() { return this.storedFuelFraction; }
    @Override
    public void setStoredFuelFraction(float value) { this.storedFuelFraction = value; }
    @Override
    public float getFuelCapacityMultiplier() { return this.fuelCapacityMultiplier; }
    @Override
    public void setFuelCapacityMultiplier(float value) { this.fuelCapacityMultiplier = value; }
    @Override
    public float getEnergyCapacityMultiplier() { return this.energyCapacityMultiplier; }
    @Override
    public void setEnergyCapacityMultiplier(float value) { this.energyCapacityMultiplier = value; }
    @Override
    public float getTickSpeedMultiplier() { return this.tickSpeedMultiplier; }
    @Override
    public void setTickSpeedMultiplier(float value) { this.tickSpeedMultiplier = value; }
    @Override
    public float getDamageResistance() { return this.damageResistance; }
    @Override
    public void setDamageResistance(float value) { this.damageResistance = value; }
    @Override
    public float getHeatCapacityMultiplier() { return this.heatCapacityMultiplier; }
    @Override
    public void setHeatCapacityMultiplier(float value) { this.heatCapacityMultiplier = value; }
    //</editor-fold>

    @Override
    public void addAffixesFromTile(IApoExGenerator tile) {
        this.generationMultiplier += tile.getGenerationMultiplier();
        this.fuelEfficiency += tile.getFuelEfficiency();
        this.heatEfficiency += tile.getHeatEfficiency();
        this.fuelCapacityMultiplier += tile.getFuelCapacityMultiplier();
        this.energyCapacityMultiplier += tile.getEnergyCapacityMultiplier();
        this.heatCapacityMultiplier += tile.getHeatCapacityMultiplier();
    }

    @Override
    public void recalculate(Level world, Set<BlockPos> locations) {
        if (isRecalculating || world.isClientSide() || world.getGameTime() == lastRecalculationTime) {
            return;
        }
        isRecalculating = true;
        lastRecalculationTime = world.getGameTime();

        try {
            // Reset values
            this.generationMultiplier = 0;
            this.fuelEfficiency = 0;
            this.heatEfficiency = 0;
            this.fuelCapacityMultiplier = 0;
            this.energyCapacityMultiplier = 0;
            this.tickSpeedMultiplier = 0;
            this.damageResistance = 0;
            this.heatCapacityMultiplier = 0;
            this.apoex_multiblock_consumptionAccumulator = 100F; //カウンターをリセット

            if (locations != null) {
                for (BlockPos pos : locations) {
                    BlockEntity tile = world.getBlockEntity(pos);
                    if (tile instanceof IApoExMekanism mekTile) {
                        CompoundTag data = mekTile.getApoExData();
                        if (data != null && !data.isEmpty()) {
                            LOGGER.debug("[ApoEx Debug] Found apoexData at {}: {}", pos, data);

                            if (tile instanceof IApoExGenerator gen) {
                                this.addAffixesFromTile(gen);
                            } else {
                                this.generationMultiplier += data.getFloat("generation_multiplier");
                                this.fuelEfficiency += data.getFloat("fuel_efficiency");
                                this.heatEfficiency += data.getFloat("heat_efficiency");
                                this.fuelCapacityMultiplier += data.getFloat("fuel_capacity_multiplier");
                                this.energyCapacityMultiplier += data.getFloat("energy_capacity_mult");
                                this.heatCapacityMultiplier += data.getFloat("heat_capacity_multiplier");
                            }
                            this.damageResistance += data.getFloat("damage_resistance");
                        }

                        float speed = mekTile.getTickSpeedMult();
                        if (speed > this.tickSpeedMultiplier) {
                            this.tickSpeedMultiplier = speed;
                        }
                    }
                }
            }
            
            // 効率が100%を超えないように99%にキャップする
            if (this.fuelEfficiency >= 1.0F) {
                this.fuelEfficiency = 0.99F;
            }

            this.updateHeatCapacity();

            // 容量変更後に保有量を調整する
            if (!world.isClientSide()) {
                for (IEnergyContainer container : energyContainers) {
                    if (!container.isEmpty() && container.getEnergy().greaterThan(container.getMaxEnergy())) {
                        container.setEnergy(container.getMaxEnergy());
                    }
                }
                for (IExtendedFluidTank tank : fluidTanks) {
                    if (!tank.isEmpty() && tank.getFluidAmount() > tank.getCapacity()) {
                        tank.setStackSize(tank.getCapacity(), Action.EXECUTE);
                    }
                }
                for (IGasTank tank : gasTanks) {
                    if (!tank.isEmpty() && tank.getStored() > tank.getCapacity()) {
                        tank.setStackSize(tank.getCapacity(), Action.EXECUTE);
                    }
                }
            }

        } finally {
            isRecalculating = false;
        }
    }

    @Inject(method = "writeUpdateTag", at = @At("TAIL"))
    private void apoex_writeUpdateTag(CompoundTag tag, CallbackInfo ci) {
        tag.putFloat("apoex_gen_mult", this.generationMultiplier);
        tag.putFloat("apoex_fuel_eff", this.fuelEfficiency);
        tag.putFloat("apoex_heat_eff", this.heatEfficiency);
        tag.putFloat("apoex_fuel_cap_mult", this.fuelCapacityMultiplier);
        tag.putFloat("apoex_energy_cap_mult", this.energyCapacityMultiplier);
        tag.putFloat("apoex_tick_speed", this.tickSpeedMultiplier);
        tag.putFloat("apoex_damage_res", this.damageResistance);
        tag.putFloat("apoex_heat_cap_mult", this.heatCapacityMultiplier);
    }

    @Inject(method = "readUpdateTag", at = @At("TAIL"))
    private void apoex_readUpdateTag(CompoundTag tag, CallbackInfo ci) {
        this.generationMultiplier = tag.getFloat("apoex_gen_mult");
        this.fuelEfficiency = tag.getFloat("apoex_fuel_eff");
        this.heatEfficiency = tag.getFloat("apoex_heat_eff");
        this.fuelCapacityMultiplier = tag.getFloat("apoex_fuel_cap_mult");
        this.energyCapacityMultiplier = tag.getFloat("apoex_energy_cap_mult");
        this.tickSpeedMultiplier = tag.getFloat("apoex_tick_speed");
        this.damageResistance = tag.getFloat("apoex_damage_res");
        this.heatCapacityMultiplier = tag.getFloat("apoex_heat_cap_mult");

        this.updateHeatCapacity();
    }
}
