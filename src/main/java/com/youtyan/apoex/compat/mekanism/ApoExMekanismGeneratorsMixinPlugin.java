package com.youtyan.apoex.compat.mekanism;

import moffy.addonapi.AddonMixinPlugin;

public class ApoExMekanismGeneratorsMixinPlugin extends AddonMixinPlugin {

    @Override
    public String[] getRequiredModIds() {
        return new String[] { "mekanismgenerators" };
    }
}