package com.youtyan.apoex.mixin.mekanism.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.ApoExChemicalInputHandler;
import com.youtyan.apoex.recipe.ApoExInputHandler;
import com.youtyan.apoex.recipe.ApoExOutputHandler;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToItemStackCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
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

@Mixin(value = ItemStackConstantChemicalToItemStackCachedRecipe.class, remap = false)
public abstract class MixinItemStackConstantChemicalToItemStackCachedRecipe<RECIPE extends MekanismRecipe> {

    @Shadow @Final @Mutable
    private IInputHandler<ItemStack> itemInputHandler;

    @Shadow @Final @Mutable
    private ILongInputHandler<ChemicalStack<?>> chemicalInputHandler;

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
                    if (!(itemInputHandler instanceof ApoExInputHandler)) {
                        this.itemInputHandler = new ApoExInputHandler(itemInputHandler, tile);
                    }
                    if (!(chemicalInputHandler instanceof ApoExChemicalInputHandler)) {
                        this.chemicalInputHandler = new ApoExChemicalInputHandler(this.chemicalInputHandler, tile);
                    }
                    if (!(outputHandler instanceof ApoExOutputHandler)) {
                        this.outputHandler = new ApoExOutputHandler<>(outputHandler, tile);
                    }
                } catch (Exception ignored) {
                } finally {
                    this.apoex_handlersWrapped = true;
                }
            }
        }
    }
}