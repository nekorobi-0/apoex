package com.youtyan.apoex.mixin.mekanismgenerators.generator.multiblock;

import com.youtyan.apoex.IApoExMultiblock;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.heat.VariableHeatCapacitor;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

@Mixin(value = FissionReactorMultiblockData.class, remap = false)
public abstract class MixinFissionReactorMultiblockData extends MultiblockData implements IApoExMultiblock {


    @Shadow public double lastBurnRate;
    @Shadow public IGasTank fuelTank;
    @Shadow public VariableHeatCapacitor heatCapacitor;
    @Shadow private IGasTank heatedCoolantTank;
    @Shadow private IGasTank wasteTank;

    public MixinFissionReactorMultiblockData(net.minecraft.world.level.block.entity.BlockEntity tile) {
        super(tile);
    }

    @Redirect(method = "burnFuel", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(DD)D", ordinal = 1))
    private double apoex_modifyBurnRateLimit(double a, double b) {
        return Math.min(a, ((FissionReactorMultiblockData)(Object)this).getMaxBurnRate());
    }

    @Inject(method = "onCreated", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/heat/VariableHeatCapacitor;setHeatCapacity(DZ)V", shift = At.Shift.BEFORE))
    private void apoex_onCreated_beforeSetHeatCapacity(Level world, CallbackInfo ci) {
        this.recalculate(world, this.locations);
    }

    @Redirect(method = "onCreated", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/heat/VariableHeatCapacitor;setHeatCapacity(DZ)V"))
    private void apoex_redirectSetHeatCapacity(VariableHeatCapacitor instance, double capacity, boolean updateHeat) {
        float mult = this.getHeatCapacityMultiplier();
        double newCapacity = capacity;
        if (mult > 0) {
            newCapacity *= (1.0 + mult);
        }
        instance.setHeatCapacity(newCapacity, updateHeat);
    }

    @Override
    public void updateHeatCapacity() {
    }

    //<editor-fold desc="Other Affix Implementations">
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/heat/VariableHeatCapacitor;create(DLjava/util/function/DoubleSupplier;Ljava/util/function/DoubleSupplier;Ljava/util/function/DoubleSupplier;Lmekanism/api/IContentsListener;)Lmekanism/common/capabilities/heat/VariableHeatCapacitor;"), index = 2)
    private DoubleSupplier apoex_modifyInsulation(DoubleSupplier insulation) {
        return () -> {
            double ins = insulation.getAsDouble();
            float mult = this.getHeatEfficiency();
            if (mult > 0) {
                return ins * (1.0F + mult);
            }
            return ins;
        };
    }

    // 消費量減少 (toBurnを減らす)
    @ModifyVariable(method = "burnFuel", at = @At(value = "STORE"), name = "toBurn")
    private double apoex_modifyToBurn(double toBurn) {
        float efficiency = this.getFuelEfficiency();
        if (efficiency > 0) {
            // 実際に消費する量を減らす
            return toBurn * (1.0F - efficiency);
        }
        return toBurn;
    }

    // 熱生成補正 & 生産量増加
    @ModifyArg(method = "burnFuel", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/heat/VariableHeatCapacitor;handleHeat(D)V"), index = 0)
    private double apoex_modifyHeatGeneration(double heat) {
        float efficiency = this.getFuelEfficiency();
        float genMult = this.getGenerationMultiplier();
        
        // toBurnが減らされているので、元の熱量に戻すための補正
        double correction = (efficiency > 0 && efficiency < 1.0F) ? (1.0 / (1.0 - efficiency)) : 1.0;
        
        // 生産量増加
        double boost = (genMult > 0) ? (1.0 + genMult) : 1.0;
        
        return heat * correction * boost;
    }

    @Inject(method = "getMaxBurnRate", at = @At("RETURN"), cancellable = true)
    private void apoex_getMaxBurnRate(CallbackInfoReturnable<Long> cir) {
        long originalMax = cir.getReturnValue();
        float mult = this.getFuelCapacityMultiplier();
        if (mult > 0) {
            long newMax = (long) (originalMax * (1.0F + mult));
            cir.setReturnValue(newMax);
        }
    }

    @Redirect(method = "burnFuel", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/IGasTank;insert(Lmekanism/api/chemical/ChemicalStack;Lmekanism/api/Action;Lmekanism/api/AutomationType;)Lmekanism/api/chemical/ChemicalStack;"))
    private ChemicalStack apoex_modifyReactorOutput(IGasTank tank, ChemicalStack stack, Action action, AutomationType type) {
        if (tank == this.heatedCoolantTank || tank == this.wasteTank) {
            float mult = this.getGenerationMultiplier();
            if (mult > 0) {
                stack.setAmount((long) (stack.getAmount() * (1.0F + mult)));
            }
        }
        return tank.insert((GasStack) stack, action, type);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;input(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;", ordinal = 0), index = 1)
    private LongSupplier apoex_modifyGasCoolantCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            float mult = this.getFuelCapacityMultiplier();
            if (mult > 0) {
                double calculated = cap * (1.0F + mult);
                if (Double.isInfinite(calculated) || Double.isNaN(calculated)) {
                    return Long.MAX_VALUE;
                }
                return (long) Math.min(calculated, Long.MAX_VALUE);
            }
            return cap;
        };
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;input(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/chemical/attribute/ChemicalAttributeValidator;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;", ordinal = 0), index = 1)
    private LongSupplier apoex_modifyFuelCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            float mult = this.getFuelCapacityMultiplier();
            if (mult > 0) {
                double calculated = cap * (1.0F + mult);
                if (Double.isInfinite(calculated) || Double.isNaN(calculated)) {
                    return Long.MAX_VALUE;
                }
                return (long) Math.min(calculated, Long.MAX_VALUE);
            }
            return cap;
        };
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/fluid/VariableCapacityFluidTank;input(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/IntSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/common/capabilities/fluid/VariableCapacityFluidTank;"))
    private VariableCapacityFluidTank apoex_modifyCoolantCapacity(MultiblockData multiblockData, IntSupplier capacity, Predicate<FluidStack> validator, IContentsListener listener) {
        IntSupplier newCapacity = () -> {
            int cap = capacity.getAsInt();
            float mult = this.getFuelCapacityMultiplier();
            if (mult > 0) {
                double calculated = cap * (1.0F + mult);
                if (Double.isInfinite(calculated) || Double.isNaN(calculated)) {
                    return Integer.MAX_VALUE;
                }
                return (int) Math.min(calculated, Integer.MAX_VALUE);
            }
            return cap;
        };
        return VariableCapacityFluidTank.input(multiblockData, newCapacity, validator, listener);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;output(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;", ordinal = 0), index = 1)
    private LongSupplier apoex_modifyHeatedCoolantCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            float mult = this.getFuelCapacityMultiplier();
            if (mult > 0) {
                double calculated = cap * (1.0F + mult);
                if (Double.isInfinite(calculated) || Double.isNaN(calculated)) {
                    return Long.MAX_VALUE;
                }
                return (long) Math.min(calculated, Long.MAX_VALUE);
            }
            return cap;
        };
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;output(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/chemical/attribute/ChemicalAttributeValidator;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;", ordinal = 0), index = 1)
    private LongSupplier apoex_modifyWasteCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            float mult = this.getFuelCapacityMultiplier();
            if (mult > 0) {
                double calculated = cap * (1.0F + mult);
                if (Double.isInfinite(calculated) || Double.isNaN(calculated)) {
                    return Long.MAX_VALUE;
                }
                return (long) Math.min(calculated, Long.MAX_VALUE);
            }
            return cap;
        };
    }

    @ModifyVariable(method = "handleDamage", at = @At(value = "STORE", ordinal = 0), name = "damageRate")
    private double apoex_modifyDamageRate(double damageRate) {
        float resistance = this.getDamageResistance();
        if (resistance > 0) {
            return damageRate * Math.max(0, 1.0F - resistance);
        }
        return damageRate;
    }

    @Redirect(method = "handleDamage", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/heat/VariableHeatCapacitor;getTemperature()D"))
    private double apoex_getTemperatureForDamage(VariableHeatCapacitor instance) {
        double temp = instance.getTemperature();
        float resistance = this.getDamageResistance();
        if (resistance > 0) {
            return temp / (1.0F + resistance);
        }
        return temp;
    }
    //</editor-fold>
}
