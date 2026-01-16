package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.ingredients.InputIngredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class ApoExFluidInputHandler implements IInputHandler<@NotNull FluidStack> {

    private final IInputHandler<@NotNull FluidStack> wrapped;
    private final IApoExMekanism tile;

    public ApoExFluidInputHandler(IInputHandler<@NotNull FluidStack> wrapped, IApoExMekanism tile) {
        this.wrapped = wrapped;
        this.tile = tile;
    }

    @NotNull
    @Override
    public FluidStack getInput() {
        return wrapped.getInput();
    }

    @NotNull
    @Override
    public FluidStack getRecipeInput(@NotNull InputIngredient<@NotNull FluidStack> recipeIngredient) {
        return wrapped.getRecipeInput(recipeIngredient);
    }

    @Override
    public void use(@NotNull FluidStack recipeInput, int operations) {
        float reduction = tile.getInputReduction();
        
        if (reduction >= 1.0F) return;
        if (reduction <= 0) {
            wrapped.use(recipeInput, operations);
            return;
        }

        int rate = (int) (100 - (reduction * 100));
        int counter = (int) tile.getConsumptionAccumulator();
        if (counter == 0) counter = 100;

        counter -= rate;

        if (counter <= 0) {
            wrapped.use(recipeInput, operations);
            counter += 100;
        }

        tile.setConsumptionAccumulator((float) counter);
    }

    @Override
    public void calculateOperationsCanSupport(@NotNull OperationTracker tracker, @NotNull FluidStack recipeInput, int usageMultiplier) {
        wrapped.calculateOperationsCanSupport(tracker, recipeInput, usageMultiplier);
    }
}
