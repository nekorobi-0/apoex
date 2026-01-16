package com.youtyan.apoex.mixin.mekanism;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.mixin.mekanism.accessor.MachineEnergyContainerAccessor;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MachineEnergyContainer.class, remap = false)
public class MixinMachineEnergyContainer {

    @Shadow
    @Final
    private TileEntityMekanism tile;

    @Inject(method = "updateMaxEnergy", at = @At("TAIL"))
    private void apoex_updateMaxEnergy(CallbackInfo ci) {
        if (tile instanceof IApoExMekanism apoExTile) {
            float mult = apoExTile.getEnergyCapacityMultiplier();
            if (mult > 0) {
                MachineEnergyContainerAccessor accessor = (MachineEnergyContainerAccessor) this;
                FloatingLong currentMax = accessor.getCurrentMaxEnergy();
                accessor.setCurrentMaxEnergy(currentMax.multiply(1.0f + mult));
            }
        }
    }

    @Inject(method = "updateEnergyPerTick", at = @At("TAIL"))
    private void apoex_updateEnergyPerTick(CallbackInfo ci) {
        if (tile instanceof IApoExMekanism apoExTile) {
            float efficiencyMult = apoExTile.getEnergyEfficiencyMultiplier();
            if (efficiencyMult > 0) {
                MachineEnergyContainerAccessor accessor = (MachineEnergyContainerAccessor) this;
                FloatingLong currentUsage = accessor.getCurrentEnergyPerTick();
                accessor.setCurrentEnergyPerTick(currentUsage.divide(1.0f + efficiencyMult));
            }
        }
    }
}
