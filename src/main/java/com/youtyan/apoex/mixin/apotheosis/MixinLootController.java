package com.youtyan.apoex.mixin.apotheosis;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootController;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(value = LootController.class, remap = false)
public class MixinLootController {

    @Inject(method = "getAvailableAffixes", at = @At("RETURN"), cancellable = true)
    private static void apoex_getAvailableAffixes(ItemStack stack, LootRarity rarity, Set<DynamicHolder<? extends Affix>> currentAffixes, AffixType type, CallbackInfoReturnable<List<DynamicHolder<? extends Affix>>> cir) {
        LootCategory cat = LootCategory.forItem(stack);

        if ("apoex:mekanism_machine".equals(cat.getName()) || "apoex:mekanism_generator".equals(cat.getName())) {
            if (type != AffixType.STAT || cir.getReturnValue().isEmpty()) {

                List<DynamicHolder<? extends Affix>> availableAffixes = AffixRegistry.INSTANCE.getValues().stream()
                        .filter(a -> a.canApplyTo(stack, cat, rarity))
                        .map(AffixRegistry.INSTANCE::holder)
                        .collect(Collectors.toList());

                if (!availableAffixes.isEmpty()) {
                    cir.setReturnValue(availableAffixes);
                }
            }
        }
    }
}