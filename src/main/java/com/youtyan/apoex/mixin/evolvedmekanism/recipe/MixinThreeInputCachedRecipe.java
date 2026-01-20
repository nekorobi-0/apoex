package com.youtyan.apoex.mixin.evolvedmekanism.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.recipe.ApoExChemicalInputHandler;
import com.youtyan.apoex.recipe.ApoExFluidInputHandler;
import com.youtyan.apoex.recipe.ApoExInputHandler;
import com.youtyan.apoex.recipe.ApoExOutputHandler;
import com.youtyan.apoex.util.ApoExContext;
import fr.iglee42.evolvedmekanism.interfaces.ThreeInputCachedRecipe;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.cache.CachedRecipe;
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

@Mixin(value = ThreeInputCachedRecipe.class, remap = false)
public abstract class MixinThreeInputCachedRecipe<INPUT_A, INPUT_B, INPUT_C, OUTPUT> {

    @Shadow @Final @Mutable
    private IInputHandler<INPUT_A> inputHandler;

    @Shadow @Final @Mutable
    private IInputHandler<INPUT_B> secondaryInputHandler;

    @Shadow @Final @Mutable
    private IInputHandler<INPUT_C> tertiaryInputHandler;

    @Shadow @Final @Mutable
    private IOutputHandler<OUTPUT> outputHandler;

    @Unique
    private boolean apoex_handlersWrapped = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "calculateOperationsThisTick", at = @At("HEAD"))
    private void apoex_checkAndWrapHandlers(CachedRecipe.OperationTracker tracker, CallbackInfo ci) {
        if (!this.apoex_handlersWrapped) {
            IApoExMekanism tile = ApoExContext.MEKANISM_TILE.get();
            if (tile != null) {
                try {
                    if (this.inputHandler instanceof ILongInputHandler && this.inputHandler.getInput() instanceof ChemicalStack && !(this.inputHandler instanceof ApoExChemicalInputHandler)) {
                        this.inputHandler = (IInputHandler<INPUT_A>) new ApoExChemicalInputHandler((ILongInputHandler<ChemicalStack<?>>) this.inputHandler, tile);
                    } else if (this.inputHandler.getInput() instanceof ItemStack && !(this.inputHandler instanceof ApoExInputHandler)) {
                        this.inputHandler = (IInputHandler<INPUT_A>) new ApoExInputHandler((IInputHandler<ItemStack>) this.inputHandler, tile);
                    } else if (this.inputHandler.getInput() instanceof FluidStack && !(this.inputHandler instanceof ApoExFluidInputHandler)) {
                        this.inputHandler = (IInputHandler<INPUT_A>) new ApoExFluidInputHandler((IInputHandler<FluidStack>) this.inputHandler, tile);
                    }

                    if (this.secondaryInputHandler instanceof ILongInputHandler && this.secondaryInputHandler.getInput() instanceof ChemicalStack && !(this.secondaryInputHandler instanceof ApoExChemicalInputHandler)) {
                        this.secondaryInputHandler = (IInputHandler<INPUT_B>) new ApoExChemicalInputHandler((ILongInputHandler<ChemicalStack<?>>) this.secondaryInputHandler, tile);
                    } else if (this.secondaryInputHandler.getInput() instanceof ItemStack && !(this.secondaryInputHandler instanceof ApoExInputHandler)) {
                        this.secondaryInputHandler = (IInputHandler<INPUT_B>) new ApoExInputHandler((IInputHandler<ItemStack>) this.secondaryInputHandler, tile);
                    } else if (this.secondaryInputHandler.getInput() instanceof FluidStack && !(this.secondaryInputHandler instanceof ApoExFluidInputHandler)) {
                        this.secondaryInputHandler = (IInputHandler<INPUT_B>) new ApoExFluidInputHandler((IInputHandler<FluidStack>) this.secondaryInputHandler, tile);
                    }

                    if (this.tertiaryInputHandler instanceof ILongInputHandler && this.tertiaryInputHandler.getInput() instanceof ChemicalStack && !(this.tertiaryInputHandler instanceof ApoExChemicalInputHandler)) {
                        this.tertiaryInputHandler = (IInputHandler<INPUT_C>) new ApoExChemicalInputHandler((ILongInputHandler<ChemicalStack<?>>) this.tertiaryInputHandler, tile);
                    } else if (this.tertiaryInputHandler.getInput() instanceof ItemStack && !(this.tertiaryInputHandler instanceof ApoExInputHandler)) {
                        this.tertiaryInputHandler = (IInputHandler<INPUT_C>) new ApoExInputHandler((IInputHandler<ItemStack>) this.tertiaryInputHandler, tile);
                    } else if (this.tertiaryInputHandler.getInput() instanceof FluidStack && !(this.tertiaryInputHandler instanceof ApoExFluidInputHandler)) {
                        this.tertiaryInputHandler = (IInputHandler<INPUT_C>) new ApoExFluidInputHandler((IInputHandler<FluidStack>) this.tertiaryInputHandler, tile);
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