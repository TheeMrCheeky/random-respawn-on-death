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
        
        // Remove any existing marker for this player first
        if (activeMarkers.containsKey(playerId)) {
            activeMarkers.remove(playerId);
        }
        
        DeathMarker marker = new DeathMarker(deathPos, playerId);
        activeMarkers.put(playerId, marker);
    }
    
    public static void updateMarkers(ServerPlayer player) {
        DeathMarker marker = activeMarkers.get(player.getUUID());
        if (marker != null) {
            if (marker.isReached(player)) {
                // Player reached the death location - remove marker and grant advancement
                activeMarkers.remove(player.getUUID());
                
                // Grant achievement for first death marker collection
                ModAdvancements.grantFirstDeathMarkerAdvancement(player);
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
    
    public static void clearAllMarkers() {
        activeMarkers.clear();
    }
    
    public static int getActiveMarkerCount() {
        return activeMarkers.size();
    }
}
