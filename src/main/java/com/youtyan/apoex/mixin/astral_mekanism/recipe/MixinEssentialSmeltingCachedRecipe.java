package com.youtyan.apoex.mixin.astral_mekanism.recipe;

import astral_mekanism.generalrecipe.cachedrecipe.EssentialSmeltingCachedRecipe;
import astral_mekanism.recipes.output.ItemInfuseOutput;
import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.chemical.infuse.InfusionStack;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EssentialSmeltingCachedRecipe.class, remap = false)
public abstract class MixinEssentialSmeltingCachedRecipe {

    @Shadow
    @Nullable
    private ItemInfuseOutput recipeOutput;

    @Inject(
            method = "calculateOperationsThisTick",
            at = @At(
                    value = "FIELD",
                    target = "Lastral_mekanism/generalrecipe/cachedrecipe/EssentialSmeltingCachedRecipe;recipeOutput:Lastral_mekanism/recipes/output/ItemInfuseOutput;",
                    opcode = org.objectweb.asm.Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void apoex_multiplyOutput(CallbackInfo ci) {
        IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
        if (tile != null && this.recipeOutput != null) {
            float multiplier = tile.getOutputMultiplier();
            if (multiplier > 0) {
                ItemStack item = this.recipeOutput.itemStack();
                InfusionStack infuse = this.recipeOutput.infusionStack();

                // Item output multiplication
                if (!item.isEmpty()) {
                    float totalItemCount = item.getCount() * (1.0F + multiplier);
                    float storedItemFraction = tile.getStoredOutputFraction(); // Reusing for item fraction
                    totalItemCount += storedItemFraction;
                    int newItemCount = (int) totalItemCount;
                    float newItemFraction = totalItemCount - newItemCount;
                    tile.setStoredOutputFraction(newItemFraction); // Store item fraction

                    if (newItemCount > 0) {
                        ItemStack newItem = item.copy();
                        newItem.setCount(newItemCount);
                        item = newItem; // Update item for the new ItemInfuseOutput
                    }
                }

                // Infusion output multiplication (existing logic)
                if (!infuse.isEmpty()) {
                    float totalInfuseAmount = infuse.getAmount() * (1.0F + multiplier);
                    float storedInfuseFraction = tile.getStoredOutputFraction(); // This might conflict if used for both
                    totalInfuseAmount += storedInfuseFraction;
                    long newInfuseAmount = (long) totalInfuseAmount;
                    float newInfuseFraction = totalInfuseAmount - newInfuseAmount;
                    tile.setStoredOutputFraction(newInfuseFraction); // Store infuse fraction

                    if (newInfuseAmount > 0) {
                        InfusionStack newInfuse = infuse.copy();
                        newInfuse.setAmount(newInfuseAmount);
                        infuse = newInfuse; // Update infuse for the new ItemInfuseOutput
                    }
                }

                // Create new ItemInfuseOutput with modified item and infuse stacks
                this.recipeOutput = new ItemInfuseOutput(item, infuse);
            }
        }
    }
}