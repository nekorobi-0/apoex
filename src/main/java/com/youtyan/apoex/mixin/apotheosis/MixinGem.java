package com.youtyan.apoex.mixin.apotheosis;

import com.youtyan.apoex.compat.mekanism.ApoExMekanismCompatModule;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mixin(Gem.class)
public class MixinGem {

    @Shadow @Final protected Map<LootCategory, GemBonus> bonusMap;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void injectMachineSupport(int weight, float quality, Set<ResourceLocation> dimensions, Optional<LootRarity> minRarity, Optional<LootRarity> maxRarity, List<GemBonus> bonuses, boolean unique, Optional<Set<String>> stages, CallbackInfo ci) {
        if (ApoExMekanismCompatModule.MEKANISM_MACHINE != null && this.bonusMap.containsKey(LootCategory.PICKAXE)) {
            this.bonusMap.put(ApoExMekanismCompatModule.MEKANISM_MACHINE, this.bonusMap.get(LootCategory.PICKAXE));
        }
    }
}
