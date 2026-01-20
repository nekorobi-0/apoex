package com.youtyan.apoex.mixin.mekanism.multiblock;

import com.youtyan.apoex.IApoExMultiblock;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.common.content.sps.SPSMultiblockData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.LongSupplier;

@Mixin(value = SPSMultiblockData.class, remap = false)
public abstract class MixinSPSMultiblockData {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;input(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/chemical/attribute/ChemicalAttributeValidator;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;"), index = 1)
    private LongSupplier apoex_modifyInputCapacity(LongSupplier original) {
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

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;output(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/chemical/attribute/ChemicalAttributeValidator;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;"), index = 1)
    private LongSupplier apoex_modifyOutputCapacity(LongSupplier original) {
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

    @Redirect(method = "process", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/IGasTank;shrinkStack(JLmekanism/api/Action;)J"))
    private long apoex_redirectShrinkStack(IGasTank tank, long operations, Action action) {
        if (this instanceof IApoExMultiblock multiblock) {
            float efficiency = multiblock.getFuelEfficiency();
            if (efficiency > 0) {
                long actualConsumption = (long) (operations * (1.0F - efficiency));
                actualConsumption = Math.max(0, actualConsumption);
                tank.shrinkStack(actualConsumption, action);
                return operations;
            }
        }
        return tank.shrinkStack(operations, action);
    }

    @ModifyArg(method = "process", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/IGasTank;insert(Lmekanism/api/chemical/ChemicalStack;Lmekanism/api/Action;Lmekanism/api/AutomationType;)Lmekanism/api/chemical/ChemicalStack;"), index = 0)
    private ChemicalStack apoex_insertOutput(ChemicalStack stack) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            if (mult > 0) {
                stack.setAmount((long) (stack.getAmount() * (1.0F + mult)));
            }
        }
        return stack;
    }
}