package com.youtyan.apoex.mixin.mekanism;

import com.youtyan.apoex.affix.MekanismStatAffix;
import com.youtyan.apoex.util.mekanism.IAffixableTile;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Field;

@Pseudo
@Mixin(targets = "mekanism.generators.common.tile.TileEntityHeatGenerator", remap = false)
public abstract class MixinTileEntityHeatGenerator extends TileEntityMekanism {

    @Shadow
    BasicHeatCapacitor heatCapacitor;

    public MixinTileEntityHeatGenerator() {
        super(null, null, null);
    }

    /**
     * @author youtyan
     * @reason Add affix support
     */
    @Overwrite
    public HeatAPI.HeatTransfer simulate() {
        double ambientTemp = ambientTemperature.getAsDouble();
        double temp = ((IHeatHandler) this).getTotalTemperature();
        // 1 - Qc / Qh
        double carnotEfficiency = 1 - Math.min(ambientTemp, temp) / Math.max(ambientTemp, temp);
        double heatLost = 0.5 * (temp - ambientTemp);
        heatCapacitor.handleHeat(-heatLost);
        FloatingLong energyFromHeat = FloatingLong.create(Math.abs(heatLost) * carnotEfficiency);

        IAffixableTile affixable = (IAffixableTile) this;
        if (!affixable.apoex$getAffixStack().isEmpty()) {
            float outputBonus = 0;
            for (AffixInstance inst : AffixHelper.getAffixes(affixable.apoex$getAffixStack()).values()) {
                if (inst.affix().get() instanceof MekanismStatAffix affix) {
                    ResourceLocation id = inst.affix().getId();
                    if (id.getPath().contains("generator_output")) {
                        outputBonus += affix.getModifier(inst.rarity().get(), inst.level());
                    }
                }
            }
            if (outputBonus > 0) {
                energyFromHeat = energyFromHeat.multiply(1 + outputBonus);
            }
        }

        try {
            // Use reflection to get the energy container to avoid build-time dependency
            Field energyContainerField = this.getClass().getSuperclass().getDeclaredField("energyContainer");
            energyContainerField.setAccessible(true);
            IEnergyContainer energyContainer = (IEnergyContainer) energyContainerField.get(this);
            energyContainer.insert(energyFromHeat, Action.EXECUTE, AutomationType.INTERNAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Log the error or handle it appropriately
            e.printStackTrace();
        }

        return new HeatAPI.HeatTransfer(0, 0);
    }
}