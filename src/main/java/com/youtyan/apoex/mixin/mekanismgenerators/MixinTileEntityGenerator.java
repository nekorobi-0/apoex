package com.youtyan.apoex.mixin.mekanismgenerators;

import com.youtyan.apoex.IApoExGenerator;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.generators.common.tile.TileEntityGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(value = TileEntityGenerator.class, remap = false)
public abstract class MixinTileEntityGenerator implements IApoExGenerator {

    @Shadow private BasicEnergyContainer energyContainer;

    @Unique
    private FloatingLong apoex_baseMaxEnergy;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void apoex_init(IBlockProvider blockProvider, BlockPos pos, BlockState state, FloatingLongSupplier maxOutput, CallbackInfo ci) {
        if (energyContainer != null) {
            apoex_baseMaxEnergy = energyContainer.getMaxEnergy();
        }
    }

    @Inject(method = "getMaxOutput", at = @At("RETURN"), cancellable = true)
    private void apoex_getMaxOutput(CallbackInfoReturnable<FloatingLong> cir) {
        float mult = this.getGenerationMultiplier();
        if (mult > 0) {
            cir.setReturnValue(cir.getReturnValue().multiply(1.0F + mult));
        }
    }

    @ModifyVariable(method = "updateMaxOutputRaw", at = @At("HEAD"), argsOnly = true)
    private FloatingLong apoex_updateMaxOutputRaw(FloatingLong maxOutput) {
        float mult = this.getGenerationMultiplier();
        if (mult > 0) {
            maxOutput = maxOutput.multiply(1.0F + mult);
        }
        
        float efficiency = this.getFuelEfficiency();
        if (efficiency > 0 && efficiency < 1.0F) {
            float effMult = 1.0F / (1.0F - efficiency);
            maxOutput = maxOutput.multiply(effMult);
        }
        return maxOutput;
    }

    @Override
    public void updateCapacity() {
        float mult = this.getEnergyCapacityMultiplier();
        if (mult > 0) {
            BasicEnergyContainer container = energyContainer;
            if (container != null) {
                if (apoex_baseMaxEnergy == null) {
                     apoex_baseMaxEnergy = container.getMaxEnergy();
                }
                
                FloatingLong newMax = apoex_baseMaxEnergy.multiply(1.0F + mult);
                if (!newMax.equals(container.getMaxEnergy())) {
                    try {
                        Field maxEnergyField = BasicEnergyContainer.class.getDeclaredField("maxEnergy");
                        maxEnergyField.setAccessible(true);
                        maxEnergyField.set(container, newMax);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Inject(method = "onUpdateServer", at = @At("HEAD"))
    private void apoex_onUpdateServer(CallbackInfo ci) {
        updateCapacity();
    }
}
