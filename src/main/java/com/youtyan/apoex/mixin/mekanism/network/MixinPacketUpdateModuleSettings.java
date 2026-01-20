package com.youtyan.apoex.mixin.mekanism.network;

import com.youtyan.apoex.integration.mekanism.MekSuitGemIntegration;
import com.youtyan.apoex.util.mekanism.ApoExModuleHelper;
import mekanism.api.gear.IModule;
import mekanism.api.gear.ModuleData;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.network.to_server.PacketUpdateModuleSettings;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PacketUpdateModuleSettings.class)
public abstract class MixinPacketUpdateModuleSettings {

    @Shadow(remap = false) private int slotId;
    @Shadow(remap = false) private ModuleData<?> moduleType;
    @Shadow(remap = false) private int dataIndex;

    @Shadow(remap = false)
    protected abstract <TYPE> void setValue(ModuleConfigItem<TYPE> moduleConfigItem);

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectHandle(NetworkEvent.Context context, CallbackInfo ci) {
        context.enqueueWork(() -> {
            Player player = context.getSender();
            if (player == null) return;

            ItemStack stack = player.getInventory().getItem(slotId);
            if (MekSuitGemIntegration.isMekArmor(stack)) {
                IModule<?> module = ApoExModuleHelper.getModule(stack, moduleType);
                if (module instanceof Module<?> impl) {
                    List<ModuleConfigItem<?>> configItems = impl.getConfigItems();
                    if (dataIndex >= 0 && dataIndex < configItems.size()) {
                        ModuleConfigItem<?> configItem = configItems.get(dataIndex);
                        if (configItem != null) {
                            setValue(configItem);
                        }
                    }
                }
            }
        });

        Player player = context.getSender();
        if (player != null) {
            ItemStack stack = player.getInventory().getItem(slotId);
            if (MekSuitGemIntegration.isMekArmor(stack)) {
                context.setPacketHandled(true);
                ci.cancel();
            }
        }
    }
}