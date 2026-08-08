package com.youtyan.apoex.util.mekanism;

import mekanism.api.chemical.gas.Gas;
import mekanism.common.capabilities.chemical.item.ChemicalTankSpec;
import mekanism.common.capabilities.fluid.item.RateLimitMultiTankFluidHandler.FluidTankSpec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.List;

public interface IMekArmorAccessor {
    List<ChemicalTankSpec<Gas>> apoex$getGasTankSpecs();
    List<FluidTankSpec> apoex$getFluidTankSpecs();

    ICapabilityProvider apoex$initCaps(ItemStack stack, CompoundTag nbt);
}