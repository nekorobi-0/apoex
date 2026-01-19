package com.youtyan.apoex.mixin.mekanismgenerators.generator;

import com.youtyan.apoex.IApoExGenerator;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.GeneratorTags;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.slot.FluidFuelInventorySlot;
import mekanism.generators.common.tile.TileEntityBioGenerator;
import mekanism.generators.common.tile.TileEntityGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityBioGenerator.class, remap = false)
public abstract class MixinTileEntityBioGenerator extends TileEntityGenerator {

    @Shadow public BasicFluidTank bioFuelTank;
    @Shadow FluidFuelInventorySlot fuelSlot;
    @Shadow EnergyInventorySlot energySlot;
    @Shadow private float lastFluidScale;

    // Dummy constructor to satisfy Java compiler
    public MixinTileEntityBioGenerator() {
        super(null, null, null, null);
    }

    @Overwrite
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSide(this::getDirection);
        // Apply fuel capacity multiplier
        int capacity = MekanismGeneratorsConfig.generators.bioTankCapacity.get();
        if (this instanceof IApoExGenerator gen) {
            float mult = gen.getFuelCapacityMultiplier();
            if (mult > 0) {
                capacity = (int) (capacity * (1.0F + mult));
            }
        }
        
        builder.addTank(bioFuelTank = VariableCapacityFluidTank.input(capacity,
                    fluidStack -> GeneratorTags.Fluids.BIOETHANOL_LOOKUP.contains(fluidStack.getFluid()), listener), mekanism.api.RelativeSide.LEFT, mekanism.api.RelativeSide.RIGHT,
              mekanism.api.RelativeSide.BACK, mekanism.api.RelativeSide.TOP, mekanism.api.RelativeSide.BOTTOM);
        return builder.build();
    }

    /**
     * @author youtyan
     * @reason Support ApoEx fuel efficiency
     */
    @Overwrite
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.drainContainer();
        fuelSlot.fillOrBurn();
        if (MekanismUtils.canFunction(this) && !bioFuelTank.isEmpty() &&
                getEnergyContainer().insert(MekanismGeneratorsConfig.generators.bioGeneration.get(), Action.SIMULATE, AutomationType.INTERNAL).isZero()) {
            setActive(true);

            int amount = 1;
            if (this instanceof IApoExGenerator gen) {
                float efficiency = gen.getFuelEfficiency();
                if (efficiency > 0 && efficiency < 1.0F) {
                    float storedFraction = gen.getStoredFuelFraction();
                    float consumeRate = 1.0F - efficiency;
                    storedFraction += consumeRate;

                    if (storedFraction >= 1.0F) {
                        storedFraction -= 1.0F;
                        gen.setStoredFuelFraction(storedFraction);
                    } else {
                        gen.setStoredFuelFraction(storedFraction);
                        amount = 0;
                    }
                }
            }

            if (amount > 0) {
                MekanismUtils.logMismatchedStackSize(bioFuelTank.shrinkStack(amount, Action.EXECUTE), amount);
            }

            getEnergyContainer().insert(MekanismGeneratorsConfig.generators.bioGeneration.get(), Action.EXECUTE, AutomationType.INTERNAL);
            float fluidScale = MekanismUtils.getScale(lastFluidScale, bioFuelTank);
            if (fluidScale != lastFluidScale) {
                lastFluidScale = fluidScale;
                sendUpdatePacket();
            }
        } else {
            setActive(false);
        }
    }

    @Inject(method = "getProductionRate", at = @At("RETURN"), cancellable = true)
    private void apoex_getProductionRate(CallbackInfoReturnable<FloatingLong> cir) {
        if (this instanceof IApoExGenerator gen) {
            float mult = gen.getGenerationMultiplier();
            if (mult > 0) {
                cir.setReturnValue(cir.getReturnValue().multiply(1.0F + mult));
            }
        }
    }
}
