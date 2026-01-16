package com.youtyan.apoex;

import mekanism.api.energy.IEnergyContainer;
import net.minecraft.nbt.CompoundTag;

public interface IApoExMekanism {
    CompoundTag getApoExData();
    void setApoExData(CompoundTag tag);
    float getTickSpeedMult();
    void setTickSpeedMult(float value);
    float getOutputMultiplier();
    void setOutputMultiplier(float value);
    float getInputReduction();
    void setInputReduction(float value);
    void apoex_onUpdateServer();
    boolean isApoExTicking();
    void setApoExTicking(boolean ticking);
    
    float getStoredOutputFraction();
    void setStoredOutputFraction(float value);

    // Methods for consumption skipping
    float getConsumptionAccumulator();
    void setConsumptionAccumulator(float value);

    float getEnergyCapacityMultiplier();
    void setEnergyCapacityMultiplier(float value);
    float getEnergyEfficiencyMultiplier();
    void setEnergyEfficiencyMultiplier(float value);

    IEnergyContainer getEnergyContainer();

    void sendUpdatePacket();
}
