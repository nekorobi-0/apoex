package com.youtyan.apoex.compat.mekanism.mekanismaddon.MekanismExtras;

import moffy.addonapi.AddonMixinPlugin;

public class ApoExMekanismExtrasMixinPlugin extends AddonMixinPlugin {
    @Override
    public String[] getRequiredModIds() {return new String[]{"mekanism_extras"};
    }
}