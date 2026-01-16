package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.ingredients.InputIngredient;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ApoExItemInputHandler implements IInputHandler<@NotNull ItemStack> {

    private final IInputHandler<@NotNull ItemStack> wrapped;
    private final IApoExMekanism tile;

    public ApoExItemInputHandler(IInputHandler<@NotNull ItemStack> wrapped, IApoExMekanism tile) {
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
        return wrapped.getRecipeInput(recipeIngredient);
    }

    @Override
    public void use(@NotNull ItemStack recipeInput, int operations) {
        float reduction = tile.getInputReduction();
        if (reduction > 0 && reduction < 1.0F) {
            int amount = Math.max(1, (int) (recipeInput.getCount() * (1.0F - reduction)));
            ItemStack used = recipeInput.copy();
            used.setCount(amount);
            wrapped.use(used, operations);
        } else {
            wrapped.use(recipeInput, operations);
        }
    }

    @Override
    public void calculateOperationsCanSupport(@NotNull OperationTracker tracker, @NotNull ItemStack recipeInput, int usageMultiplier) {
        float reduction = tile.getInputReduction();
        if (reduction > 0 && reduction < 1.0F) {
            int amount = Math.max(1, (int) (recipeInput.getCount() * (1.0F - reduction)));
            ItemStack used = recipeInput.copy();
            used.setCount(amount);
            wrapped.calculateOperationsCanSupport(tracker, used, usageMultiplier);
        } else {
            wrapped.calculateOperationsCanSupport(tracker, recipeInput, usageMultiplier);
        }
    }
}
