package com.youtyan.apoex.affix;

import com.youtyan.apoex.ApoEXMod;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import mekanism.common.item.block.machine.ItemBlockMachine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ApoExLootCategories {
    public static final LootCategory MEKANISM_MACHINE = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":mekanism_machine",
            s -> isMekanismBlock(s, false), // 発電機以外
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory MEKANISM_GENERATOR = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":mekanism_generator",
            s -> isMekanismBlock(s, true), // 発電機のみ
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    private static boolean isMekanismBlock(ItemStack s, boolean isGenerator) {
        if (s.getItem() instanceof BlockItem) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(s.getItem());
            if (id != null) {
                String namespace = id.getNamespace();
                if (namespace.equals("mekanism") || namespace.equals("mekanismgenerators") ||
                        namespace.equals("mekanism_extras") || namespace.equals("evolvedmekanism") ||
                        namespace.equals("emextras") || namespace.equals("astral_mekanism")) {

                    // ApoExGeneratorCategoriesで既に分類されているものは除外
                    if (ApoExGeneratorCategories.isAnyGenerator(s)) {
                        return isGenerator;
                    }

                    String path = id.getPath();
                    boolean isGen = path.contains("generator") || path.contains("turbine") || path.contains("reactor");

                    if (isGenerator) {
                        return isGen;
                    } else {
                        // 発電機以外で、機械っぽいもの
                        return !isGen && (s.getItem() instanceof ItemBlockMachine || path.contains("machine") || path.contains("factory"));
                    }
                }
            }
        }
        return false;
    }

    public static void init() {}
}