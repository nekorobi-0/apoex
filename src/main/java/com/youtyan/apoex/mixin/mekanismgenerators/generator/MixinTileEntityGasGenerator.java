package com.youtyan.apoex.mixin.mekanismgenerators.generator;

import com.youtyan.apoex.IApoExGenerator;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.FloatingLong;
import mekanism.generators.common.tile.TileEntityGasGenerator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(value = TileEntityGasGenerator.class, remap = false)
public abstract class MixinTileEntityGasGenerator extends mekanism.generators.common.tile.TileEntityGenerator implements IApoExGenerator {

    @Shadow
    private FloatingLong generationRate;

    public MixinTileEntityGasGenerator() {
        super(null, null, null, null);
    }

    @Override
    public void updateCapacity() {
        float mult = this.getFuelCapacityMultiplier();
        if (mult > 0) {
            try {
                Field fuelTankField = TileEntityGasGenerator.class.getDeclaredField("fuelTank");
                fuelTankField.setAccessible(true);
                Object fuelTank = fuelTankField.get(this);

                if (fuelTank != null) {
                    long baseCapacity = mekanism.generators.common.config.MekanismGeneratorsConfig.generators.gbgTankCapacity.get();
                    long newCapacity = (long) (baseCapacity * (1.0F + mult));
                    
                    Field capacityField = mekanism.api.chemical.BasicChemicalTank.class.getDeclaredField("capacity");
                    capacityField.setAccessible(true);
                    
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(capacityField, capacityField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
                    
                    capacityField.setLong(fuelTank, newCapacity);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    @Inject(method = "onUpdateServer", at = @At("HEAD"))
    private void apoex_checkCapacityUpdate(CallbackInfo ci) {
        updateCapacity();
    }

    @Inject(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/generators/common/tile/TileEntityGasGenerator;getToUse()J"))
    private void apoex_onUpdateServer_preGetToUse(CallbackInfo ci) {
        float mult = this.getGenerationMultiplier();
        if (mult > 0) {
            generationRate = generationRate.multiply(1.0F + mult);
        }
    }

    @Redirect(
            method = "onUpdateServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/generators/common/tile/TileEntityGasGenerator$FuelTank;setStack(Lmekanism/api/chemical/gas/GasStack;)V"
            )
    )
    private void apoex_setStack(@Coerce Object instance, GasStack stack) {
        float efficiency = this.getFuelEfficiency();
        if (efficiency > 0 && efficiency < 1.0F) {
            int rate = (int) (100 - (efficiency * 100));
            int counter = (int) this.getConsumptionAccumulator();
            if (counter == 0) counter = 100;

            counter -= rate;

            if (counter <= 0) {
                invokeSetStack(instance, stack);
                counter += 100;
            }

            this.setConsumptionAccumulator((float) counter);
            return;
        }
        invokeSetStack(instance, stack);
    }

    private void invokeSetStack(Object instance, GasStack stack) {
        try {
            if (instance instanceof IGasTank tank) {
                tank.setStack(stack);
            } else {
                Method setStackMethod = instance.getClass().getMethod("setStack", GasStack.class);
                setStackMethod.invoke(instance, stack);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "getGenerationRate", at = @At("RETURN"), cancellable = true)
    private void apoex_getGenerationRate(CallbackInfoReturnable<FloatingLong> cir) {
        updateCapacity();
        float mult = this.getGenerationMultiplier();
        if (mult > 0) {
            Level level = ((BlockEntity)(Object)this).getLevel();
            if (level != null && level.isClientSide) {
                FloatingLong rate = cir.getReturnValue();
                rate = rate.multiply(1.0F + mult);
                cir.setReturnValue(rate);
            }
        }
    }
}
