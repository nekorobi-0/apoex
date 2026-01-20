package com.youtyan.apoex.mixin.mekanism.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.ApoExOutputHandler;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ChemicalCrystallizerCachedRecipe;
import mekanism.api.recipes.inputs.BoxedChemicalInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChemicalCrystallizerCachedRecipe.class, remap = false)
public abstract class MixinChemicalCrystallizerCachedRecipe {

    @Shadow @Final @Mutable
    private IOutputHandler<ItemStack> outputHandler;

    @Unique
    private boolean apoex_handlerWrapped = false;

    @Inject(method = "calculateOperationsThisTick", at = @At("HEAD"))
    private void apoex_wrapOutputHandler(CachedRecipe.OperationTracker tracker, CallbackInfo ci) {
        if (!this.apoex_handlerWrapped) {
            IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
            if (tile != null) {
                try {
                    if (!(this.outputHandler instanceof ApoExOutputHandler)) {
                        this.outputHandler = new ApoExOutputHandler<>(this.outputHandler, tile);
                    }
                } catch (Exception ignored) {}
                this.apoex_handlerWrapped = true;
            }
        }
    }

    @Redirect(
            method = "finishProcessing",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/api/recipes/inputs/BoxedChemicalInputHandler;use(Lmekanism/api/chemical/merged/BoxedChemicalStack;J)V"
            )
    )
    private void apoex_reduceInputForUse(BoxedChemicalInputHandler instance, BoxedChemicalStack recipeInput, long operations) {
        IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
        if (tile != null) {
            float reduction = tile.getInputReduction();
            if (reduction > 0 && reduction < 1.0F && !recipeInput.isEmpty()) {
                long amountToUse = recipeInput.getChemicalStack().getAmount() * operations;
                long reducedAmount = Math.max(1, (long) (amountToUse * (1.0F - reduction)));

                ChemicalStack<?> chemicalStack = recipeInput.getChemicalStack();
                ChemicalStack<?> newStack = chemicalStack.copy();
                newStack.setAmount(reducedAmount);

                instance.use(BoxedChemicalStack.box(newStack), 1);
                return;
            }
        }
        instance.use(recipeInput, operations);
    }
}