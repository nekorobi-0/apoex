package com.youtyan.apoex.mixin.apotheosis;

import org.spongepowered.asm.mixin.Mixin;
@Mixin(targets = "dev.shadowsoffire.apotheosis.adventure.loot.LootCategory", remap = false)
public class MixinLootCategory {
    // This mixin is disabled to allow proper loot category registration.
}
