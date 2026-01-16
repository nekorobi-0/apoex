package com.youtyan.apoex.mixin.mekanism.client;

import com.youtyan.apoex.IApoExMultiblock;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTexturedElement;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.generators.client.gui.GuiIndustrialTurbine;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = GuiIndustrialTurbine.class, remap = false)
public abstract class MixinGuiIndustrialTurbine {

    @Redirect(method = "addGuiElements", at = @At(value = "NEW", target = "mekanism/client/gui/element/tab/GuiEnergyTab"))
    private GuiEnergyTab apoex_replaceEnergyTab(IGuiWrapper gui, GuiTexturedElement.IInfoHandler tooltip) {
        return new GuiEnergyTab(gui, () -> {
            GuiIndustrialTurbine self = (GuiIndustrialTurbine) (Object) this;
            TurbineMultiblockData multiblock = self.getTileEntity().getMultiblock();
            EnergyDisplay storing;
            EnergyDisplay producing;
            if (multiblock.isFormed()) {
                storing = EnergyDisplay.of(multiblock.energyContainer);
                producing = EnergyDisplay.of(multiblock.getProductionRate());
            } else {
                storing = EnergyDisplay.ZERO;
                producing = EnergyDisplay.ZERO;
            }
            
            List<Component> list = new ArrayList<>();
            list.add(MekanismLang.STORING.translate(storing));
            list.add(GeneratorsLang.PRODUCING_AMOUNT.translate(producing));
            
            if (multiblock instanceof IApoExMultiblock apoExMultiblock) {
                float mult = apoExMultiblock.getGenerationMultiplier();
                if (mult > 0) {
                    list.add(Component.literal("ApoEx Multiplier: +" + (int)(mult * 100) + "%"));
                }
            }
            
            return list;
        });
    }
}
