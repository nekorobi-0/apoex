package com.youtyan.apoex.affix;

import com.youtyan.apoex.ApoEXMod;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ApoEXMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ApoExAffixes {

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            AffixRegistry.INSTANCE.registerCodec(new ResourceLocation(ApoEXMod.MODID, "mekanism_stat"), MekanismStatAffix.CODEC);
            AffixRegistry.INSTANCE.registerCodec(new ResourceLocation(ApoEXMod.MODID, "true_damage"), TrueDamageAffix.CODEC);
        });
    }
}
