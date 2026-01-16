package com.youtyan.apoex.mixin.apotheosis;

import com.youtyan.apoex.affix.ApoExLootCategories;
import dev.shadowsoffire.apotheosis.adventure.affix.AttributeAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(AttributeAffix.class)
public class MixinAttributeAffix {

    @Shadow(remap = false) protected Set<LootCategory> types;

    @Inject(method = "canApplyTo", at = @At("HEAD"), cancellable = true, remap = false)
    private void apoex_canApplyToMachine(ItemStack stack, LootCategory cat, LootRarity rarity, CallbackInfoReturnable<Boolean> cir) {
        if (ApoExLootCategories.MEKANISM_MACHINE != null && cat == ApoExLootCategories.MEKANISM_MACHINE) {
            // 機械カテゴリの場合、AttributeAffixが適用されないようにする
            // これにより、機械専用のAffixのみが適用されるようになる
            cir.setReturnValue(false);
        }
    }
}