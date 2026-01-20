package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.ingredients.InputIngredient;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ApoExInputHandler implements IInputHandler<@NotNull ItemStack> {

    private final IInputHandler<@NotNull ItemStack> wrapped;
    private final IApoExMekanism tile;

    public ApoExInputHandler(IInputHandler<@NotNull ItemStack> wrapped, IApoExMekanism tile) {
        this.wrapped = wrapped;
        this.tile = tile;
    }

    @NotNull
    @Override
    public ItemStack getInput() {
        return wrapped.getInput();
    }

    @NotNull
    @Override
    public ItemStack getRecipeInput(@NotNull InputIngredient<@NotNull ItemStack> recipeIngredient) {
        float reduction = tile.getInputReduction();
        if (reduction > 0 && reduction < 1.0F) {
            ItemStack stored = wrapped.getInput();
            if (!stored.isEmpty()) {
                if (recipeIngredient.testType(stored)) {
                    long originalNeeded = recipeIngredient.getNeededAmount(stored);
                    if (originalNeeded > 0) {
                        int reducedNeeded = Math.max(1, (int) (originalNeeded * (1.0F - reduction)));

                        if (stored.getCount() >= reducedNeeded) {
                            ItemStack fakeResult = stored.copy();
                            fakeResult.setCount((int) originalNeeded);
                            return fakeResult;
                        }
                    }
                }
            }
        }

        return wrapped.getRecipeInput(recipeIngredient);
    }

    @Override
    public void use(@NotNull ItemStack recipeInput, int operations) {
        float reduction = tile.getInputReduction();
        if (reduction > 0 && reduction < 1.0F) {
            int perOp = Math.max(1, (int) (recipeInput.getCount() * (1.0F - reduction)));

            ItemStack reducedInput = recipeInput.copy();
            reducedInput.setCount(perOp);
            wrapped.use(reducedInput, operations);
        } else {
            wrapped.use(recipeInput, operations);
        }
    }

    @Override
    public void calculateOperationsCanSupport(@NotNull OperationTracker tracker, @NotNull ItemStack recipeInput, int usageMultiplier) {
        float reduction = tile.getInputReduction();
        if (reduction > 0 && reduction < 1.0F) {
            int originalNeeded = recipeInput.getCount();
            int reducedNeeded = Math.max(1, (int) (originalNeeded * (1.0F - reduction)));
            
            ItemStack reducedInput = recipeInput.copy();
            reducedInput.setCount(reducedNeeded);
            
            wrapped.calculateOperationsCanSupport(tracker, reducedInput, usageMultiplier);
        } else {
            wrapped.calculateOperationsCanSupport(tracker, recipeInput, usageMultiplier);
        }
    }
}
