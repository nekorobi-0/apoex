package com.youtyan.apoex.mixin.mekanism.client;

import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiTankGauge;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GuiFluidGauge.class, remap = false)
public abstract class MixinGuiFluidGauge extends GuiTankGauge<FluidStack, IExtendedFluidTank> {

    public MixinGuiFluidGauge() {
        super(null, null, 0, 0, 0, 0, null, null);
    }
}