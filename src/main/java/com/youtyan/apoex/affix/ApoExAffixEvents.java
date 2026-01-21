package com.youtyan.apoex.affix;

import com.youtyan.apoex.ApoEXMod;
import com.youtyan.apoex.registry.ApoExDamageTypes;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = ApoEXMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ApoExAffixEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getSource().is(ApoExDamageTypes.TRUE_DAMAGE)) return;

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            ItemStack stack = attacker.getMainHandItem();
            if (stack.isEmpty()) return;

            Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);
            ResourceLocation trueDamageId = new ResourceLocation(ApoEXMod.MODID, "true_damage");
            Affix affix = AffixRegistry.INSTANCE.getValue(trueDamageId);
            
            if (affix instanceof TrueDamageAffix trueDamageAffix) {
                for (Map.Entry<DynamicHolder<? extends Affix>, AffixInstance> entry : affixes.entrySet()) {
                    if (entry.getKey().get() == affix) {
                        AffixInstance inst = entry.getValue();
                        trueDamageAffix.applyTrueDamage(event, inst);
                        break;
                    }
                }
            }
        }
    }
}
