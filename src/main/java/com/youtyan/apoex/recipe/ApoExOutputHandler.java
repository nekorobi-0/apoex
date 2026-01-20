package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.IApoExMultiblock;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.outputs.IOutputHandler;
import org.jetbrains.annotations.NotNull;

public class ApoExOutputHandler<OUTPUT> implements IOutputHandler<OUTPUT> {

    private final IOutputHandler<OUTPUT> wrapped;
    private IApoExMekanism tile;
    private IApoExMultiblock multiblock;

    public ApoExOutputHandler(IOutputHandler<OUTPUT> wrapped, IApoExMekanism tile) {
        this.wrapped = wrapped;
        this.tile = tile;
    }

    public ApoExOutputHandler(IOutputHandler<OUTPUT> wrapped, IApoExMultiblock multiblock) {
        this.wrapped = wrapped;
        this.multiblock = multiblock;
    }

    @Override
    public void handleOutput(@NotNull OUTPUT output, int operations) {
        float multiplier = 0;
        float storedFraction = 0;

        if (this.tile != null) {
            multiplier = this.tile.getOutputMultiplier();
            storedFraction = this.tile.getStoredOutputFraction();
        } else if (this.multiblock != null) {
            multiplier = this.multiblock.getGenerationMultiplier();
            storedFraction = this.multiblock.getStoredFuelFraction();
        }

        if (multiplier > 0) {
            float totalOps = operations * (1.0F + multiplier);
            totalOps += storedFraction;
            
            int baseOps = (int) totalOps;
            float newFraction = totalOps - baseOps;
            
            if (this.tile != null) {
                this.tile.setStoredOutputFraction(newFraction);
            } else if (this.multiblock != null) {
                this.multiblock.setStoredFuelFraction(newFraction);
            }
            
            if (baseOps > 0) {
                wrapped.handleOutput(output, baseOps);
            }
        } else {
            wrapped.handleOutput(output, operations);
        }
    }

    @Override
    public void calculateOperationsCanSupport(@NotNull OperationTracker tracker, @NotNull OUTPUT output) {
        wrapped.calculateOperationsCanSupport(tracker, output);
    }
}
