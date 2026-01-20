package com.youtyan.apoex.mixin.mekanism.client;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiTankGauge;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GuiChemicalGauge.class, remap = false)
public abstract class MixinGuiChemicalGauge<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>>
      extends GuiTankGauge<CHEMICAL, TANK> {

    public MixinGuiChemicalGauge() {
        super(null, null, 0, 0, 0, 0, null, null);
    }
}