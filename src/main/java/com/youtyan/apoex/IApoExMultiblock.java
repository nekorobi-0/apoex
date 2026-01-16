package com.youtyan.apoex;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.Set;

public interface IApoExMultiblock {
    float getGenerationMultiplier();
    void setGenerationMultiplier(float value);
    float getFuelEfficiency();
    void setFuelEfficiency(float value);
    float getHeatEfficiency();
    void setHeatEfficiency(float value);
    float getFuelCapacityMultiplier();
    void setFuelCapacityMultiplier(float value);

    float getStoredFuelFraction();
    void setStoredFuelFraction(float value);

    float getEnergyCapacityMultiplier();
    void setEnergyCapacityMultiplier(float value);

    float getTickSpeedMultiplier();
    void setTickSpeedMultiplier(float value);

    float getDamageResistance();
    void setDamageResistance(float value);

    float getHeatCapacityMultiplier();
    void setHeatCapacityMultiplier(float value);

    void addAffixesFromTile(IApoExGenerator tile);

    void recalculate(Level world, Set<BlockPos> locations);

    default void updateHeatCapacity() {}
}