package com.youtyan.apoex.mixin.mekanism.api;

import com.youtyan.apoex.ApoEXMod;
import com.youtyan.apoex.affix.MekanismStatAffix;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mekanism.api.math.FloatingLong;
import mekanism.common.util.MekanismUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = MekanismUtils.class, remap = false)
public class MixinMekanismUtils {

    @Inject(method = "getMaxEnergy(Lnet/minecraft/world/item/ItemStack;Lmekanism/api/math/FloatingLong;)Lmekanism/api/math/FloatingLong;", at = @At("RETURN"), cancellable = true)
    private static void apoex_getMaxEnergy(ItemStack stack, FloatingLong def, CallbackInfoReturnable<FloatingLong> cir) {
        if (stack.hasTag() && stack.getTag().contains(AffixHelper.AFFIX_DATA)) {
            Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);
            float capacityMult = 0;

            for (AffixInstance inst : affixes.values()) {
                Affix affix = inst.affix().get();
                if (affix instanceof MekanismStatAffix mekAffix) {
                    ResourceLocation id = inst.affix().getId();
                    // アイテムの場合はレベルを取得するのが難しい場合があるが、AffixInstanceにはレベルが含まれている
                    float modifier = mekAffix.getModifier(inst.rarity().get(), inst.level());

                    if (id.getNamespace().equals(ApoEXMod.MODID)) {
                        String path = id.getPath();
                        if (path.contains("energy_capacity")) {
                            capacityMult += modifier;
                        }
                    }
                }
            }

            if (capacityMult > 0) {
                FloatingLong original = cir.getReturnValue();
                cir.setReturnValue(original.multiply(1.0f + capacityMult));
            }
        }
    }
}
