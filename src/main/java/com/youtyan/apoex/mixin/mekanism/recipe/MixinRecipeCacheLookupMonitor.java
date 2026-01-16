package com.youtyan.apoex.mixin.mekanism.recipe;

import com.youtyan.apoex.IApoExMekanism;
import com.youtyan.apoex.util.ApoExContext;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = RecipeCacheLookupMonitor.class, remap = false)
public class MixinRecipeCacheLookupMonitor<RECIPE extends MekanismRecipe> {

    @Shadow
    @Final
    private IRecipeLookupHandler<RECIPE> handler;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    private void apoex_getRecipe(int cacheIndex, CallbackInfoReturnable<MekanismRecipe> cir) {
        if (cir.getReturnValue() == null && handler instanceof IApoExMekanism apoExTile) {
            boolean setContext = false;
            if (ApoExContext.MEKANISM_TILE.get() == null) {
                ApoExContext.MEKANISM_TILE.set(apoExTile);
                setContext = true;
            }
            try {
                float reduction = apoExTile.getInputReduction();
                if (reduction > 0 && handler instanceof TileEntityMekanism tile) {
                    // inputSlots フィールドの代わりに getInventorySlots を使用
                    List<IInventorySlot> inputSlots = tile.getInventorySlots(null);
                    
                    if (inputSlots != null && !inputSlots.isEmpty()) {
                        IInventorySlot slot = null;
                        for (IInventorySlot s : inputSlots) {
                            if (!s.isEmpty()) {
                                slot = s;
                                break;
                            }
                        }
                        
                        if (slot != null) {
                            ItemStack stack = slot.getStack();
                            var recipeTypeProvider = handler.getRecipeType();
                            var inputCache = recipeTypeProvider.getInputCache();
                            
                            if (inputCache instanceof InputRecipeCache.SingleItem) {
                                InputRecipeCache.SingleItem singleItemCache = (InputRecipeCache.SingleItem) inputCache;
                                MekanismRecipe recipe = (MekanismRecipe) singleItemCache.findTypeBasedRecipe(tile.getLevel(), stack);
                                if (recipe != null) {
                                    cir.setReturnValue(recipe);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            } finally {
                if (setContext) {
                    ApoExContext.MEKANISM_TILE.remove();
                }
            }
        }
    }
}
