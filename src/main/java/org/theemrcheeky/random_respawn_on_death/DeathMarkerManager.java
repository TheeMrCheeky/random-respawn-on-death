package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DeathMarkerManager {
    private static final Map<UUID, DeathMarker> activeMarkers = new ConcurrentHashMap<>();
    
    public static void createMarker(ServerPlayer player, BlockPos deathPos) {
        UUID playerId = player.getUUID();
        DeathMarker marker = new DeathMarker(deathPos, playerId);
        activeMarkers.put(playerId, marker);
    }
    
    public static void updateMarkers(ServerPlayer player) {
        DeathMarker marker = activeMarkers.get(player.getUUID());
        if (marker != null) {
            if (marker.isExpired()) {
                activeMarkers.remove(player.getUUID());
            } else {
                marker.tick(player);
            }
        }
    }
    
    public static void removeMarker(UUID playerId) {
        activeMarkers.remove(playerId);
    }
    
    public static boolean hasActiveMarker(UUID playerId) {
        return activeMarkers.containsKey(playerId);
    }
}
