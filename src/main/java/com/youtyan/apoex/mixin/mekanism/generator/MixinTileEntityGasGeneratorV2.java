package com.youtyan.apoex.mixin.mekanism.generator;

import com.youtyan.apoex.IApoExGenerator;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.chemical.variable.VariableCapacityChemicalTankBuilder;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.tile.TileEntityGasGenerator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityGasGenerator.class, remap = false)
public abstract class MixinTileEntityGasGeneratorV2 extends mekanism.generators.common.tile.TileEntityGenerator {

    @Shadow
    private FloatingLong generationRate;
    
    // Shadowing FuelTank inner class is hard, so we overwrite getInitialGasTanks to use a custom tank or modify capacity
    // But FuelTank is a private inner class in TileEntityGasGenerator.
    // We can't easily instantiate it here.
    
    // However, we can modify the capacity in the constructor of FuelTank if we mixin into it?
    // Or we can use @Redirect on the constructor call in getInitialGasTanks?
    // But FuelTank constructor is protected/private.
    
    // Let's try to modify the capacity passed to super constructor of FuelTank?
    // No, FuelTank constructor calls super with MekanismGeneratorsConfig.generators.gbgTankCapacity.
    
    // We can overwrite getInitialGasTanks and use reflection to set capacity?
    // Or we can use @ModifyArg on the FuelTank constructor call?
    // But FuelTank is an inner class, so the constructor call is implicit or explicit.
    
    // TileEntityGasGenerator.java:
    // builder.addTank(fuelTank = new FuelTank(listener), ...);
    
    // FuelTank constructor:
    // super(MekanismGeneratorsConfig.generators.gbgTankCapacity, ...);
    
    // We can mixin into FuelTank and modify the capacity passed to super.
    // But we already had issues with inner class mixins.
    
    // Alternative: After fuelTank is assigned, we can change its capacity.
    // VariableCapacityGasTank has setCapacity? No, it's final usually?
    // VariableCapacityGasTank extends BasicGasTank.
    // BasicGasTank has protected setCapacity? No.
    
    // Wait, VariableCapacityGasTank is designed to have variable capacity?
    // No, it just implements IConfigurable.
    
    // Let's check BasicChemicalTank.
    // It has `protected long capacity`.
    
    // If we can't change capacity easily, maybe we can wrap the tank?
    
    // Let's try to use @Redirect on the FuelTank constructor call in getInitialGasTanks.
    // But we can't return a new FuelTank because it's private.
    
    // Let's go back to MixinTileEntityGasGeneratorFuelTank approach but fix the target issue?
    // The issue was "Redirector ... failed injection check".
    
    // Let's try to use @ModifyConstant in FuelTank constructor?
    // But we need access to the tile entity to get the multiplier.
    // Inner class has access to outer instance.
    
    // Let's try to mixin into TileEntityGasGenerator$FuelTank again, but use @Inject on constructor.
    
    public MixinTileEntityGasGeneratorV2() {
        super(null, null, null, null);
    }

    @Inject(method = "getToUse", at = @At("RETURN"), cancellable = true)
    private void apoex_getToUse(CallbackInfoReturnable<Long> cir) {
        // getToUse は消費量を計算するが、発電量にも影響する
        // ここでは何もしない
    }

    @Inject(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/generators/common/tile/TileEntityGasGenerator;getToUse()J"))
    private void apoex_onUpdateServer_preGetToUse(CallbackInfo ci) {
        if (this instanceof IApoExGenerator gen) {
            // generationRate has been set by the lambda just before this

            float mult = gen.getGenerationMultiplier();
            if (mult > 0) {
                generationRate = generationRate.multiply(1.0F + mult);
            }

            // 燃料効率: 1tickごとの消費を減らすのではなく、何tickごとに消費にする
            // つまり、エネルギー密度を上げる
            float efficiency = gen.getFuelEfficiency();
            if (efficiency > 0 && efficiency < 1.0F) {
                float effMult = 1.0F / (1.0F - efficiency);
                generationRate = generationRate.multiply(effMult);
            }
        }
    }

    @Inject(method = "getGenerationRate", at = @At("RETURN"), cancellable = true)
    private void apoex_getGenerationRate(CallbackInfoReturnable<FloatingLong> cir) {
        if (this instanceof IApoExGenerator gen) {
            float mult = gen.getGenerationMultiplier();
            if (mult > 0) {
                // クライアント側での表示補正
                Level level = ((BlockEntity)(Object)this).getLevel();
                if (level != null && level.isClientSide) {
                    FloatingLong rate = cir.getReturnValue();
                    rate = rate.multiply(1.0F + mult);

                    float efficiency = gen.getFuelEfficiency();
                    if (efficiency > 0 && efficiency < 1.0F) {
                        float effMult = 1.0F / (1.0F - efficiency);
                        rate = rate.multiply(effMult);
                    }
                    cir.setReturnValue(rate);
                }
            }
        }
    }
}