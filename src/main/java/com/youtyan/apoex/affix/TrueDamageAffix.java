package com.youtyan.apoex.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.youtyan.apoex.ApoEXMod;
import com.youtyan.apoex.registry.ApoExDamageTypes;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Map;
import java.util.Set;

public class TrueDamageAffix extends Affix {

    public static final Codec<TrueDamageAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    LootRarity.mapCodec(StepFunction.CODEC).fieldOf("values").forGetter(a -> a.values),
                    PlaceboCodecs.setOf(Codec.STRING).fieldOf("types").forGetter(a -> a.types))
            .apply(inst, TrueDamageAffix::new));

    protected final Map<LootRarity, StepFunction> values;
    protected final Set<String> types;

    public TrueDamageAffix(Map<LootRarity, StepFunction> values, Set<String> types) {
        super(AffixType.ABILITY);
        this.values = values;
        this.types = types;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        if (this.types.isEmpty() || this.types.contains(cat.getName())) {
            return this.values.containsKey(rarity);
        }
        return false;
    }

    @Override
    public MutableComponent getDescription(ItemStack stack, LootRarity rarity, float level) {
        return Component.translatable("affix." + this.getId() + ".desc", fmt(100 * this.getTrueLevel(rarity, level)));
    }

    @Override
    public Component getAugmentingText(ItemStack stack, LootRarity rarity, float level) {
        MutableComponent comp = this.getDescription(stack, rarity, level);

        Component minComp = Component.translatable("%s%%", fmt(100 * this.getTrueLevel(rarity, 0)));
        Component maxComp = Component.translatable("%s%%", fmt(100 * this.getTrueLevel(rarity, 1)));
        return comp.append(valueBounds(minComp, maxComp));
    }

    private float getTrueLevel(LootRarity rarity, float level) {
        return this.values.get(rarity).get(level);
    }

    public void applyTrueDamage(LivingHurtEvent event, AffixInstance inst) {
        LivingEntity target = event.getEntity();
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            float damage = event.getAmount();
            float multiplier = this.getTrueLevel(inst.rarity().get(), inst.level());
            float trueDamage = damage * multiplier;

            if (trueDamage < 1.0F) {
                trueDamage = 1.0F;
            }

            var registry = attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
            var holder = registry.getHolderOrThrow(ApoExDamageTypes.TRUE_DAMAGE);
            DamageSource source = new DamageSource(holder, attacker);

            target.invulnerableTime = 0;
            target.hurt(source, trueDamage);
        }
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}