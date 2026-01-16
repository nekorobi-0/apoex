package com.youtyan.apoex.mixin.mekanism.generator;

import com.youtyan.apoex.IApoExMultiblock;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.common.capabilities.chemical.multiblock.MultiblockChemicalTankBuilder;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

@Mixin(value = FusionReactorMultiblockData.class, remap = false)
public abstract class MixinFusionReactorMultiblockData {

    @ModifyVariable(method = "burnFuel", at = @At(value = "STORE"), name = "fuelBurned")
    private long apoex_modifyFuelBurned(long fuelBurned) {
        if (this instanceof IApoExMultiblock multiblock) {
            float efficiency = multiblock.getFuelEfficiency();
            if (efficiency > 0 && efficiency < 1.0F) {
                float storedFraction = multiblock.getStoredFuelFraction();
                float consumeRate = 1.0F - efficiency;
                storedFraction += consumeRate;

                if (storedFraction >= 1.0F) {
                    storedFraction -= 1.0F;
                    multiblock.setStoredFuelFraction(storedFraction);
                    return fuelBurned;
                } else {
                    multiblock.setStoredFuelFraction(storedFraction);
                    return 0;
                }
            }
        }
        return fuelBurned;
    }

    @Redirect(method = "burnFuel", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/IGasTank;shrinkStack(JLmekanism/api/Action;)J"))
    private long apoex_redirectShrinkStack(IGasTank instance, long amount, Action action) {
        return instance.shrinkStack(amount, action);
    }

    @Inject(method = "getPassiveGeneration(ZZ)Lmekanism/api/math/FloatingLong;", at = @At("RETURN"), cancellable = true)
    private void apoex_getPassiveGeneration(boolean active, boolean current, CallbackInfoReturnable<FloatingLong> cir) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            if (mult > 0) {
                cir.setReturnValue(cir.getReturnValue().multiply(1.0F + mult));
            }
        }
    }

    @ModifyArg(method = "transferHeat", at = @At(value = "INVOKE", target = "Lmekanism/api/energy/IEnergyContainer;insert(Lmekanism/api/math/FloatingLong;Lmekanism/api/Action;Lmekanism/api/AutomationType;)Lmekanism/api/math/FloatingLong;"), index = 0)
    private FloatingLong apoex_insertEnergy(FloatingLong amount) {
        FusionReactorMultiblockData self = (FusionReactorMultiblockData) (Object) this;
        if (self instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            if (mult > 0) {
                FloatingLong newAmount = amount.multiply(1.0F + mult);
                return newAmount.min(self.energyContainer.getNeeded());
            }
        }
        return amount.min(self.energyContainer.getNeeded());
    }

    @ModifyArg(method = "transferHeat", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/IGasTank;insert(Lmekanism/api/chemical/ChemicalStack;Lmekanism/api/Action;Lmekanism/api/AutomationType;)Lmekanism/api/chemical/ChemicalStack;"), index = 0)
    private ChemicalStack apoex_insertSteam(ChemicalStack stack) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            if (mult > 0) {
                double calculated = stack.getAmount() * (1.0F + mult);
                if (Double.isInfinite(calculated) || Double.isNaN(calculated)) {
                    stack.setAmount(Long.MAX_VALUE);
                } else {
                    stack.setAmount((long) Math.min(calculated, Long.MAX_VALUE));
                }
            }
        }
        return stack;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;input(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;"), index = 1)
    private LongSupplier apoex_modifyGasCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            if (this instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getFuelCapacityMultiplier();
                if (mult > 0) {
                    double calculated = cap * (1.0F + mult);
                    if (Double.isInfinite(calculated) || Double.isNaN(calculated)) {
                        return Long.MAX_VALUE;
                    }
                    return (long) Math.min(calculated, Long.MAX_VALUE);
                }
            }
            return cap;
        };
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;output(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;"), index = 1)
    private LongSupplier apoex_modifySteamCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            if (this instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getFuelCapacityMultiplier();
                if (mult > 0) {
                    double calculated = cap * (1.0F + mult);
                    if (Double.isInfinite(calculated) || Double.isNaN(calculated)) {
                        return Long.MAX_VALUE;
                    }
                    return (long) Math.min(calculated, Long.MAX_VALUE);
                }
            }
            return cap;
        };
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/fluid/VariableCapacityFluidTank;input(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/IntSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/common/capabilities/fluid/VariableCapacityFluidTank;"))
    private VariableCapacityFluidTank apoex_modifyWaterCapacity(MultiblockData multiblockData, IntSupplier capacity, Predicate<FluidStack> validator, IContentsListener listener) {
        IntSupplier newCapacity = () -> {
            int cap = capacity.getAsInt();
            if (listener instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getFuelCapacityMultiplier();
                if (mult > 0) {
                    double calculated = cap * (1.0F + mult);
                    if (Double.isInfinite(calculated) || Double.isNaN(calculated)) {
                        return Integer.MAX_VALUE;
                    }
                    return (int) Math.min(calculated, Integer.MAX_VALUE);
                }
            }
            return cap;
        };
        return VariableCapacityFluidTank.input(multiblockData, newCapacity, validator, listener);
    }

    @Redirect(method = "burnFuel", at = @At(value = "INVOKE", target = "Lmekanism/generators/common/content/fusion/FusionReactorMultiblockData;setPlasmaTemp(D)V"))
    private void apoex_modifyPlasmaTemp(FusionReactorMultiblockData instance, double temp) {
        double originalTempIncrease = temp - instance.getPlasmaTemp();
        double modifiedTempIncrease = originalTempIncrease;
        if (instance instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getHeatEfficiency();
            if (mult > 0) {
                modifiedTempIncrease *= (1.0F + mult);
            }
        }
        double newTemp = instance.getPlasmaTemp() + modifiedTempIncrease;
        
        // 制限を撤廃し、自然な計算結果に任せる
        instance.setPlasmaTemp(Math.max(0, newTemp));
    }

    @ModifyVariable(method = "addTemperatureFromEnergyInput", at = @At("HEAD"), argsOnly = true)
    private FloatingLong apoex_modifyEnergyInput(FloatingLong energyAdded) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getHeatEfficiency();
            if (mult > 0) {
                // 外部からの加熱も効率アップ
                return energyAdded.multiply(1.0F + mult);
            }
        }
        return energyAdded;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/energy/VariableCapacityEnergyContainer;output(Lmekanism/api/math/FloatingLongSupplier;Lmekanism/api/IContentsListener;)Lmekanism/common/capabilities/energy/VariableCapacityEnergyContainer;"))
    private VariableCapacityEnergyContainer apoex_modifyEnergyContainer(FloatingLongSupplier maxEnergy, IContentsListener listener) {
        FloatingLongSupplier newMaxEnergy = () -> {
            FloatingLong original = maxEnergy.get();
            if (listener instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getEnergyCapacityMultiplier();
                if (mult > 0) {
                    return original.multiply(1.0F + mult);
                }
            }
            return original;
        };
        return VariableCapacityEnergyContainer.output(newMaxEnergy, listener);
    }
}