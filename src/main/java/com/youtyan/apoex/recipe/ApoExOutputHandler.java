package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.outputs.IOutputHandler;
import org.jetbrains.annotations.NotNull;

public class ApoExOutputHandler<OUTPUT> implements IOutputHandler<OUTPUT> {

    private final IOutputHandler<OUTPUT> wrapped;
    private final IApoExMekanism tile;

    public ApoExOutputHandler(IOutputHandler<OUTPUT> wrapped, IApoExMekanism tile) {
        this.wrapped = wrapped;
        this.tile = tile;
    }

    @Override
    public void handleOutput(@NotNull OUTPUT output, int operations) {
        float multiplier = tile.getOutputMultiplier();
        if (multiplier > 0) {
            float totalOps = operations * (1.0F + multiplier);
            
            // 以前の端数を加算
            float storedFraction = tile.getStoredOutputFraction();
            totalOps += storedFraction;
            
            int baseOps = (int) totalOps;
            float newFraction = totalOps - baseOps;
            
            // 新しい端数を保存
            tile.setStoredOutputFraction(newFraction);
            
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
