package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Random;

@EventBusSubscriber(modid = Random_respawn_on_death.MODID)
public class DeathEventHandler {
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel level = player.serverLevel();

        // Only apply respawn logic in the overworld
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        // Check if player has a respawn position set
        boolean hasSpawnPoint = player.getRespawnPosition() != null && player.getRespawnDimension() == Level.OVERWORLD;

        // If player is returning from End after defeating dragon, send them to their bed if they have one
        if (event.isEndConquered()) {
            if (hasSpawnPoint) {
                return; // Let vanilla handle bed spawn
            }
        } else if (hasSpawnPoint) {
            return; // Normal death with bed set - use bed spawn
        }

        // Get the player's death position
        BlockPos deathPos = player.getLastDeathLocation().map(globalPos -> globalPos.pos()).orElse(player.blockPosition());

        // Calculate random respawn position within configured distance
        BlockPos randomPos = getRandomRespawnPosition(deathPos, Config.respawnDistance);

        // Find a safe spawn location
        BlockPos safePos = findSafeSpawnLocation(level, randomPos);

        // Create death marker at the death location
        DeathMarkerManager.createMarker(player, deathPos);

        // Teleport player to the safe location
        player.teleportTo(level, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5, player.getYRot(), player.getXRot());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        // Update death markers for all players
        for (ServerLevel serverLevel : event.getServer().getAllLevels()) {
            for (ServerPlayer player : serverLevel.players()) {
                DeathMarkerManager.updateMarkers(player);
            }
        }
    }

    private static BlockPos getRandomRespawnPosition(BlockPos deathPos, int maxDistance) {
        double angle = random.nextDouble() * 2 * Math.PI;
        int distance = 100 + random.nextInt(maxDistance - 100 + 1);
        int newX = deathPos.getX() + (int) (Math.cos(angle) * distance);
        int newZ = deathPos.getZ() + (int) (Math.sin(angle) * distance);
        return new BlockPos(newX, deathPos.getY(), newZ);
    }

    private static BlockPos findSafeSpawnLocation(ServerLevel level, BlockPos startPos) {
        int surfaceY = findTrueSurfaceLevel(level, startPos.getX(), startPos.getZ());

        if (surfaceY != -1) {
            BlockPos surfacePos = new BlockPos(startPos.getX(), surfaceY, startPos.getZ());
            if (isSafeSpawnLocation(level, surfacePos)) {
                return surfacePos.above();
            }
        }

        // Search in expanding rings for a safe location
        for (int radius = 1; radius <= 10; radius++) {
            for (int xOffset = -radius; xOffset <= radius; xOffset++) {
                for (int zOffset = -radius; zOffset <= radius; zOffset++) {
                    // Skip positions we've already checked in smaller radii
                    if (Math.abs(xOffset) < radius && Math.abs(zOffset) < radius) continue;
                    
                    int nearbyY = findTrueSurfaceLevel(level, startPos.getX() + xOffset, startPos.getZ() + zOffset);
                    if (nearbyY != -1) {
                        BlockPos nearbyPos = new BlockPos(startPos.getX() + xOffset, nearbyY, startPos.getZ() + zOffset);
                        if (isSafeSpawnLocation(level, nearbyPos)) {
                            return nearbyPos.above();
                        }
                    }
                }
            }
        }

        // Ultimate fallback - find world spawn and go up from there
        BlockPos worldSpawn = level.getSharedSpawnPos();
        int spawnSurfaceY = findTrueSurfaceLevel(level, worldSpawn.getX(), worldSpawn.getZ());
        if (spawnSurfaceY != -1) {
            return new BlockPos(worldSpawn.getX(), spawnSurfaceY + 1, worldSpawn.getZ());
        }

        return new BlockPos(startPos.getX(), level.getSeaLevel() + 10, startPos.getZ()); // Last resort - spawn well above sea level
    }

    private static boolean isSafeSpawnLocation(ServerLevel level, BlockPos pos) {
        // Check if there's a solid foundation
        if (!level.getBlockState(pos).isCollisionShapeFullBlock(level, pos)) {
            return false;
        }
        
        // Check for 3 blocks of air space (head, body, and breathing room)
        for (int i = 1; i <= 3; i++) {
            if (!level.getBlockState(pos.above(i)).isAir()) {
                return false;
            }
        }
        
        // Check if this location can see the sky (not in a cave)
        if (!level.canSeeSky(pos.above())) {
            return false;
        }
        
        // Check for dangerous blocks nearby (lava, fire, etc.)
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                for (int yOffset = 0; yOffset <= 2; yOffset++) {
                    BlockPos checkPos = pos.offset(xOffset, yOffset, zOffset);
                    if (level.getBlockState(checkPos).getBlock().toString().contains("lava") ||
                        level.getBlockState(checkPos).getBlock().toString().contains("fire") ||
                        level.getBlockState(checkPos).getBlock().toString().contains("magma")) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

    private static int findTrueSurfaceLevel(ServerLevel level, int x, int z) {
        int maxY = level.getMaxBuildHeight();
        int minY = level.getMinBuildHeight();

        // Start from the top and find the first solid block that can see the sky
        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isCollisionShapeFullBlock(level, pos)) {
                // Check if this block can see the sky (not underground)
                if (level.canSeeSky(pos.above())) {
                    // Make sure there's air above it
                    if (level.getBlockState(pos.above()).isAir()) {
                        // Double-check it's actually surface by ensuring we're not too deep
                        int surfaceLevel = level.getHeight();
                        if (y >= surfaceLevel - 20) { // Allow some variation for hills/mountains
                            return y;
                        }
                    }
                }
            }
        }
        
        // If no true surface found, try to find the highest solid block as fallback
        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isCollisionShapeFullBlock(level, pos) &&
                level.getBlockState(pos.above()).isAir()) {
                return y;
            }
        }
        
        return -1;
    }
}
