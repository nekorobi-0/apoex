package com.youtyan.apoex.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TrueDamageAffixData implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;

    public TrueDamageAffixData(DataGenerator gen) {
        this.output = gen.getPackOutput();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        Map<String, Step> trueDamageValues = new HashMap<>();
        trueDamageValues.put("apotheosis:common", new Step(0.05F, 10, 0.005F));
        trueDamageValues.put("apotheosis:uncommon", new Step(0.10F, 10, 0.01F));
        trueDamageValues.put("apotheosis:rare", new Step(0.15F, 10, 0.01F));
        trueDamageValues.put("apotheosis:epic", new Step(0.20F, 10, 0.01F));
        trueDamageValues.put("apotheosis:mythic", new Step(0.25F, 10, 0.01F));
        trueDamageValues.put("apotheosis:ancient", new Step(0.30F, 10, 0.01F));
        trueDamageValues.put("apotheotic_additions:esoteric", new Step(0.40F, 10, 0.01F));
        trueDamageValues.put("apotheotic_additions:heirloom", new Step(0.50F, 10, 0.01F));
        trueDamageValues.put("apotheotic_additions:artifact", new Step(0.60F, 10, 0.01F));

        futures.add(generateAffix(cache, "true_damage", "affix.apoex.true_damage", trueDamageValues));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<?> generateAffix(CachedOutput cache, String name, String desc, Map<String, Step> valuesMap) {
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
            futures.add(saveAffix(cache, name, desc, standardValues, null));
        }

        if (!additionsValues.isEmpty()) {
            futures.add(saveAffix(cache, name + "_additions", desc, additionsValues, "apotheotic_additions"));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<?> saveAffix(CachedOutput cache, String filename, String desc, Map<String, Step> valuesMap, String requiredMod) {
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

        obj.addProperty("type", "apoex:true_damage");

        JsonArray types = new JsonArray();
        types.add("heavy_weapon");
        types.add("sword");
        types.add("trident");
        types.add("bow");
        types.add("crossbow");
        types.add("pickaxe");
        types.add("shovel");
        types.add("apotheosis_modern_ragnarok:full_auto");
        types.add("apotheosis_modern_ragnarok:full_auto");
        types.add("apotheosis_modern_ragnarok:semi_auto");
        types.add("apotheosis_modern_ragnarok:bolt_action");

        obj.add("types", types);

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
        return "ApoEX True Damage Affix";
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