package com.youtyan.apoex.mixin.mekanism.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.ApoExOutputHandler;
import com.youtyan.apoex.recipe.ApoExSkippingInputHandler;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ChemicalChemicalToChemicalCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChemicalChemicalToChemicalCachedRecipe.class, remap = false)
public abstract class MixinChemicalChemicalToChemicalCachedRecipe<STACK extends ChemicalStack<?>> {

    @Shadow @Final @Mutable
    private IInputHandler<STACK> leftInputHandler;

    @Shadow @Final @Mutable
    private IInputHandler<STACK> rightInputHandler;

    @Shadow @Final @Mutable
    private IOutputHandler<STACK> outputHandler;

    @Unique
    private boolean apoex_handlersWrapped = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "calculateOperationsThisTick", at = @At("HEAD"))
    private void apoex_wrapHandlers(CachedRecipe.OperationTracker tracker, CallbackInfo ci) {
        if (!this.apoex_handlersWrapped) {
            IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
            if (tile != null) {
                try {
                    if (!(this.leftInputHandler instanceof ApoExSkippingInputHandler)) {
                        this.leftInputHandler = new ApoExSkippingInputHandler(this.leftInputHandler, tile);
                    }
                    if (!(this.rightInputHandler instanceof ApoExSkippingInputHandler)) {
                        this.rightInputHandler = new ApoExSkippingInputHandler(this.rightInputHandler, tile);
                    }
                    if (!(this.outputHandler instanceof ApoExOutputHandler)) {
                        this.outputHandler = new ApoExOutputHandler<>(this.outputHandler, tile);
                    }
                } catch (Exception ignored) {}
                this.apoex_handlersWrapped = true;
            }
        }
    }
}
