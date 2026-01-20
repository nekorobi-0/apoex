package com.youtyan.apoex.recipe;

import com.youtyan.apoex.IApoExMekanism;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.cache.TwoInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ApoExRecipeHelper {

    public static void wrapHandlers(CachedRecipe<?> recipe, IApoExMekanism tile) {
        try {
            if (recipe instanceof OneInputCachedRecipe) {
                wrapOneInput((OneInputCachedRecipe<?, ?, ?>) recipe, tile);
            } else if (recipe instanceof TwoInputCachedRecipe) {
                wrapTwoInput((TwoInputCachedRecipe<?, ?, ?, ?>) recipe, tile);
            }
        } catch (Exception e) {
        }
    }

    private static void wrapOneInput(OneInputCachedRecipe<?, ?, ?> recipe, IApoExMekanism tile) throws Exception {
        Field inputField = OneInputCachedRecipe.class.getDeclaredField("inputHandler");
        IInputHandler<?> inputHandler = (IInputHandler<?>) getFinalField(inputField, recipe);
        if (!(inputHandler instanceof ApoExInputHandler)) {
            try {
                if (inputHandler.getInput() instanceof ItemStack) {
                    setFinalField(inputField, recipe, new ApoExInputHandler((IInputHandler<ItemStack>) inputHandler, tile));
                }
            } catch (Exception ignored) {}
        }

        Field outputField = OneInputCachedRecipe.class.getDeclaredField("outputHandler");
        IOutputHandler<?> outputHandler = (IOutputHandler<?>) getFinalField(outputField, recipe);
        if (!(outputHandler instanceof ApoExOutputHandler)) {
            setFinalField(outputField, recipe, new ApoExOutputHandler<>(outputHandler, tile));
        }
    }

    private static void wrapTwoInput(TwoInputCachedRecipe<?, ?, ?, ?> recipe, IApoExMekanism tile) throws Exception {
        Field inputField = TwoInputCachedRecipe.class.getDeclaredField("inputHandler");
        IInputHandler<?> inputHandler = (IInputHandler<?>) getFinalField(inputField, recipe);
        if (!(inputHandler instanceof ApoExInputHandler)) {
            try {
                if (inputHandler.getInput() instanceof ItemStack) {
                    setFinalField(inputField, recipe, new ApoExInputHandler((IInputHandler<ItemStack>) inputHandler, tile));
                }
            } catch (Exception ignored) {}
        }

        Field secondaryInputField = TwoInputCachedRecipe.class.getDeclaredField("secondaryInputHandler");
        IInputHandler<?> secondaryInputHandler = (IInputHandler<?>) getFinalField(secondaryInputField, recipe);
        if (!(secondaryInputHandler instanceof ApoExInputHandler)) {
            try {
                if (secondaryInputHandler.getInput() instanceof ItemStack) {
                    setFinalField(secondaryInputField, recipe, new ApoExInputHandler((IInputHandler<ItemStack>) secondaryInputHandler, tile));
                }
            } catch (Exception ignored) {}
        }

        Field outputField = TwoInputCachedRecipe.class.getDeclaredField("outputHandler");
        IOutputHandler<?> outputHandler = (IOutputHandler<?>) getFinalField(outputField, recipe);
        if (!(outputHandler instanceof ApoExOutputHandler)) {
            setFinalField(outputField, recipe, new ApoExOutputHandler<>(outputHandler, tile));
        }
    }

    private static Object getFinalField(Field field, Object instance) throws Exception {
        field.setAccessible(true);
        return field.get(instance);
    }

    private static void setFinalField(Field field, Object instance, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(instance, newValue);
    }
}
