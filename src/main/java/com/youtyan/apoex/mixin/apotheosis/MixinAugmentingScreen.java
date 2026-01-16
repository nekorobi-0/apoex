package com.youtyan.apoex.mixin.apotheosis;

import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.augmenting.AugmentingScreen;
import dev.shadowsoffire.apotheosis.adventure.client.AdventureContainerScreen;
import dev.shadowsoffire.apotheosis.adventure.client.DropDownList;
import net.minecraft.network.chat.FormattedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(value = AugmentingScreen.class, remap = false)
public abstract class MixinAugmentingScreen extends AdventureContainerScreen {

    @Shadow protected List<AffixInstance> currentItemAffixes;
    @Shadow protected List<List<FormattedText>> alternativePages;
    @Shadow protected int alternativePage;

    public MixinAugmentingScreen() {
        super(null, null, null);
    }

    @Inject(method = "computeAlternatives", at = @At("HEAD"), cancellable = true)
    private void apoex_safeComputeAlternatives(int selected, CallbackInfo ci) {
        if (selected != DropDownList.NO_SELECTION && selected >= 0 && selected < this.currentItemAffixes.size()) {
            AffixInstance current = this.currentItemAffixes.get(selected);
            if (!current.affix().isBound() || !current.rarity().isBound()) {
                this.alternativePages = Collections.emptyList();
                this.alternativePage = DropDownList.NO_SELECTION;
                ci.cancel();
            }
        }
    }

    @Redirect(method = "computeAlternatives", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"))
    private Object apoex_safeOptionalGet(Optional<Integer> instance) {
        return instance.orElse(0);
    }
}
