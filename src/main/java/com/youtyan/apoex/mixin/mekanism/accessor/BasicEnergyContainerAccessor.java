package com.youtyan.apoex.mixin.mekanism.accessor;

import java.util.function.Predicate;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BasicEnergyContainer.class, remap = false)
public interface BasicEnergyContainerAccessor {
    @Accessor
    IContentsListener getListener();

    @Accessor("canExtract")
    Predicate<@NotNull AutomationType> getCanExtract();

    @Accessor("canInsert")
    Predicate<@NotNull AutomationType> getCanInsert();
}