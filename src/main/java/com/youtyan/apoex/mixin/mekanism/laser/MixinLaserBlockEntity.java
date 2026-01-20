package com.youtyan.apoex.mixin.mekanism.laser;


import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.tile.laser.TileEntityLaser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/*

@Mixin(value = TileEntityLaser.class, remap = false)
public abstract class MixinLaserBlockEntity {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/energy/BasicEnergyContainer;create(Lmekanism/api/math/FloatingLong;Lmekanism/api/IContentsListener;)Lmekanism/common/capabilities/energy/BasicEnergyContainer;"))
    private BasicEnergyContainer apoex_modifyEnergyContainer(FloatingLong maxEnergy, mekanism.api.IContentsListener listener) {
        return BasicEnergyContainer.create(maxEnergy, () -> {
            if (listener instanceof IApoExMekanism mekTile) {
                float mult = mekTile.getEnergyCapacityMultiplier();
                if (mult > 0 && mekTile.getEnergyContainer() != null) {
                    mekTile.getEnergyContainer().setEnergy(mekTile.getEnergyContainer().getEnergy().multiply(1.0F + mult));
                }
            }
            if (listener != null) {
                listener.onContentsChanged();
            }
        });
    }
}

 */
