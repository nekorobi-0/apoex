package com.youtyan.apoex.compat.mekanism;

import com.youtyan.apoex.affix.ApoExLootCategories;
import com.youtyan.apoex.item.ApoExItems;
import com.youtyan.apoex.item.mekanism.MekSuitsGemItem;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import moffy.addonapi.AddonModule;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

public class ApoExMekanismCompatModule implements AddonModule {

    public static RegistryObject<Item> MEK_SUITS_GEM;
    public static LootCategory MEKANISM_MACHINE;

    public ApoExMekanismCompatModule() {
        MEK_SUITS_GEM = ApoExItems.ITEMS.register("mek_suits_gem", () -> new MekSuitsGemItem(new Item.Properties()));
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MEKANISM_MACHINE = ApoExLootCategories.MEKANISM_MACHINE;
        });
        registerEvents();
    }

    private void registerEvents() {
        try {
            Class<?> handlerClass = Class.forName("com.youtyan.apoex.event.mekanism.MekSuitGemDamageHandler");
            MinecraftForge.EVENT_BUS.register(handlerClass);
        } catch (Throwable e) {
            System.err.println("Failed to register MekSuitGemDamageHandler: " + e.getMessage());
        }
    }
}
