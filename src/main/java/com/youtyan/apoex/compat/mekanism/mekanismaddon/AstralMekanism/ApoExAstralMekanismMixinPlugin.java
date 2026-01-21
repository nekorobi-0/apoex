package com.youtyan.apoex.compat.mekanism.mekanismaddon.AstralMekanism;

import moffy.addonapi.AddonMixinPlugin;

public class ApoExAstralMekanismMixinPlugin extends AddonMixinPlugin {

    @Override
    public String[] getRequiredModIds() {
        return new String[] { "astral_mekanism" };
    }
}