package com.youtyan.apoex.mixin.astralmekanism.generator;

import astral_mekanism.block.blockentity.generator.BEHeatGenerator;
import com.youtyan.apoex.IApoExGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = BEHeatGenerator.class, remap = false)
public abstract class MixinBEHeatGenerator implements IApoExGenerator {

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
}