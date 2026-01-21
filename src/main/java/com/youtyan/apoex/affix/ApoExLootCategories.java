package com.youtyan.apoex.affix;

import com.youtyan.apoex.ApoEXMod;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

public class ApoExLootCategories {
    public static final LootCategory MEKANISM_MACHINE = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":mekanism_machine",
            s -> isMekanismBlock(s, false),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory MEKANISM_GENERATOR = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":mekanism_generator",
            s -> isMekanismBlock(s, true),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    private static final List<String> EXCLUDED_BLOCKS = Arrays.asList("universal_storage", "item_sortable_storage");

    private static boolean isMekanismBlock(ItemStack s, boolean isGenerator) {
        if (s.getItem() instanceof BlockItem) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(s.getItem());
            if (id != null) {
                String namespace = id.getNamespace();
                String path = id.getPath();

                if (namespace.equals("astral_mekanism") && EXCLUDED_BLOCKS.stream().anyMatch(path::contains)) {
                    return false;
                }

                if (namespace.equals("mekanism") || namespace.equals("mekanismgenerators") ||
                        namespace.equals("mekanism_extras") || namespace.equals("evolvedmekanism") ||
                        namespace.equals("emextras") || namespace.equals("astral_mekanism")) {

                    if (ApoExGeneratorCategories.isAnyGenerator(s)) {
                        return false;
                    }

                    boolean isGen = path.contains("generator");

                    if (isGenerator) {
                        return isGen;
                    } else {
                        return !isGen && (isItemBlockMachine(s.getItem()) || path.contains("machine") || path.contains("factory"));
                    }
                }
            }
        }
        return false;
    }

    private static boolean isItemBlockMachine(Object item) {
        if (item == null) return false;
        Class<?> clazz = item.getClass();
        while (clazz != null) {
            if (clazz.getName().equals("mekanism.common.item.block.machine.ItemBlockMachine")) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    public static void init() {}
}