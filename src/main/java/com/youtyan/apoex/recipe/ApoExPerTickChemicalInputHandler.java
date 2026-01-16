package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.ingredients.InputIngredient;
import org.jetbrains.annotations.NotNull;

public class ApoExPerTickChemicalInputHandler<STACK extends ChemicalStack<?>> implements IInputHandler<@NotNull STACK> {

    private final IInputHandler<@NotNull STACK> wrapped;
    private final IApoExMekanism tile;

    public ApoExPerTickChemicalInputHandler(IInputHandler<@NotNull STACK> wrapped, IApoExMekanism tile) {
        this.wrapped = wrapped;
        this.tile = tile;
    }

    @NotNull
    @Override
    public STACK getInput() {
        return wrapped.getInput();
    }

    @NotNull
    @Override
    public STACK getRecipeInput(@NotNull InputIngredient<@NotNull STACK> recipeIngredient) {
        return wrapped.getRecipeInput(recipeIngredient);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void use(@NotNull STACK recipeInput, int operations) {
        float reduction = tile.getInputReduction();
        if (reduction > 0 && reduction < 1.0F) {
            long amount = Math.max(1, (long) (recipeInput.getAmount() * (1.0F - reduction)));
            if (amount > 0) {
                STACK reducedInput = (STACK) recipeInput.copy();
                reducedInput.setAmount(amount);
                wrapped.use(reducedInput, operations); // operationsはそのまま渡す
            }
        } else {
            wrapped.use(recipeInput, operations);
        }
    }

    @Override
    public void calculateOperationsCanSupport(@NotNull OperationTracker tracker, @NotNull STACK recipeInput, int usageMultiplier) {
        float reduction = tile.getInputReduction();
        if (reduction > 0 && reduction < 1.0F) {
            long amount = Math.max(1, (long) (recipeInput.getAmount() * (1.0F - reduction)));
            if (amount > 0) {
                STACK used = (STACK) recipeInput.copy();
                used.setAmount(amount);
                wrapped.calculateOperationsCanSupport(tracker, used, usageMultiplier);
                return;
            }
        }
        wrapped.calculateOperationsCanSupport(tracker, recipeInput, usageMultiplier);
    }
}