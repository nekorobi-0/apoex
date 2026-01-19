package com.youtyan.apoex.mixin.mekanism.multiblock;

import com.youtyan.apoex.IApoExMultiblock;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableFloat;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityMultiblock.class, remap = false)
public abstract class MixinTileEntityMultiblock {

    @Inject(method = "structureChanged", at = @At("RETURN"))
    private void apoex_structureChanged(MultiblockData multiblock, CallbackInfo ci) {
        if (multiblock instanceof IApoExMultiblock apoExMultiblock) {
            net.minecraft.world.level.Level world = ((TileEntityMultiblock)(Object)this).getLevel();
            if (world != null && !world.isClientSide) {
                apoExMultiblock.recalculate(world, multiblock.locations);
                ((TileEntityUpdateable)(Object)this).sendUpdatePacket();
            }
        }
    }

    @Inject(method = "addContainerTrackers", at = @At("TAIL"))
    private void apoex_addContainerTrackers(MekanismContainer container, CallbackInfo ci) {
        MultiblockData multiblock = ((TileEntityMultiblock<?>)(Object)this).getMultiblock();
        if (multiblock instanceof IApoExMultiblock apoExMultiblock) {
            container.track(SyncableFloat.create(
                apoExMultiblock::getGenerationMultiplier,
                apoExMultiblock::setGenerationMultiplier
            ));
            container.track(SyncableFloat.create(
                apoExMultiblock::getFuelEfficiency,
                apoExMultiblock::setFuelEfficiency
            ));
            container.track(SyncableFloat.create(
                apoExMultiblock::getHeatEfficiency,
                apoExMultiblock::setHeatEfficiency
            ));
            container.track(SyncableFloat.create(
                apoExMultiblock::getFuelCapacityMultiplier,
                apoExMultiblock::setFuelCapacityMultiplier
            ));
            container.track(SyncableFloat.create(
                apoExMultiblock::getEnergyCapacityMultiplier,
                apoExMultiblock::setEnergyCapacityMultiplier
            ));
        }
    }
}
