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
            // Store death location for marker creation on respawn
            DeathMarkerManager.createMarker(player, player.blockPosition());
        }
    }
}
