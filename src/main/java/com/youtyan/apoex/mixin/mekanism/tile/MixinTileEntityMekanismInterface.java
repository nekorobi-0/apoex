package com.youtyan.apoex.mixin.mekanism.tile;

import com.youtyan.apoex.util.mekanism.IAffixableTile;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TileEntityMekanism.class, remap = false)
public class MixinTileEntityMekanismInterface implements IAffixableTile {

    @Override
    public ItemStack apoex$getAffixStack() {
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;
        if (self.getPersistentData().contains(AffixHelper.AFFIX_DATA)) {
            CompoundTag affixData = self.getPersistentData().getCompound(AffixHelper.AFFIX_DATA);
            ItemStack stack = new ItemStack(Items.STONE);
            stack.getOrCreateTag().put(AffixHelper.AFFIX_DATA, affixData);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void apoex$setAffixStack(ItemStack stack) {
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;
        if (stack.isEmpty()) {
            self.getPersistentData().remove(AffixHelper.AFFIX_DATA);
        } else if (stack.hasTag() && stack.getTag().contains(AffixHelper.AFFIX_DATA)) {
            self.getPersistentData().put(AffixHelper.AFFIX_DATA, stack.getTag().getCompound(AffixHelper.AFFIX_DATA).copy());
        }
    }
}