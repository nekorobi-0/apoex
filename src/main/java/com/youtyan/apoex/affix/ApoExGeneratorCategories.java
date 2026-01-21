package com.youtyan.apoex.affix;

import com.youtyan.apoex.ApoEXMod;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ApoExGeneratorCategories {

    public static final LootCategory HEAT_GENERATOR = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":heat_generator",
            s -> isGenerator(s, "heat_generator"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory GAS_GENERATOR = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":gas_generator",
            s -> isGenerator(s, "gas_burning_generator") || isGenerator(s, "gas_generator"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory BIO_GENERATOR = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":bio_generator",
            s -> isGenerator(s, "bio_generator"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory WIND_GENERATOR = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":wind_generator",
            s -> isGenerator(s, "wind_generator"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory SOLAR_GENERATOR = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":solar_generator",
            s -> isGenerator(s, "solar_generator"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory FISSION_REACTOR = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":fission_reactor",
            s -> isGenerator(s, "fission_reactor") || isGenerator(s, "compact_fir"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory FUSION_REACTOR = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":fusion_reactor",
            s -> isGenerator(s, "fusion_reactor") || isGenerator(s, "naquadah_reactor"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory TURBINE = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":turbine",
            s -> isGenerator(s, "turbine"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );
    
    public static final LootCategory SPS = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":sps",
            s -> isGenerator(s, "sps") || isGenerator(s, "compact_sps"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

    public static final LootCategory EVAPORATION_PLANT = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":evaporation_plant",
            s -> isGenerator(s, "thermal_evaporation") || isGenerator(s, "compact_tep"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );
    /*

    public static final LootCategory LASER = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":laser",
            s -> isGenerator(s, "laser") || isGenerator(s, "laser_amplifier") || isGenerator(s, "laser_tractor_beam"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );

     */



    public static final LootCategory BOILER = LootCategory.register(
            LootCategory.PICKAXE,
            ApoEXMod.MODID + ":boiler",
            s -> isGenerator(s, "boiler"),
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}
    );



    public static boolean isGenerator(ItemStack s, String name) {
        if (s.getItem() instanceof BlockItem bi) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(s.getItem());
            if (id != null) {
                String namespace = id.getNamespace();
                if (namespace.equals("mekanism") || namespace.equals("mekanismgenerators") ||
                    namespace.equals("mekanism_extras") || namespace.equals("evolved_mekanism") ||
                    namespace.equals("evolved_mekanism_extras") || namespace.equals("astral_mekanism")
                ){
                    return id.getPath().contains(name);
                }
            }
        }
        return false;
    }
    
    public static boolean isAnyGenerator(ItemStack s) {
        return isGenerator(s, "heat_generator") ||
               isGenerator(s, "gas_burning_generator") || isGenerator(s, "gas_generator") ||
               isGenerator(s, "bio_generator") ||
               isGenerator(s, "wind_generator") ||
               isGenerator(s, "solar_generator") ||
               isGenerator(s, "fission_reactor") || isGenerator(s, "compact_fir") ||
               isGenerator(s, "fusion_reactor") || isGenerator(s, "naquadah_reactor") ||
               isGenerator(s, "turbine") ||
               isGenerator(s, "sps") || isGenerator(s, "compact_sps") ||
               isGenerator(s, "thermal_evaporation") || isGenerator(s, "compact_tep") ||
               // isGenerator(s, "laser") || isGenerator(s, "laser_amplifier") || isGenerator(s, "laser_tractor_beam") ||
               isGenerator(s, "boiler");
    }

    public static void init() {}
}