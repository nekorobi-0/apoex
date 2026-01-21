package com.youtyan.apoex.registry;

import com.youtyan.apoex.ApoEXMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ApoExDamageTypes {
    public static final ResourceKey<DamageType> TRUE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ApoEXMod.MODID, "true_damage"));
}
