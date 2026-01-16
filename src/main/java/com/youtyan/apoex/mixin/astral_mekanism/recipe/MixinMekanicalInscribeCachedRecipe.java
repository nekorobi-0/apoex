package com.youtyan.apoex.mixin.astral_mekanism.recipe;

import astral_mekanism.generalrecipe.cachedrecipe.MekanicalInscribeCachedRecipe;
import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.ApoExInputHandler;
import com.youtyan.apoex.recipe.ApoExOutputHandler;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanicalInscribeCachedRecipe.class, remap = false)
public abstract class MixinMekanicalInscribeCachedRecipe {

    @Shadow @Final @Mutable
    private IInputHandler<ItemStack> topInputHandler;

    @Shadow @Final @Mutable
    private IInputHandler<ItemStack> middleInputHandler;

    @Shadow @Final @Mutable
    private IInputHandler<ItemStack> bottomInputHandler;

    @Shadow @Final @Mutable
    private IOutputHandler<ItemStack> outputHandler;

    @Unique
    private boolean apoex_handlersWrapped = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "calculateOperationsThisTick", at = @At("HEAD"))
    private void apoex_checkAndWrapHandlers(CachedRecipe.OperationTracker tracker, CallbackInfo ci) {
        if (!this.apoex_handlersWrapped) {
            IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
            if (tile != null) {
                try {
                    if (!(this.topInputHandler instanceof ApoExInputHandler)) {
                        this.topInputHandler = new ApoExInputHandler(this.topInputHandler, tile);
                    }
                    if (!(this.middleInputHandler instanceof ApoExInputHandler)) {
                        this.middleInputHandler = new ApoExInputHandler(this.middleInputHandler, tile);
                    }
                    if (!(this.bottomInputHandler instanceof ApoExInputHandler)) {
                        this.bottomInputHandler = new ApoExInputHandler(this.bottomInputHandler, tile);
                    }
                    if (!(this.outputHandler instanceof ApoExOutputHandler)) {
                        this.outputHandler = new ApoExOutputHandler<>(this.outputHandler, tile);
                    }
                } catch (Exception ignored) {
                    // Ignore exceptions during wrapping
                } finally {
                    this.apoex_handlersWrapped = true;
                }
            }
        }
    }
}