package com.youtyan.apoex.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AffixData implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;

    public AffixData(DataGenerator gen) {
        this.output = gen.getPackOutput();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        String[] generatorTypes = {
            "apoex:heat_generator", "apoex:gas_generator", "apoex:bio_generator", 
            "apoex:wind_generator", "apoex:solar_generator", 
            "apoex:fission_reactor", "apoex:fusion_reactor", "apoex:turbine"
        };
        
        String[] singleBlockGeneratorTypes = {
            "apoex:heat_generator", "apoex:gas_generator", "apoex:bio_generator", 
            "apoex:wind_generator", "apoex:solar_generator"
        };

        // --- 共通設定 (Single Block) ---

        // エネルギー容量 (Energy Capacity)
        Map<String, Step> capacityValues = new HashMap<>();
        capacityValues.put("apotheosis:common", new Step(0.10F, 10, 0.01F));
        capacityValues.put("apotheosis:uncommon", new Step(0.20F, 10, 0.01F));
        capacityValues.put("apotheosis:rare", new Step(0.35F, 10, 0.01F));
        capacityValues.put("apotheosis:epic", new Step(0.55F, 10, 0.02F));
        capacityValues.put("apotheosis:mythic", new Step(0.80F, 10, 0.01F));
        capacityValues.put("apotheosis:ancient", new Step(1.10F, 10, 0.01F));
        capacityValues.put("apotheotic_additions:esoteric", new Step(3.60F, 70, 0.05F));
        capacityValues.put("apotheotic_additions:heirloom", new Step(2.00F, 50, 0.03F));
        capacityValues.put("apotheotic_additions:artifact", new Step(1.50F, 30, 0.02F));

        futures.add(generateAffix(cache, "mekanism/energy_capacity", "affix.apoex.mekanism.energy_capacity", capacityValues, "apoex:mekanism_machine"));
        futures.add(generateAffix(cache, "mekanism/generator/energy_capacity", "affix.apoex.mekanism.generator.energy_capacity", capacityValues, singleBlockGeneratorTypes));

        // Tick速度 (Tick Speed)
        Map<String, Step> tickSpeedValues = new HashMap<>();
        tickSpeedValues.put("apotheosis:mythic", new Step(1.0F, 1, 1.0F));
        tickSpeedValues.put("apotheosis:ancient", new Step(1.0F, 2, 1.0F));
        tickSpeedValues.put("apotheotic_additions:esoteric", new Step(10.0F, 4, 1.0F));
        tickSpeedValues.put("apotheotic_additions:heirloom", new Step(5.0F, 2, 1.0F));
        tickSpeedValues.put("apotheotic_additions:artifact", new Step(3.0F, 2, 1.0F));

        futures.add(generateAffix(cache, "mekanism/tick_speed", "affix.apoex.mekanism.tick_speed", tickSpeedValues, "apoex:mekanism_machine"));
        futures.add(generateAffix(cache, "mekanism/generator/tick_speed", "affix.apoex.mekanism.generator.tick_speed", tickSpeedValues, generatorTypes));

        // エネルギー効率 (Energy Efficiency)
        Map<String, Step> efficiencyValues = new HashMap<>();
        efficiencyValues.put("apotheosis:common", new Step(0.05F, 10, 0.005F));
        efficiencyValues.put("apotheosis:uncommon", new Step(0.10F, 10, 0.01F));
        efficiencyValues.put("apotheosis:rare", new Step(0.20F, 10, 0.01F));
        efficiencyValues.put("apotheosis:epic", new Step(0.35F, 10, 0.01F));
        efficiencyValues.put("apotheosis:mythic", new Step(0.55F, 10, 0.01F));
        efficiencyValues.put("apotheosis:ancient", new Step(0.80F, 10, 0.01F));
        efficiencyValues.put("apotheotic_additions:esoteric", new Step(2.00F, 10, 0.01F));
        efficiencyValues.put("apotheotic_additions:heirloom", new Step(1.50F, 10, 0.01F));
        efficiencyValues.put("apotheotic_additions:artifact", new Step(1.10F, 10, 0.01F));

        futures.add(generateAffix(cache, "mekanism/energy_efficiency", "affix.apoex.mekanism.energy_efficiency", efficiencyValues, "apoex:mekanism_machine"));

        // 生産量倍増 (Output Multiplier)
        Map<String, Step> outputValues = new HashMap<>();
        outputValues.put("apotheosis:mythic", new Step(1.0F, 1, 1.0F));
        outputValues.put("apotheosis:ancient", new Step(1.0F, 2, 1.0F));
        outputValues.put("apotheotic_additions:esoteric", new Step(5.0F, 5, 1.0F));
        outputValues.put("apotheotic_additions:heirloom", new Step(3.0F, 2, 1.0F));
        outputValues.put("apotheotic_additions:artifact", new Step(2.0F, 1, 1.0F));

        futures.add(generateAffix(cache, "mekanism/output_multiplier", "affix.apoex.mekanism.output_multiplier", outputValues, "apoex:mekanism_machine"));

        // 素材消費削減 (Input Reduction)
        Map<String, Step> reductionValues = new HashMap<>();
        reductionValues.put("apotheosis:rare", new Step(0.10F, 5, 0.01F));
        reductionValues.put("apotheosis:epic", new Step(0.20F, 5, 0.01F));
        reductionValues.put("apotheosis:mythic", new Step(0.30F, 5, 0.01F));
        reductionValues.put("apotheosis:ancient", new Step(0.40F, 5, 0.01F));
        reductionValues.put("apotheotic_additions:esoteric", new Step(0.70F, 5, 0.01F));
        reductionValues.put("apotheotic_additions:heirloom", new Step(0.60F, 5, 0.01F));
        reductionValues.put("apotheotic_additions:artifact", new Step(0.50F, 5, 0.01F));

        futures.add(generateAffix(cache, "mekanism/input_reduction", "affix.apoex.mekanism.input_reduction", reductionValues, "apoex:mekanism_machine"));

        // 発電量倍率 (Generation Multiplier) - Single Block
        Map<String, Step> generationValues = new HashMap<>();
        generationValues.put("apotheosis:common", new Step(0.10F, 10, 0.01F));
        generationValues.put("apotheosis:uncommon", new Step(0.20F, 10, 0.01F));
        generationValues.put("apotheosis:rare", new Step(0.35F, 10, 0.01F));
        generationValues.put("apotheosis:epic", new Step(0.55F, 10, 0.01F));
        generationValues.put("apotheosis:mythic", new Step(0.80F, 10, 0.01F));
        generationValues.put("apotheosis:ancient", new Step(1.10F, 10, 0.01F));
        generationValues.put("apotheotic_additions:esoteric", new Step(2.60F, 10, 0.01F));
        generationValues.put("apotheotic_additions:heirloom", new Step(2.00F, 10, 0.01F));
        generationValues.put("apotheotic_additions:artifact", new Step(1.50F, 10, 0.01F));

        futures.add(generateAffix(cache, "mekanism/generator/generation_multiplier", "affix.apoex.mekanism.generator.generation_multiplier", generationValues, singleBlockGeneratorTypes));

        // 燃料効率 (Fuel Efficiency) - Single Block
        Map<String, Step> fuelEfficiencyValues = new HashMap<>();
        fuelEfficiencyValues.put("apotheosis:rare", new Step(0.10F, 5, 0.01F));
        fuelEfficiencyValues.put("apotheosis:epic", new Step(0.20F, 5, 0.01F));
        fuelEfficiencyValues.put("apotheosis:mythic", new Step(0.30F, 5, 0.01F));
        fuelEfficiencyValues.put("apotheosis:ancient", new Step(0.40F, 5, 0.01F));
        fuelEfficiencyValues.put("apotheotic_additions:esoteric", new Step(0.70F, 5, 0.01F));
        fuelEfficiencyValues.put("apotheotic_additions:heirloom", new Step(0.60F, 5, 0.01F));
        fuelEfficiencyValues.put("apotheotic_additions:artifact", new Step(0.50F, 5, 0.01F));

        futures.add(generateAffix(cache, "mekanism/generator/fuel_efficiency", "affix.apoex.mekanism.generator.fuel_efficiency", fuelEfficiencyValues,
            "apoex:heat_generator", "apoex:gas_generator", "apoex:bio_generator"));

        // 燃料容量 (Fuel Capacity) - Single Block
        Map<String, Step> fuelCapacityValues = new HashMap<>();
        fuelCapacityValues.put("apotheosis:common", new Step(0.10F, 10, 0.01F));
        fuelCapacityValues.put("apotheosis:uncommon", new Step(0.20F, 10, 0.01F));
        fuelCapacityValues.put("apotheosis:rare", new Step(0.35F, 10, 0.01F));
        fuelCapacityValues.put("apotheosis:epic", new Step(0.55F, 10, 0.01F));
        fuelCapacityValues.put("apotheosis:mythic", new Step(0.60F, 10, 0.01F));
        fuelCapacityValues.put("apotheosis:ancient", new Step(0.70F, 10, 0.01F));
        fuelCapacityValues.put("apotheotic_additions:esoteric", new Step(1.10F, 10, 0.01F));
        fuelCapacityValues.put("apotheotic_additions:heirloom", new Step(0.90F, 10, 0.01F));
        fuelCapacityValues.put("apotheotic_additions:artifact", new Step(0.80F, 10, 0.01F));

        futures.add(generateAffix(cache, "mekanism/generator/fuel_capacity", "affix.apoex.mekanism.generator.fuel_capacity", fuelCapacityValues,
            "apoex:heat_generator", "apoex:gas_generator", "apoex:bio_generator"));


        // --- マルチブロック個別設定 ---

        // デフォルト値の定義 (コピー用)
        Map<String, Step> defaultMultiblockCapacity = new HashMap<>();
        defaultMultiblockCapacity.put("apotheosis:common", new Step(0.20F, 10, 0.02F));
        defaultMultiblockCapacity.put("apotheosis:uncommon", new Step(0.40F, 10, 0.02F));
        defaultMultiblockCapacity.put("apotheosis:rare", new Step(0.70F, 10, 0.02F));
        defaultMultiblockCapacity.put("apotheosis:epic", new Step(1.10F, 10, 0.04F));
        defaultMultiblockCapacity.put("apotheosis:mythic", new Step(1.60F, 10, 0.02F));
        defaultMultiblockCapacity.put("apotheosis:ancient", new Step(2.20F, 10, 0.02F));
        defaultMultiblockCapacity.put("apotheotic_additions:esoteric", new Step(7.20F, 70, 0.10F));
        defaultMultiblockCapacity.put("apotheotic_additions:heirloom", new Step(4.00F, 50, 0.06F));
        defaultMultiblockCapacity.put("apotheotic_additions:artifact", new Step(3.00F, 30, 0.04F));

        Map<String, Step> defaultMultiblockGeneration = new HashMap<>();
        defaultMultiblockGeneration.put("apotheosis:common", new Step(0.10F, 10, 0.01F));
        defaultMultiblockGeneration.put("apotheosis:uncommon", new Step(0.10F, 20, 0.01F));
        defaultMultiblockGeneration.put("apotheosis:rare", new Step(0.10F, 40, 0.01F));
        defaultMultiblockGeneration.put("apotheosis:epic", new Step(0.10F, 50, 0.01F));
        defaultMultiblockGeneration.put("apotheosis:mythic", new Step(0.10F, 60, 0.01F));
        defaultMultiblockGeneration.put("apotheosis:ancient", new Step(0.10F, 70, 0.01F));
        defaultMultiblockGeneration.put("apotheotic_additions:esoteric", new Step(0.10F, 130, 0.01F));
        defaultMultiblockGeneration.put("apotheotic_additions:heirloom", new Step(0.10F, 90, 0.01F));
        defaultMultiblockGeneration.put("apotheotic_additions:artifact", new Step(0.10F, 80, 0.01F));

        Map<String, Step> defaultMultiblockFuelEfficiency = new HashMap<>();
        defaultMultiblockFuelEfficiency.put("apotheosis:rare", new Step(0.20F, 5, 0.02F));
        defaultMultiblockFuelEfficiency.put("apotheosis:epic", new Step(0.30F, 5, 0.02F));
        defaultMultiblockFuelEfficiency.put("apotheosis:mythic", new Step(0.40F, 5, 0.02F));
        defaultMultiblockFuelEfficiency.put("apotheosis:ancient", new Step(0.50F, 5, 0.02F));
        defaultMultiblockFuelEfficiency.put("apotheotic_additions:esoteric", new Step(0.90F, 5, 0.02F));
        defaultMultiblockFuelEfficiency.put("apotheotic_additions:heirloom", new Step(0.80F, 5, 0.02F));
        defaultMultiblockFuelEfficiency.put("apotheotic_additions:artifact", new Step(0.70F, 5, 0.02F));

        Map<String, Step> defaultMultiblockHeatEfficiency = new HashMap<>();
        defaultMultiblockHeatEfficiency.put("apotheosis:rare", new Step(0.10F, 05, 0.01F));
        defaultMultiblockHeatEfficiency.put("apotheosis:epic", new Step(0.20F, 05, 0.01F));
        defaultMultiblockHeatEfficiency.put("apotheosis:mythic", new Step(0.4F, 10, 0.01F));
        defaultMultiblockHeatEfficiency.put("apotheosis:ancient", new Step(0.5F, 20, 0.01F));
        defaultMultiblockHeatEfficiency.put("apotheotic_additions:esoteric", new Step(0.85F, 10, 0.01F));
        defaultMultiblockHeatEfficiency.put("apotheotic_additions:heirloom", new Step(0.65F, 20, 0.01F));
        defaultMultiblockHeatEfficiency.put("apotheotic_additions:artifact", new Step(0.55F, 20, 0.01F));

        Map<String, Step> defaultMultiblockFuelCapacity = new HashMap<>();
        defaultMultiblockFuelCapacity.put("apotheosis:common", new Step(0.20F, 10, 0.02F));
        defaultMultiblockFuelCapacity.put("apotheosis:uncommon", new Step(0.40F, 10, 0.02F));
        defaultMultiblockFuelCapacity.put("apotheosis:rare", new Step(0.70F, 10, 0.02F));
        defaultMultiblockFuelCapacity.put("apotheosis:epic", new Step(1.10F, 10, 0.04F));
        defaultMultiblockFuelCapacity.put("apotheosis:mythic", new Step(1.60F, 10, 0.02F));
        defaultMultiblockFuelCapacity.put("apotheosis:ancient", new Step(2.20F, 10, 0.02F));
        defaultMultiblockFuelCapacity.put("apotheotic_additions:esoteric", new Step(7.20F, 70, 0.10F));
        defaultMultiblockFuelCapacity.put("apotheotic_additions:heirloom", new Step(4.00F, 50, 0.06F));
        defaultMultiblockFuelCapacity.put("apotheotic_additions:artifact", new Step(3.00F, 30, 0.04F));

        Map<String, Step> defaultMultiblockoutput = new HashMap<>();
        defaultMultiblockoutput.put("apotheosis:mythic", new Step(1.0F, 1, 1.0F));
        defaultMultiblockoutput.put("apotheosis:ancient", new Step(1.0F, 2, 1.0F));
        defaultMultiblockoutput.put("apotheotic_additions:esoteric", new Step(5.0F, 5, 1.0F));
        defaultMultiblockoutput.put("apotheotic_additions:heirloom", new Step(3.0F, 2, 1.0F));
        defaultMultiblockoutput.put("apotheotic_additions:artifact", new Step(2.0F, 1, 1.0F));

        Map<String, Step> defaultMultiblockresistance = new HashMap<>();
        defaultMultiblockresistance.put("apotheosis:rare", new Step(0.10F, 5, 0.01F));
        defaultMultiblockresistance.put("apotheosis:epic", new Step(0.20F, 5, 0.01F));
        defaultMultiblockresistance.put("apotheosis:mythic", new Step(0.30F, 5, 0.01F));
        defaultMultiblockresistance.put("apotheosis:ancient", new Step(0.40F, 5, 0.01F));
        defaultMultiblockresistance.put("apotheotic_additions:esoteric", new Step(0.70F, 5, 0.01F));
        defaultMultiblockresistance.put("apotheotic_additions:heirloom", new Step(0.60F, 5, 0.01F));
        defaultMultiblockresistance.put("apotheotic_additions:artifact", new Step(0.50F, 5, 0.01F));

        Map<String, Step> defaultMultiblockHeatCapacity = new HashMap<>();
        defaultMultiblockHeatCapacity.put("apotheosis:rare", new Step(0.10F, 5, 0.01F));
        defaultMultiblockHeatCapacity.put("apotheosis:epic", new Step(0.20F, 5, 0.01F));
        defaultMultiblockHeatCapacity.put("apotheosis:mythic", new Step(0.30F, 5, 0.01F));
        defaultMultiblockHeatCapacity.put("apotheosis:ancient", new Step(0.40F, 5, 0.01F));
        defaultMultiblockHeatCapacity.put("apotheotic_additions:esoteric", new Step(0.70F, 5, 0.01F));
        defaultMultiblockHeatCapacity.put("apotheotic_additions:heirloom", new Step(0.60F, 5, 0.01F));
        defaultMultiblockHeatCapacity.put("apotheotic_additions:artifact", new Step(0.50F, 5, 0.01F));

        Map<String, Step> TurbineFuelCapacity = new HashMap<>();
        TurbineFuelCapacity.put("apotheosis:common", new Step(0.10F, 10, 0.01F));
        TurbineFuelCapacity.put("apotheosis:uncommon", new Step(0.20F, 10, 0.01F));
        TurbineFuelCapacity.put("apotheosis:rare", new Step(0.35F, 10, 0.01F));
        TurbineFuelCapacity.put("apotheosis:epic", new Step(0.55F, 10, 0.01F));
        TurbineFuelCapacity.put("apotheosis:mythic", new Step(0.60F, 10, 0.01F));
        TurbineFuelCapacity.put("apotheosis:ancient", new Step(0.70F, 10, 0.01F));
        TurbineFuelCapacity.put("apotheotic_additions:esoteric", new Step(1.10F, 10, 0.01F));
        TurbineFuelCapacity.put("apotheotic_additions:heirloom", new Step(0.90F, 10, 0.01F));
        TurbineFuelCapacity.put("apotheotic_additions:artifact", new Step(0.80F, 10, 0.01F));

        Map<String, Step> TurbinefuelEfficiency = new HashMap<>();
        TurbinefuelEfficiency.put("apotheosis:rare", new Step(0.10F, 5, 0.01F));
        TurbinefuelEfficiency.put("apotheosis:epic", new Step(0.20F, 5, 0.01F));
        TurbinefuelEfficiency.put("apotheosis:mythic", new Step(0.30F, 5, 0.01F));
        TurbinefuelEfficiency.put("apotheosis:ancient", new Step(0.40F, 5, 0.01F));
        TurbinefuelEfficiency.put("apotheotic_additions:esoteric", new Step(0.70F, 5, 0.01F));
        TurbinefuelEfficiency.put("apotheotic_additions:heirloom", new Step(0.60F, 5, 0.01F));
        TurbinefuelEfficiency.put("apotheotic_additions:artifact", new Step(0.50F, 5, 0.01F));

        // 各マルチブロックごとの設定
        // 必要に応じてここで値を上書きする
        
        // Fission Reactor
        futures.add(generateMultiblockAffix(cache, "fission_reactor", "generation_multiplier", defaultMultiblockGeneration, true));
        futures.add(generateMultiblockAffix(cache, "fission_reactor", "fuel_efficiency", defaultMultiblockFuelEfficiency, true));
        futures.add(generateMultiblockAffix(cache, "fission_reactor", "fuel_capacity", defaultMultiblockFuelCapacity, true));
        futures.add(generateMultiblockAffix(cache, "fission_reactor", "damage_resistance", defaultMultiblockresistance, true));
        futures.add(generateMultiblockAffix(cache, "fission_reactor", "heat_capacity_multiplier", defaultMultiblockHeatCapacity, true));
        futures.add(generateMultiblockAffix(cache, "fission_reactor", "heat_efficiency", defaultMultiblockHeatEfficiency, true));

        // Fusion Reactor
        futures.add(generateMultiblockAffix(cache, "fusion_reactor", "energy_capacity", defaultMultiblockCapacity, true));
        futures.add(generateMultiblockAffix(cache, "fusion_reactor", "generation_multiplier", defaultMultiblockGeneration, true));
        futures.add(generateMultiblockAffix(cache, "fusion_reactor", "fuel_efficiency", defaultMultiblockFuelEfficiency, true));
        futures.add(generateMultiblockAffix(cache, "fusion_reactor", "heat_efficiency", defaultMultiblockHeatEfficiency, true));
        futures.add(generateMultiblockAffix(cache, "fusion_reactor", "fuel_capacity", defaultMultiblockFuelCapacity, true));
        futures.add(generateMultiblockAffix(cache, "fusion_reactor", "output_multiplier", defaultMultiblockFuelCapacity, true));

        // Turbine
        futures.add(generateMultiblockAffix(cache, "turbine", "energy_capacity", defaultMultiblockCapacity, true));
        futures.add(generateMultiblockAffix(cache, "turbine", "generation_multiplier", defaultMultiblockGeneration, true));
        futures.add(generateMultiblockAffix(cache, "turbine", "fuel_efficiency", TurbinefuelEfficiency, true)); // タービンに燃料効率は微妙だが、蒸気消費効率として機能するならあり
        futures.add(generateMultiblockAffix(cache, "turbine", "fuel_capacity", TurbineFuelCapacity, true));
        futures.add(generateMultiblockAffix(cache, "turbine", "output_multiplier", defaultMultiblockoutput, true));
        // SPS (Not Generator)
        futures.add(generateMultiblockAffix(cache, "sps", "energy_capacity", defaultMultiblockCapacity, false));
        futures.add(generateMultiblockAffix(cache, "sps", "generation_multiplier", defaultMultiblockGeneration, false));
        futures.add(generateMultiblockAffix(cache, "sps", "fuel_efficiency", defaultMultiblockFuelEfficiency, false));
        futures.add(generateMultiblockAffix(cache, "sps", "fuel_capacity", defaultMultiblockFuelCapacity, false));
        futures.add(generateMultiblockAffix(cache, "sps", "output_multiplier", defaultMultiblockoutput, false));
        // Evaporation Plant (Not Generator)
        futures.add(generateMultiblockAffix(cache, "evaporation_plant", "generation_multiplier", defaultMultiblockGeneration, false));
        futures.add(generateMultiblockAffix(cache, "evaporation_plant", "fuel_efficiency", defaultMultiblockFuelEfficiency, false));
        futures.add(generateMultiblockAffix(cache, "evaporation_plant", "heat_efficiency", defaultMultiblockHeatEfficiency, false));
        futures.add(generateMultiblockAffix(cache, "evaporation_plant", "fuel_capacity", defaultMultiblockFuelCapacity, false));
        futures.add(generateMultiblockAffix(cache, "evaporation_plant", "output_multiplier", defaultMultiblockoutput, false));

        // --- レーザー用 (Laser) ---
        // 熱効率 (Heat Efficiency) - Laser
        Map<String, Step> laserHeatEfficiencyValues = new HashMap<>();
        laserHeatEfficiencyValues.put("apotheosis:rare", new Step(0.10F, 05, 0.01F));
        laserHeatEfficiencyValues.put("apotheosis:epic", new Step(0.20F, 05, 0.01F));
        laserHeatEfficiencyValues.put("apotheosis:mythic", new Step(0.4F, 10, 0.01F));
        laserHeatEfficiencyValues.put("apotheosis:ancient", new Step(0.5F, 20, 0.01F));
        laserHeatEfficiencyValues.put("apotheotic_additions:esoteric", new Step(0.85F, 10, 0.01F));
        laserHeatEfficiencyValues.put("apotheotic_additions:heirloom", new Step(0.65F, 20, 0.01F));
        laserHeatEfficiencyValues.put("apotheotic_additions:artifact", new Step(0.55F, 20, 0.01F));

        futures.add(generateAffix(cache, "mekanism/laser/energy_capacity", "affix.apoex.mekanism.laser.energy_capacity", capacityValues, "apoex:laser"));
        futures.add(generateAffix(cache, "mekanism/laser/tick_speed", "affix.apoex.mekanism.laser.tick_speed", tickSpeedValues, "apoex:laser"));
        futures.add(generateAffix(cache, "mekanism/laser/energy_efficiency", "affix.apoex.mekanism.laser.energy_efficiency", efficiencyValues, "apoex:laser"));
        futures.add(generateAffix(cache, "mekanism/laser/heat_efficiency", "affix.apoex.mekanism.laser.heat_efficiency", laserHeatEfficiencyValues, "apoex:laser"));
        futures.add(generateAffix(cache, "mekanism/laser/heat_multiplier", "affix.apoex.mekanism.laser.heat_multiplier", generationValues, "apoex:laser"));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<?> generateMultiblockAffix(CachedOutput cache, String multiblockName, String affixType, Map<String, Step> values, boolean isGenerator) {
        String pathPrefix = isGenerator ? "mekanism/generator/multiblock/" : "mekanism/multiblock/";
        String translationKeyPrefix = isGenerator ? "affix.apoex.mekanism.generator.multiblock." : "affix.apoex.mekanism.multiblock.";
        
        String path = pathPrefix + multiblockName + "/" + affixType;
        String translationKey = translationKeyPrefix + multiblockName + "." + affixType;
        String type = "apoex:" + multiblockName;

        return generateAffix(cache, path, translationKey, values, type);
    }

    private CompletableFuture<?> generateAffix(CachedOutput cache, String name, String desc, Map<String, Step> valuesMap, String... types) {
        Map<String, Step> standardValues = new HashMap<>();
        Map<String, Step> additionsValues = new HashMap<>();

        for (Map.Entry<String, Step> entry : valuesMap.entrySet()) {
            if (entry.getKey().startsWith("apotheotic_additions:")) {
                additionsValues.put(entry.getKey(), entry.getValue());
            } else {
                standardValues.put(entry.getKey(), entry.getValue());
            }
        }

        List<CompletableFuture<?>> futures = new ArrayList<>();

        if (!standardValues.isEmpty()) {
            futures.add(saveAffix(cache, name, desc, standardValues, null, types));
        }

        if (!additionsValues.isEmpty()) {
            futures.add(saveAffix(cache, name + "_additions", desc, additionsValues, "apotheotic_additions", types));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<?> saveAffix(CachedOutput cache, String filename, String desc, Map<String, Step> valuesMap, String requiredMod, String... types) {
        Path path = this.output.getOutputFolder().resolve("data/apoex/affixes/" + filename + ".json");
        JsonObject obj = new JsonObject();

        if (requiredMod != null) {
            JsonArray conditions = new JsonArray();
            JsonObject condition = new JsonObject();
            condition.addProperty("type", "forge:mod_loaded");
            condition.addProperty("modid", requiredMod);
            conditions.add(condition);
            obj.add("conditions", conditions);
        }

        obj.addProperty("type", "apoex:mekanism_stat");
        obj.addProperty("desc", desc);

        JsonArray typesArray = new JsonArray();
        for (String type : types) {
            typesArray.add(type);
        }
        obj.add("types", typesArray);

        JsonObject values = new JsonObject();

        for (Map.Entry<String, Step> entry : valuesMap.entrySet()) {
            JsonObject rarityObj = new JsonObject();
            Step step = entry.getValue();
            rarityObj.addProperty("min", step.min);
            rarityObj.addProperty("steps", step.steps);
            rarityObj.addProperty("step", step.step);

            values.add(entry.getKey(), rarityObj);
        }
        obj.add("values", values);

        return DataProvider.saveStable(cache, obj, path);
    }

    @Override
    public String getName() {
        return "ApoEX Affixes";
    }

    private static class Step {
        float min;
        int steps;
        float step;

        public Step(float min, int steps, float step) {
            this.min = min;
            this.steps = steps;
            this.step = step;
        }
    }
}