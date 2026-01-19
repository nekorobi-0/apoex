package com.youtyan.apoex.mixin.mekanism.tile;

import com.youtyan.apoex.IApoExGenerator;
import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.mixin.mekanism.accessor.TileEntityRecipeMachineAccessor;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.ITileUpgradable;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = TileEntityMekanism.class, remap = false)
public abstract class MixinTileEntityMekanism implements IApoExMekanism, IApoExGenerator {

    @Shadow protected abstract void onUpdateServer();
    @Shadow public abstract List<IEnergyContainer> getEnergyContainers(net.minecraft.core.Direction side);

    @Unique
    private CompoundTag apoexData = new CompoundTag();
    @Unique
    private float tickSpeedMult = 0;
    @Unique
    private float outputMultiplier = 0;
    @Unique
    private float inputReduction = 0;
    @Unique
    private boolean apoex_isTicking = false;
    @Unique
    private float storedOutputFraction = 0;
    @Unique
    private float energyCapacityMult = 0;
    @Unique
    private float energyEfficiencyMult = 0;

    // Generator specific
    @Unique
    private float generationMultiplier = 0;
    @Unique
    private float fuelEfficiency = 0;
    @Unique
    private float heatEfficiency = 0;
    @Unique
    private float storedFuelFraction = 0;
    @Unique
    private float fuelCapacityMultiplier = 0;
    @Unique
    private float heatCapacityMultiplier = 0;

    @Override
    public CompoundTag getApoExData() {
        return this.apoexData;
    }

    @Override
    public void setApoExData(CompoundTag tag) {
        this.apoexData = tag;
    }

    @Override
    public float getTickSpeedMult() {
        return this.tickSpeedMult;
    }

    @Override
    public void setTickSpeedMult(float value) {
        this.tickSpeedMult = value;
    }

    @Override
    public float getOutputMultiplier() {
        return this.outputMultiplier;
    }

    @Override
    public void setOutputMultiplier(float value) {
        this.outputMultiplier = value;
    }

    @Override
    public float getInputReduction() {
        return this.inputReduction;
    }

    @Override
    public void setInputReduction(float value) {
        this.inputReduction = value;
    }

    @Override
    public void apoex_onUpdateServer() {
        this.onUpdateServer();
    }

    @Override
    public boolean isApoExTicking() {
        return this.apoex_isTicking;
    }

    @Override
    public void setApoExTicking(boolean ticking) {
        this.apoex_isTicking = ticking;
    }

    @Override
    public float getStoredOutputFraction() {
        return this.storedOutputFraction;
    }

    @Override
    public void setStoredOutputFraction(float value) {
        this.storedOutputFraction = value;
    }

    @Override
    public float getEnergyCapacityMultiplier() {
        return this.energyCapacityMult;
    }

    @Override
    public void setEnergyCapacityMultiplier(float value) {
        this.energyCapacityMult = value;
    }

    @Override
    public float getEnergyEfficiencyMultiplier() {
        return this.energyEfficiencyMult;
    }

    @Override
    public void setEnergyEfficiencyMultiplier(float value) {
        this.energyEfficiencyMult = value;
    }

    @Override
    public float getGenerationMultiplier() {
        return this.generationMultiplier;
    }

    @Override
    public void setGenerationMultiplier(float value) {
        this.generationMultiplier = value;
    }

    @Override
    public float getFuelEfficiency() {
        return this.fuelEfficiency;
    }

    @Override
    public void setFuelEfficiency(float value) {
        this.fuelEfficiency = value;
    }

    @Override
    public float getHeatEfficiency() {
        return this.heatEfficiency;
    }

    @Override
    public void setHeatEfficiency(float value) {
        this.heatEfficiency = value;
    }

    @Override
    public float getStoredFuelFraction() {
        return this.storedFuelFraction;
    }

    @Override
    public void setStoredFuelFraction(float value) {
        this.storedFuelFraction = value;
    }

    @Override
    public float getFuelCapacityMultiplier() {
        return this.fuelCapacityMultiplier;
    }

    @Override
    public void setFuelCapacityMultiplier(float value) {
        this.fuelCapacityMultiplier = value;
    }

    @Override
    public float getHeatCapacityMultiplier() {
        return this.heatCapacityMultiplier;
    }

    @Override
    public void setHeatCapacityMultiplier(float value) {
        this.heatCapacityMultiplier = value;
    }

