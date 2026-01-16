package com.youtyan.apoex.datagen;

import com.youtyan.apoex.ApoEXMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ApoEXMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ApoExDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(event.includeServer(), new AffixData(gen));
    }
}