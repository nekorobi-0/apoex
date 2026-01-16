package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.ingredients.InputIngredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ApoExBatchChemicalInputHandler implements ILongInputHandler {

    private final ILongInputHandler wrapped;
    private final IApoExMekanism tile;

    public ApoExBatchChemicalInputHandler(ILongInputHandler wrapped, IApoExMekanism tile) {
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
        float reduction = tile.getInputReduction();
        if (reduction > 0 && reduction < 1.0F) {
            if (recipeInput instanceof ChemicalStack stack) {
                long amount = Math.max(1, (long) (stack.getAmount() * (1.0F - reduction)));
                ChemicalStack reducedInput = stack.copy();
                reducedInput.setAmount(amount);
                wrapped.use(reducedInput, operations);
                return;
            } else if (recipeInput instanceof FluidStack stack) {
                int amount = Math.max(1, (int) (stack.getAmount() * (1.0F - reduction)));
                FluidStack reducedInput = stack.copy();
                reducedInput.setAmount(amount);
                wrapped.use(reducedInput, operations);
                return;
            }
        }
        wrapped.use(recipeInput, operations);
    }

    @Override
    public void calculateOperationsCanSupport(@NotNull OperationTracker tracker, @NotNull Object recipeInput, long usageMultiplier) {
        float reduction = tile.getInputReduction();
        if (reduction > 0 && reduction < 1.0F) {
            if (recipeInput instanceof ChemicalStack stack) {
                long amount = Math.max(1, (long) (stack.getAmount() * (1.0F - reduction)));
                ChemicalStack reducedInput = stack.copy();
                reducedInput.setAmount(amount);
                wrapped.calculateOperationsCanSupport(tracker, reducedInput, usageMultiplier);
                return;
            } else if (recipeInput instanceof FluidStack stack) {
                int amount = Math.max(1, (int) (stack.getAmount() * (1.0F - reduction)));
                FluidStack reducedInput = stack.copy();
                reducedInput.setAmount(amount);
                wrapped.calculateOperationsCanSupport(tracker, reducedInput, usageMultiplier);
                return;
            }
        }
        wrapped.calculateOperationsCanSupport(tracker, recipeInput, usageMultiplier);
    }
}