    @Override
    public float getConsumptionAccumulator() {
        return getApoExData().getFloat("consumptionAccumulator");
    }

    @Override
    public void setConsumptionAccumulator(float value) {
        getApoExData().putFloat("consumptionAccumulator", value);
    }

    @Override
    public IEnergyContainer getEnergyContainer() {
        List<IEnergyContainer> containers = this.getEnergyContainers(null);
        if (containers.isEmpty()) {
            return null;
        }
        return containers.get(0);
    }

    @Inject(method = "load", at = @At("HEAD"), remap = true)
    private void apoex_load(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("apoex_data")) {
            this.apoexData = nbt.getCompound("apoex_data");
            this.tickSpeedMult = this.apoexData.getFloat("tick_speed_mult");
            this.outputMultiplier = this.apoexData.getFloat("output_multiplier");
            this.inputReduction = this.apoexData.getFloat("input_reduction");
            this.storedOutputFraction = this.apoexData.getFloat("stored_output_fraction");
            this.energyCapacityMult = this.apoexData.getFloat("energy_capacity_mult");
            this.energyEfficiencyMult = this.apoexData.getFloat("energy_efficiency_mult");

            this.generationMultiplier = this.apoexData.getFloat("generation_multiplier");
            this.fuelEfficiency = this.apoexData.getFloat("fuel_efficiency");
            this.heatEfficiency = this.apoexData.getFloat("heat_efficiency");
            this.storedFuelFraction = this.apoexData.getFloat("stored_fuel_fraction");
            this.fuelCapacityMultiplier = this.apoexData.getFloat("fuel_capacity_multiplier");
            this.heatCapacityMultiplier = this.apoexData.getFloat("heat_capacity_multiplier");

            // Update capacity before loading tank data
            this.updateCapacity();

            if ((Object) this instanceof ITileUpgradable upgradable) {
                upgradable.recalculateUpgrades(mekanism.api.Upgrade.ENERGY);
            }
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"), remap = true)
    private void apoex_saveAdditional(CompoundTag nbt, CallbackInfo ci) {
        if (!this.apoexData.isEmpty()) {
            this.apoexData.putFloat("tick_speed_mult", this.tickSpeedMult);
            this.apoexData.putFloat("output_multiplier", this.outputMultiplier);
            this.apoexData.putFloat("input_reduction", this.inputReduction);
            this.apoexData.putFloat("stored_output_fraction", this.storedOutputFraction);
            this.apoexData.putFloat("energy_capacity_mult", this.energyCapacityMult);
            this.apoexData.putFloat("energy_efficiency_mult", this.energyEfficiencyMult);

            this.apoexData.putFloat("generation_multiplier", this.generationMultiplier);
            this.apoexData.putFloat("fuel_efficiency", this.fuelEfficiency);
            this.apoexData.putFloat("heat_efficiency", this.heatEfficiency);
            this.apoexData.putFloat("stored_fuel_fraction", this.storedFuelFraction);
            this.apoexData.putFloat("fuel_capacity_multiplier", this.fuelCapacityMultiplier);
            this.apoexData.putFloat("heat_capacity_multiplier", this.heatCapacityMultiplier);
            nbt.put("apoex_data", this.apoexData);
        }
    }

    @Inject(method = "getReducedUpdateTag", at = @At("RETURN"), remap = false)
    private void apoex_getReducedUpdateTag(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        if (!this.apoexData.isEmpty()) {
            // Ensure data is up to date in apoexData
            this.apoexData.putFloat("tick_speed_mult", this.tickSpeedMult);
            this.apoexData.putFloat("output_multiplier", this.outputMultiplier);
            this.apoexData.putFloat("input_reduction", this.inputReduction);
            this.apoexData.putFloat("stored_output_fraction", this.storedOutputFraction);
            this.apoexData.putFloat("energy_capacity_mult", this.energyCapacityMult);
            this.apoexData.putFloat("energy_efficiency_mult", this.energyEfficiencyMult);

            this.apoexData.putFloat("generation_multiplier", this.generationMultiplier);
            this.apoexData.putFloat("fuel_efficiency", this.fuelEfficiency);
            this.apoexData.putFloat("heat_efficiency", this.heatEfficiency);
            this.apoexData.putFloat("stored_fuel_fraction", this.storedFuelFraction);
            this.apoexData.putFloat("fuel_capacity_multiplier", this.fuelCapacityMultiplier);
            this.apoexData.putFloat("heat_capacity_multiplier", this.heatCapacityMultiplier);

            tag.put("apoex_data", this.apoexData);
        }
    }

    @Inject(method = "handleUpdateTag", at = @At("TAIL"), remap = false)
    private void apoex_handleUpdateTag(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("apoex_data")) {
            this.apoexData = tag.getCompound("apoex_data");
            this.tickSpeedMult = this.apoexData.getFloat("tick_speed_mult");
            this.outputMultiplier = this.apoexData.getFloat("output_multiplier");
            this.inputReduction = this.apoexData.getFloat("input_reduction");
            this.storedOutputFraction = this.apoexData.getFloat("stored_output_fraction");
            this.energyCapacityMult = this.apoexData.getFloat("energy_capacity_mult");
            this.energyEfficiencyMult = this.apoexData.getFloat("energy_efficiency_mult");

            this.generationMultiplier = this.apoexData.getFloat("generation_multiplier");
            this.fuelEfficiency = this.apoexData.getFloat("fuel_efficiency");
            this.heatEfficiency = this.apoexData.getFloat("heat_efficiency");
            this.storedFuelFraction = this.apoexData.getFloat("stored_fuel_fraction");
            this.fuelCapacityMultiplier = this.apoexData.getFloat("fuel_capacity_multiplier");
            this.heatCapacityMultiplier = this.apoexData.getFloat("heat_capacity_multiplier");
            
            // Update capacity on client side
            this.updateCapacity();
        }
    }

