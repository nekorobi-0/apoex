package com.youtyan.apoex.mixin.astral_mekanism.recipe;

import astral_mekanism.recipes.cachedRecipe.AstralCraftingCachedRecipe;
import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.ApoExFluidInputHandler;
import com.youtyan.apoex.recipe.ApoExInputHandler;
import com.youtyan.apoex.recipe.ApoExOutputHandler;
import com.youtyan.apoex.recipe.ApoExPerTickChemicalInputHandler; // Gas is a chemical
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.chemical.gas.GasStack;
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

@Mixin(value = AstralCraftingCachedRecipe.class, remap = false)
public abstract class MixinAstralCraftingCachedRecipe {

    @Shadow @Final @Mutable
    private IInputHandler<ItemStack>[] itemInputHandlers;

    @Shadow @Final @Mutable
    private IInputHandler<FluidStack> fluidInputHandler;

    @Shadow @Final @Mutable
    private IInputHandler<GasStack> gasInputHandler;

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
                    // Item Inputs
                    for (int i = 0; i < this.itemInputHandlers.length; i++) {
                        if (!(this.itemInputHandlers[i] instanceof ApoExInputHandler)) {
                            this.itemInputHandlers[i] = new ApoExInputHandler(this.itemInputHandlers[i], tile);
                        }
                    }
                    // Fluid Input
                    if (!(this.fluidInputHandler instanceof ApoExFluidInputHandler)) {
                        this.fluidInputHandler = new ApoExFluidInputHandler(this.fluidInputHandler, tile);
                    }
                    // Gas Input (Treat as per-tick chemical)
                    if (!(this.gasInputHandler instanceof ApoExPerTickChemicalInputHandler)) {
                        this.gasInputHandler = new ApoExPerTickChemicalInputHandler(this.gasInputHandler, tile);
                    }
                    // Output
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
