package com.youtyan.apoex.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;

public class MekanismStatAffix extends Affix {

    public static final Codec<Map<LootRarity, StepFunction>> VALUES_CODEC = LootRarity.mapCodec(StepFunction.CODEC);

    public static final Codec<MekanismStatAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    VALUES_CODEC.fieldOf("values").forGetter(a -> a.values),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(a -> a.types))
            .apply(inst, MekanismStatAffix::new));

    protected final Map<LootRarity, StepFunction> values;
    protected final Set<LootCategory> types;

    public MekanismStatAffix(Map<LootRarity, StepFunction> values, Set<LootCategory> types) {
        super(AffixType.STAT);
        this.values = values;
        this.types = types;
    }

    @Override
    public MutableComponent getDescription(ItemStack stack, LootRarity rarity, float level) {
        // ツールチップは共通化するため、_additionsを取り除く
        ResourceLocation id = this.getId();
        String path = id.getPath().replace("_additions", "");
        String key = String.format("affix.%s.%s.desc", id.getNamespace(), path.replace('/', '.'));
        
        float mod = this.getModifier(rarity, level);
        if (mod == 0) return Component.empty();
        return Component.translatable(key, fmt(mod * 100));
    }

    @Override
    public Component getName(boolean prefix) {
        // Affix名は区別するため、_additionsを取り除かない
        ResourceLocation id = this.getId();
        String path = id.getPath();
        String key = String.format("affix.%s.%s", id.getNamespace(), path.replace('/', '.'));
        if (prefix) return Component.translatable(key);
        return Component.translatable(key + ".suffix");
    }

    public float getModifier(LootRarity rarity, float level) {
        StepFunction func = this.values.get(rarity);
        if (func == null) return 0;
        return func.get(level);
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        return this.types.contains(cat);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}