    @Inject(method = "tickServer", at = @At("HEAD"), cancellable = true)
    private static void apoex_tickServer(Level level, BlockPos pos, BlockState state, TileEntityMekanism tile, CallbackInfo ci) {
        if (tile instanceof IApoExMekanism mekTile) {

            if (mekTile.isApoExTicking()) {
                return;
            }

            ApoExContext.MEKANISM_TILE.set(mekTile);

            float tickSpeed = mekTile.getTickSpeedMult();
            if (tickSpeed > 0) {
                mekTile.setApoExTicking(true);
                try {
                    int totalTicks = 1 + (int) tickSpeed;
                    float partial = tickSpeed - (int) tickSpeed;

                    // Optimization for TileEntityRecipeMachine
                    if (tile instanceof TileEntityRecipeMachine) {
                        // Run normal tick once
                        TileEntityMekanism.tickServer(level, pos, state, tile);

                        // Run recipe processing loop for the remaining ticks
                        RecipeCacheLookupMonitor<?> monitor = ((TileEntityRecipeMachineAccessor) tile).getRecipeCacheLookupMonitor();
                        
                        int extraLoops = (int) tickSpeed;
                        if (partial > 0 && level.random.nextFloat() < partial) {
                            extraLoops++;
                        }

                        // Limit loop count to prevent extreme lag
                        int maxLoops = 64; 
                        if (extraLoops > maxLoops) {
                            // If we exceed max loops, we just cap it. 
                            // Ideally we would multiply the effect, but that's hard with Mekanism's structure.
                            extraLoops = maxLoops;
                        }

                        for (int i = 0; i < extraLoops; i++) {
                            monitor.updateAndProcess();
                        }
                    } else {
                        // Fallback for other machines
                        int loopCount = totalTicks;
                        if (partial > 0 && level.random.nextFloat() < partial) {
                            loopCount++;
                        }
                        
                        // Limit loop count here as well
                        int maxLoops = 64;
                        if (loopCount > maxLoops) {
                            loopCount = maxLoops;
                        }

                        for (int i = 0; i < loopCount; i++) {
                            TileEntityMekanism.tickServer(level, pos, state, tile);
                        }
                    }

                    ci.cancel();
                } finally {
                    mekTile.setApoExTicking(false);
                    ApoExContext.MEKANISM_TILE.remove();
                }
            }
        }
    }

    @Inject(method = "tickServer", at = @At("TAIL"))
    private static void apoex_tickServerTail(Level level, BlockPos pos, BlockState state, TileEntityMekanism tile, CallbackInfo ci) {
        if (tile instanceof IApoExMekanism mekTile && mekTile.isApoExTicking()) {
            return;
        }
        ApoExContext.MEKANISM_TILE.remove();
    }
}
