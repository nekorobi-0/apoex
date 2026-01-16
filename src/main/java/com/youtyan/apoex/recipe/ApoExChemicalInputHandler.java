package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.ingredients.InputIngredient;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ApoExChemicalInputHandler implements ILongInputHandler {

    private final ILongInputHandler wrapped;
    private final IApoExMekanism tile;

    public ApoExChemicalInputHandler(ILongInputHandler wrapped, IApoExMekanism tile) {
        this.wrapped = wrapped;
        this.tile = tile;
    }

    @NotNull
    @Override
    public Object getInput() {
        return wrapped.getInput();
    }

    @NotNull
    @Override
    public Object getRecipeInput(@NotNull InputIngredient recipeIngredient) {
        return wrapped.getRecipeInput(recipeIngredient);
    }

    @Override
    public void use(@NotNull Object recipeInput, long operations) {
        if (recipeInput instanceof ChemicalStack) {
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
            return;
        }
        wrapped.use(recipeInput, operations);
    }

    @Override
    public void calculateOperationsCanSupport(@NotNull OperationTracker tracker, @NotNull Object recipeInput, long usageMultiplier) {
        wrapped.calculateOperationsCanSupport(tracker, recipeInput, usageMultiplier);
    }
}