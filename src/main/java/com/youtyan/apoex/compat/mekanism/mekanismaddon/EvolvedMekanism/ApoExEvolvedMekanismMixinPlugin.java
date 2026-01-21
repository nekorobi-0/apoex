package com.youtyan.apoex.compat.mekanism.mekanismaddon.EvolvedMekanism;

import moffy.addonapi.AddonMixinPlugin;

public class ApoExEvolvedMekanismMixinPlugin extends AddonMixinPlugin {
    @Override
    public String[] getRequiredModIds() {
        return new String[]{"evolvedmekanism"};
    }
}
