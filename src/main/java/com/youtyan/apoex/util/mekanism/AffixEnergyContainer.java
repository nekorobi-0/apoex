package com.youtyan.apoex.util.mekanism;

import com.youtyan.apoex.affix.MekanismStatAffix;
import com.youtyan.apoex.mixin.mekanism.accessor.BasicEnergyContainerAccessor;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AffixEnergyContainer extends BasicEnergyContainer {
    private final IAffixableTile tile;

    public AffixEnergyContainer(BasicEnergyContainer original, IAffixableTile tile) {
        super(original.getMaxEnergy(), ((BasicEnergyContainerAccessor) original).getCanExtract(), ((BasicEnergyContainerAccessor) original).getCanInsert(), ((BasicEnergyContainerAccessor) original).getListener());
        this.tile = tile;
        setEnergy(original.getEnergy());
    }

    @Override
    public FloatingLong insert(@NotNull FloatingLong amount, Action action, @NotNull AutomationType automationType) {
        if (automationType == AutomationType.INTERNAL && tile != null && !tile.apoex$getAffixStack().isEmpty()) {
            float outputBonus = 0F;
            for (AffixInstance inst : AffixHelper.getAffixes(tile.apoex$getAffixStack()).values()) {
                if (inst.affix().get() instanceof MekanismStatAffix affix) {
                    ResourceLocation id = inst.affix().getId();
                    if (id.getPath().contains("generator_output")) {
                        outputBonus += affix.getModifier(inst.rarity().get(), inst.level());
                    }
                }
            }
            if (outputBonus > 0) {
                amount = amount.multiply(1 + outputBonus);
            }
        }
        return super.insert(amount, action, automationType);
    }
}