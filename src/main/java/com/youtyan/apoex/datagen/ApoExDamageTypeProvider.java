package com.youtyan.apoex.datagen;

import com.youtyan.apoex.ApoEXMod;
import com.youtyan.apoex.registry.ApoExDamageTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ApoExDamageTypeProvider extends DatapackBuiltinEntriesProvider {

    public static final net.minecraft.core.RegistrySetBuilder BUILDER = new net.minecraft.core.RegistrySetBuilder()
            .add(net.minecraft.core.registries.Registries.DAMAGE_TYPE, ApoExDamageTypeProvider::bootstrap);

    public ApoExDamageTypeProvider(DataGenerator output, CompletableFuture<net.minecraft.core.HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output.getPackOutput(), registries, BUILDER, Set.of(ApoEXMod.MODID));
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(ApoExDamageTypes.TRUE_DAMAGE, new DamageType("apoex.true_damage", DamageScaling.ALWAYS, 0.1F, DamageEffects.HURT));
    }
}