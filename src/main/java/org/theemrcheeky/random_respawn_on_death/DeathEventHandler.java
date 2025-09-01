package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

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

        // Check if player has a respawn position set (bed, respawn anchor, etc.)
        boolean hasSpawnPoint = player.getRespawnPosition() != null && player.getRespawnDimension() == Level.OVERWORLD;

        // If player is returning from End after defeating dragon, send them to their bed if they have one
        if (event.isEndConquered()) {
            if (hasSpawnPoint) {
                return; // Let vanilla handle bed spawn
            }
            // If no bed, continue with random spawn
        } else if (hasSpawnPoint) {
            return; // Normal death with bed set - use bed spawn
        }

        // Get the player's death position (stored as their last death location)
        BlockPos deathPos = player.getLastDeathLocation().map(globalPos -> globalPos.pos()).orElse(player.blockPosition());

        // Calculate random respawn position within configured distance
        BlockPos randomPos = getRandomRespawnPosition(deathPos, Config.respawnDistance);

        // Find a safe spawn location
        BlockPos safePos = findSafeSpawnLocation(level, randomPos);

        // Teleport player to the safe location
        player.teleportTo(level, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5, player.getYRot(), player.getXRot());
    }

    private static BlockPos getRandomRespawnPosition(BlockPos deathPos, int maxDistance) {
        // Generate random angle
        double angle = random.nextDouble() * 2 * Math.PI;

        // Generate random distance within the configured range
        int distance = 100 + random.nextInt(maxDistance - 100 + 1);

        // Calculate new position
        int newX = deathPos.getX() + (int) (Math.cos(angle) * distance);
        int newZ = deathPos.getZ() + (int) (Math.sin(angle) * distance);

        return new BlockPos(newX, deathPos.getY(), newZ);
    }

    private static BlockPos findSafeSpawnLocation(ServerLevel level, BlockPos startPos) {
        // Start from the highest block and work downward to find the surface
        int surfaceY = findSurfaceLevel(level, startPos.getX(), startPos.getZ());

        if (surfaceY != -1) {
            BlockPos surfacePos = new BlockPos(startPos.getX(), surfaceY, startPos.getZ());

            // Verify it's a safe spawn location (solid block with air above)
            if (level.getBlockState(surfacePos).isCollisionShapeFullBlock(level, surfacePos) && 
                level.getBlockState(surfacePos.above()).isAir() && 
                level.getBlockState(surfacePos.above(2)).isAir()) {

                return surfacePos.above(); // Return the air block above the solid block
            }
        }

        // Fallback: try to find any surface-exposed safe location in a small area
        for (int xOffset = -2; xOffset <= 2; xOffset++) {
            for (int zOffset = -2; zOffset <= 2; zOffset++) {
                int fallbackY = findSurfaceLevel(level, startPos.getX() + xOffset, startPos.getZ() + zOffset);
                if (fallbackY != -1) {
                    BlockPos fallbackPos = new BlockPos(startPos.getX() + xOffset, fallbackY, startPos.getZ() + zOffset);
                    if (level.getBlockState(fallbackPos).isCollisionShapeFullBlock(level, fallbackPos) && 
                        level.getBlockState(fallbackPos.above()).isAir() && 
                        level.getBlockState(fallbackPos.above(2)).isAir()) {

                        return fallbackPos.above();
                    }
                }
            }
        }

        // Ultimate fallback: spawn at world surface level
        return new BlockPos(startPos.getX(), level.getSeaLevel() + 1, startPos.getZ());
    }

    private static int findSurfaceLevel(ServerLevel level, int x, int z) {
        // Start from max build height and find the first solid block exposed to sky
        for (int y = level.getMaxBuildHeight() - 1; y >= level.getSeaLevel(); y--) {
            BlockPos checkPos = new BlockPos(x, y, z);

            // Check if this block is solid and has sky access above it
            if (level.getBlockState(checkPos).isCollisionShapeFullBlock(level, checkPos)) {
                // Verify sky access - check if there's a clear path to the sky
                if (hasDirectSkyAccess(level, checkPos.above())) {
                    return y;
                }
            }
        }
        return -1; // No surface found
    }

    private static boolean hasDirectSkyAccess(ServerLevel level, BlockPos pos) {
        // Check if there's a clear path to the sky (no solid blocks above)
        for (int y = pos.getY(); y < level.getMaxBuildHeight(); y++) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());
            if (level.getBlockState(checkPos).isCollisionShapeFullBlock(level, checkPos)) {
                return false; // Blocked by a solid block
            }
        }
        return true; // Clear path to sky
    }
}
