package com.youtyan.apoex.event.mekanism;

import com.youtyan.apoex.util.mekanism.IMekArmorAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MekSuitCapabilityHandler {
    public static final ResourceLocation MEKA_SUIT_GEM_CAPABILITY = ResourceLocation.fromNamespaceAndPath("apoex", "meka_suit_gem");

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if ((Object) stack.getItem() instanceof IMekArmorAccessor armor) {
            var caps = armor.apoex$initCaps(stack, stack.getTag());
            if (caps == null) return;
            event.addCapability(MEKA_SUIT_GEM_CAPABILITY, caps);
        }
    }
}
