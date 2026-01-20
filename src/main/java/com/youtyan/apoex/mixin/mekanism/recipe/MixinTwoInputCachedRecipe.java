package com.youtyan.apoex.mixin.mekanism.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.*;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.TwoInputCachedRecipe;
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

@Mixin(value = TwoInputCachedRecipe.class, remap = false)
public abstract class MixinTwoInputCachedRecipe<INPUT_A, INPUT_B, OUTPUT, RECIPE extends MekanismRecipe> {

    @Shadow @Final @Mutable
    private IInputHandler<INPUT_A> inputHandler;

    @Shadow @Final @Mutable
    private IInputHandler<INPUT_B> secondaryInputHandler;

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
                    Object inputA = this.inputHandler.getInput();
                    if (inputA instanceof ItemStack) {
                        if (!(this.inputHandler instanceof ApoExItemInputHandler)) {
                            this.inputHandler = (IInputHandler<INPUT_A>) new ApoExItemInputHandler((IInputHandler<ItemStack>) this.inputHandler, tile);
                        }
                    } else if (inputA instanceof FluidStack || inputA instanceof ChemicalStack) {
                        if (this.apoex_isPerTick) {
                            if (!(this.inputHandler instanceof ApoExSkippingInputHandler)) {
                                this.inputHandler = new ApoExSkippingInputHandler(this.inputHandler, tile);
                            }
                        } else {
                            if (this.inputHandler instanceof ILongInputHandler && !(this.inputHandler instanceof ApoExBatchChemicalInputHandler)) {
                                this.inputHandler = (IInputHandler<INPUT_A>) new ApoExBatchChemicalInputHandler((ILongInputHandler) this.inputHandler, tile);
                            }
                        }
                    }

                    Object inputB = this.secondaryInputHandler.getInput();
                    if (inputB instanceof ItemStack) {
                        if (!(this.secondaryInputHandler instanceof ApoExItemInputHandler)) {
                            this.secondaryInputHandler = (IInputHandler<INPUT_B>) new ApoExItemInputHandler((IInputHandler<ItemStack>) this.secondaryInputHandler, tile);
                        }
                    } else if (inputB instanceof FluidStack || inputB instanceof ChemicalStack) {
                        if (this.apoex_isPerTick) {
                            if (!(this.secondaryInputHandler instanceof ApoExSkippingInputHandler)) {
                                this.secondaryInputHandler = new ApoExSkippingInputHandler(this.secondaryInputHandler, tile);
                            }
                        } else {
                            if (this.secondaryInputHandler instanceof ILongInputHandler && !(this.secondaryInputHandler instanceof ApoExBatchChemicalInputHandler)) {
                                this.secondaryInputHandler = (IInputHandler<INPUT_B>) new ApoExBatchChemicalInputHandler((ILongInputHandler) this.secondaryInputHandler, tile);
                            }
                        }
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