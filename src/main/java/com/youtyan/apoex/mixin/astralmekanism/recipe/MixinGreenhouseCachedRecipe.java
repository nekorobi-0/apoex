package com.youtyan.apoex.mixin.astralmekanism.recipe;

import astral_mekanism.recipes.cachedRecipe.GreenhouseCachedRecipe;
import astral_mekanism.recipes.output.TripleItemOutput;
import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.ApoExFluidInputHandler;
import com.youtyan.apoex.recipe.ApoExInputHandler;
import com.youtyan.apoex.recipe.ApoExOutputHandler;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GreenhouseCachedRecipe.class, remap = false)
public abstract class MixinGreenhouseCachedRecipe {

    @Shadow @Final @Mutable
    private IInputHandler<ItemStack> seedInputHandler;

    @Shadow @Final @Mutable
    private IInputHandler<ItemStack> farmlandHandler;

    @Shadow @Final @Mutable
    private IInputHandler<FluidStack> fluidHandler;

    @Shadow @Final @Mutable
    private IOutputHandler<TripleItemOutput> outputHandler;

    @Unique
    private boolean apoex_handlersWrapped = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "calculateOperationsThisTick", at = @At("HEAD"))
    private void apoex_checkAndWrapHandlers(CachedRecipe.OperationTracker tracker, CallbackInfo ci) {
        if (!this.apoex_handlersWrapped) {
            IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
            if (tile != null) {
                try {
                    if (!(this.seedInputHandler instanceof ApoExInputHandler)) {
                        this.seedInputHandler = new ApoExInputHandler(this.seedInputHandler, tile);
                    }
                    if (!(this.farmlandHandler instanceof ApoExInputHandler)) {
                        this.farmlandHandler = new ApoExInputHandler(this.farmlandHandler, tile);
                    }
                    if (!(this.fluidHandler instanceof ApoExFluidInputHandler)) {
                        this.fluidHandler = new ApoExFluidInputHandler(this.fluidHandler, tile);
                    }
                    if (!(this.outputHandler instanceof ApoExOutputHandler)) {
                        this.outputHandler = new ApoExOutputHandler<>(this.outputHandler, tile);
                    }
                } catch (Exception ignored) {
                } finally {
                    this.apoex_handlersWrapped = true;
                }
            }
        }
    }
}