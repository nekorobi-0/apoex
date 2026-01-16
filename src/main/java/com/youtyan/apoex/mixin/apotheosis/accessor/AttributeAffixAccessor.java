package com.youtyan.apoex.mixin.apotheosis.accessor;

import dev.shadowsoffire.apotheosis.adventure.affix.AttributeAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.util.StepFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = AttributeAffix.class, remap = false)
public interface AttributeAffixAccessor {
    @Accessor
    Map<LootRarity, StepFunction> getValues();
}