package com.youtyan.apoex.datagen;

import com.youtyan.apoex.ApoEXMod;
import com.youtyan.apoex.registry.ApoExDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ApoExDamageTypeTagsProvider extends DamageTypeTagsProvider {

    public ApoExDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, pProvider, ApoEXMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ApoExDamageTypes.TRUE_DAMAGE);
        this.tag(DamageTypeTags.BYPASSES_SHIELD).add(ApoExDamageTypes.TRUE_DAMAGE);
        this.tag(DamageTypeTags.BYPASSES_INVULNERABILITY).add(ApoExDamageTypes.TRUE_DAMAGE);
        this.tag(DamageTypeTags.BYPASSES_RESISTANCE).add(ApoExDamageTypes.TRUE_DAMAGE);
        this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(ApoExDamageTypes.TRUE_DAMAGE);
    }
}
