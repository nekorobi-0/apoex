package com.youtyan.apoex.mixin.emextras;

import com.youtyan.apoex.IApoExMekanism;
import io.github.masyumero.emextras.common.tile.factory.TileEntityEMExtraFactory;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = TileEntityEMExtraFactory.class, remap = false)
public class MixinTileEntityEMExtraFactory implements IApoExMekanism {

    @Unique
    private CompoundTag apoexData = new CompoundTag();

    @Override
    public CompoundTag getApoExData() {
        return this.apoexData;
    }

    @Override
    public void setApoExData(CompoundTag tag) {
        this.apoexData = tag;
    }

    // IApoExMekanismの他のメソッドのデフォルト実装
    // (MixinTileEntityMekanismからコピー)
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
        // TileEntityEMExtraFactoryにはonUpdateServerがないため、何もしない
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
    public float getConsumptionAccumulator() {
        return getApoExData().getFloat("consumptionAccumulator");
    }

    @Override
    public void setConsumptionAccumulator(float value) {
        getApoExData().putFloat("consumptionAccumulator", value);
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
    public mekanism.api.energy.IEnergyContainer getEnergyContainer() {
        return null; // TileEntityEMExtraFactoryにはgetEnergyContainersがないため、nullを返す
    }

    @Override
    public void sendUpdatePacket() {
        // TileEntityEMExtraFactoryにはsendUpdatePacketがないため、何もしない
    }
}
