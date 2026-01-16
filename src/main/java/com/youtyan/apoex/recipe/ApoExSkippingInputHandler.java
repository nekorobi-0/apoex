package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.ingredients.InputIngredient;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ApoExSkippingInputHandler implements ILongInputHandler {

    private final IInputHandler wrapped;
    private final IApoExMekanism tile;

    public ApoExSkippingInputHandler(IInputHandler wrapped, IApoExMekanism tile) {
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
    public void use(@NotNull Object recipeInput, int operations) {
        use(recipeInput, (long) operations);
    }

    @Override
    public void use(@NotNull Object recipeInput, long operations) {
        float reduction = tile.getInputReduction();
        
        if (reduction >= 1.0F) return;
        if (reduction <= 0) {
            if (wrapped instanceof ILongInputHandler longHandler) {
                longHandler.use(recipeInput, operations);
            } else {
                wrapped.use(recipeInput, (int) operations);
            }
            return;
        }

        int rate = (int) (100 - (reduction * 100));
        int counter = (int) tile.getConsumptionAccumulator();
        if (counter == 0) counter = 100;

        counter -= rate;

        if (counter <= 0) {
            if (wrapped instanceof ILongInputHandler longHandler) {
                longHandler.use(recipeInput, operations);
            } else {
                wrapped.use(recipeInput, (int) operations);
            }
            counter += 100;
        }

        tile.setConsumptionAccumulator((float) counter);
    }

    @Override
    public void calculateOperationsCanSupport(@NotNull OperationTracker tracker, @NotNull Object recipeInput, int usageMultiplier) {
        calculateOperationsCanSupport(tracker, recipeInput, (long) usageMultiplier);
    }

    @Override
    public void calculateOperationsCanSupport(@NotNull OperationTracker tracker, @NotNull Object recipeInput, long usageMultiplier) {
        if (wrapped instanceof ILongInputHandler longHandler) {
            longHandler.calculateOperationsCanSupport(tracker, recipeInput, usageMultiplier);
        } else {
            wrapped.calculateOperationsCanSupport(tracker, recipeInput, (int) usageMultiplier);
        }
    }
}
