package com.youtyan.apoex.mixin.mekanism.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.ApoExItemInputHandler;
import com.youtyan.apoex.recipe.ApoExSkippingInputHandler;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ChemicalDissolutionCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.outputs.BoxedChemicalOutputHandler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChemicalDissolutionCachedRecipe.class, remap = false)
public abstract class MixinChemicalDissolutionCachedRecipe {

    @Shadow @Final @Mutable
    private IInputHandler<ItemStack> itemInputHandler;

    @Shadow @Final @Mutable
    private ILongInputHandler<GasStack> gasInputHandler;

    @Unique
    private boolean apoex_handlersWrapped = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "calculateOperationsThisTick", at = @At("HEAD"))
    private void apoex_wrapInputHandlers(CachedRecipe.OperationTracker tracker, CallbackInfo ci) {
        if (!this.apoex_handlersWrapped) {
            IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
            if (tile != null) {
                try {
                    if (!(this.itemInputHandler instanceof ApoExItemInputHandler)) {
                        this.itemInputHandler = new ApoExItemInputHandler(this.itemInputHandler, tile);
                    }
                    if (!(this.gasInputHandler instanceof ApoExSkippingInputHandler)) {
                        this.gasInputHandler = (ILongInputHandler) new ApoExSkippingInputHandler(this.gasInputHandler, tile);
                    }
                } catch (Exception ignored) {}
                this.apoex_handlersWrapped = true;
            }
        }
    }

    @Redirect(
            method = "finishProcessing",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/api/recipes/outputs/BoxedChemicalOutputHandler;handleOutput(Lmekanism/api/chemical/merged/BoxedChemicalStack;I)V"
            )
    )
    private void apoex_multiplyOutput(BoxedChemicalOutputHandler instance, BoxedChemicalStack output, int operations) {
        IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
        if (tile != null) {
            float multiplier = tile.getOutputMultiplier();
            if (multiplier > 0) {
                float totalOps = operations * (1.0F + multiplier);
                float storedFraction = tile.getStoredOutputFraction();
                totalOps += storedFraction;
                int baseOps = (int) totalOps;
                float newFraction = totalOps - baseOps;
                tile.setStoredOutputFraction(newFraction);

                if (baseOps > 0) {
                    instance.handleOutput(output, baseOps);
                }
                return;
            }
        }
        instance.handleOutput(output, operations);
    }
}
