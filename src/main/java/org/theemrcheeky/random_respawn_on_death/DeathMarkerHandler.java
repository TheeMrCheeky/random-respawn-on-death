package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = Random_respawn_on_death.MODID)
public class DeathMarkerHandler {
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Check if player is in temporary hardcore mode
            if (ModAdvancements.isInTemporaryHardcoreMode(player)) {
                // Handle hardcore death (sets to spectator mode)
                ModAdvancements.handleHardcoreDeath(player);
                return; // Don't create death marker for hardcore deaths
            }
            
            // Store death location for marker creation on respawn
            DeathMarkerManager.createMarker(player, player.blockPosition());
            
            // Increment death count for advancement tracking
            ModAdvancements.incrementDeathCount(player);
        }
    }
}
