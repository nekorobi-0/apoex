package com.youtyan.apoex;

public interface IApoExGenerator extends IApoExMekanism {
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

    float getHeatCapacityMultiplier();
    void setHeatCapacityMultiplier(float value);
}