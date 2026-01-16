package com.youtyan.apoex.mixin.mekanism.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.*;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
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

import java.lang.reflect.Field;

@Mixin(value = OneInputCachedRecipe.class, remap = false)
public abstract class MixinOneInputCachedRecipe<INPUT, OUTPUT, RECIPE extends MekanismRecipe> {

    @Shadow @Final @Mutable
    private IInputHandler<INPUT> inputHandler;

    @Shadow @Final @Mutable
    private IOutputHandler<OUTPUT> outputHandler;

    @Unique
    private boolean apoex_handlersWrapped = false;

    @Unique
    private boolean apoex_perTickChecked = false;

    @Unique
    private boolean apoex_isPerTick = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "calculateOperationsThisTick", at = @At("HEAD"))
    private void apoex_checkAndWrapHandlers(CachedRecipe.OperationTracker tracker, CallbackInfo ci) {
        if (!this.apoex_handlersWrapped) {
            IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
            if (tile != null) {
                if (!this.apoex_perTickChecked) {
                    try {
                        Field field = this.getClass().getDeclaredField("perTickUsage");
                        this.apoex_isPerTick = field.getType() == boolean.class;
                    } catch (NoSuchFieldException e) {
                        this.apoex_isPerTick = false;
                    }
                    this.apoex_perTickChecked = true;
                }

                try {
                    // Input handler
                    Object input = this.inputHandler.getInput();
                    if (input instanceof ItemStack) {
                        if (!(this.inputHandler instanceof ApoExItemInputHandler)) {
                            this.inputHandler = (IInputHandler<INPUT>) new ApoExItemInputHandler((IInputHandler<ItemStack>) this.inputHandler, tile);
                        }
                    } else if (input instanceof FluidStack || input instanceof ChemicalStack) {
                        if (this.apoex_isPerTick) {
                            if (!(this.inputHandler instanceof ApoExSkippingInputHandler)) {
                                this.inputHandler = new ApoExSkippingInputHandler(this.inputHandler, tile);
                            }
                        } else {
                            if (this.inputHandler instanceof ILongInputHandler && !(this.inputHandler instanceof ApoExBatchChemicalInputHandler)) {
                                this.inputHandler = (IInputHandler<INPUT>) new ApoExBatchChemicalInputHandler((ILongInputHandler) this.inputHandler, tile);
                            }
                        }
                    }

                    // Output handler
                    if (!(outputHandler instanceof ApoExOutputHandler)) {
                        this.outputHandler = new ApoExOutputHandler<>(outputHandler, tile);
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
