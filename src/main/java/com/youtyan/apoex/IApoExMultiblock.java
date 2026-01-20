package com.youtyan.apoex;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.Map;
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

    Map<DynamicHolder<? extends Affix>, AffixInstance> getAffixes();

    default void updateHeatCapacity() {}
    default void updateCapacity() {}

    default float getConsumptionAccumulator() { return 100F; }
    default void setConsumptionAccumulator(float value) {}
}
