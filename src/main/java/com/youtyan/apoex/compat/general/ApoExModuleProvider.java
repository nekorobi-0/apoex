package com.youtyan.apoex.compat.general;

import com.youtyan.apoex.compat.mekanism.ApoExMekanismCompatModule;
import moffy.addonapi.AddonModuleProvider;
import com.youtyan.apoex.ApoEXMod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ApoExModuleProvider extends AddonModuleProvider {

    public ApoExModuleProvider(FMLJavaModLoadingContext context) {
        super(context);
    }

    @Override
    public void registerRawModules() {
        addRawModule(
                "default_compat",
                "Default Compat",
                ApoExModule.class,
                new String[] { "apotheosis" },
                true
        );
        addRawModule(
                "mekanism_compat",
                "Mekanism Compat",
                ApoExMekanismCompatModule.class,
                new String[] { "apotheosis", "mekanism" },
                true
        );
    }

    @Override
    public String getModId() {
        return ApoEXMod.MODID;
    }
}
