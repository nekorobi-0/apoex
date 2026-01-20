package com.youtyan.apoex.datagen;

import com.youtyan.apoex.ApoEXMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ApoEXMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ApoExDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(event.includeServer(), new MekanismAffixData(gen));
        gen.addProvider(event.includeServer(), new TrueDamageAffixData(gen));

        ApoExDamageTypeProvider damageTypeProvider = new ApoExDamageTypeProvider(gen, lookupProvider, existingFileHelper);
        gen.addProvider(event.includeServer(), damageTypeProvider);

        gen.addProvider(event.includeServer(), new ApoExDamageTypeTagsProvider(output, damageTypeProvider.getRegistryProvider(), existingFileHelper));
    }
}