package com.youtyan.apoex.mixin.mekanism.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.util.ApoExContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "mekanism.common.recipe.ingredient.creator.ItemStackIngredientCreator$SingleItemStackIngredient", remap = false)
public abstract class MixinItemStackIngredient {

    @Shadow @Final private Ingredient ingredient;
    @Shadow @Final private int amount;

    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    public void test(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
        if (tile != null) {
            float reduction = tile.getInputReduction();
            if (reduction > 0 && reduction < 1.0F) {
                if (!this.ingredient.test(stack)) {
                    cir.setReturnValue(false);
                    return;
                }
                
                int reducedAmount = Math.max(1, (int) (this.amount * (1.0F - reduction)));
                
                if (stack.getCount() >= reducedAmount) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}