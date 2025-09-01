package org.theemrcheeky.random_respawn_on_death.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientPacketHandler {
    
    public static void handleHardcoreModePacket(final HardcoreModePacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.getPersistentData().putBoolean("random_respawn_hardcore_mode", packet.hardcoreMode());
            }
            // Note: GUI mixin is disabled for now due to method mapping issues
            // GuiMixin.setTemporaryHardcoreMode(packet.hardcoreMode());
        });
    }
}
