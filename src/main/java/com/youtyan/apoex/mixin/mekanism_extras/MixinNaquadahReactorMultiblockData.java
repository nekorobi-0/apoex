package com.youtyan.apoex.mixin.mekanism_extras;

import com.youtyan.apoex.IApoExMultiblock;
import mekanism.api.Action;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.lib.multiblock.MultiblockData;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

@Mixin(targets = "com.jerry.generator_extras.common.content.reactor.NaquadahReactorMultiblockData", remap = false)
public abstract class MixinNaquadahReactorMultiblockData {

    @Redirect(method = "burnFuel", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/IGasTank;shrinkStack(JLmekanism/api/Action;)J"))
    private long apoex_redirectShrinkStack(IGasTank instance, long amount, Action action) {
        if (this instanceof IApoExMultiblock multiblock) {
            float efficiency = multiblock.getFuelEfficiency();
            if (efficiency > 0) {
                long actualConsumption = (long) (amount * (1.0F - efficiency));
                actualConsumption = Math.max(0, actualConsumption);
                instance.shrinkStack(actualConsumption, action);
                return amount;
            }
        }
        return instance.shrinkStack(amount, action);
    }

    @ModifyArg(method = "burnFuel", at = @At(value = "INVOKE", target = "Lmekanism/api/math/FloatingLong;multiply(J)Lmekanism/api/math/FloatingLong;"))
    private long apoex_modifyBurnFuelEnergy(long fuelBurned) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            if (mult > 0) {
                return (long) (fuelBurned * (1.0F + mult));
            }
        }
        return fuelBurned;
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

    @ModifyArg(method = "addTemperatureFromEnergyInput", at = @At(value = "INVOKE", target = "Lmekanism/api/math/FloatingLong;divide(D)Lmekanism/api/math/FloatingLong;"), index = 0)
    private double apoex_addTemperatureFromEnergyInput(double plasmaHeatCapacity) {
         if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getHeatEfficiency();
            if (mult > 0) {
                return plasmaHeatCapacity / (1.0F + mult);
            }
        }
        return plasmaHeatCapacity;
    }

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;input(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;"
        ),
        index = 1
    )
    private LongSupplier apoex_modifyGasCapacity(LongSupplier originalCapacitySupplier) {
        return () -> {
            long capacity = originalCapacitySupplier.getAsLong();
            if (this instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getFuelCapacityMultiplier();
                if (mult > 0) {
                    return (long) (capacity * (1.0F + mult));
                }
            }
            return capacity;
        };
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;output(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;"), index = 1)
    private LongSupplier apoex_modifySteamCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            if (this instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getFuelCapacityMultiplier();
                if (mult > 0) {
                    return (long) (cap * (1.0F + mult));
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
                    return (int) (cap * (1.0F + mult));
                }
            }
            return cap;
        };
        return VariableCapacityFluidTank.input(multiblockData, newCapacity, validator, listener);
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
