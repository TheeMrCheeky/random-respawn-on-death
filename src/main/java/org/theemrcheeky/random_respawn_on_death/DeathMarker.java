package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import java.util.UUID;

public class DeathMarker {
    private final BlockPos deathPos;
    private final UUID playerId;
    private final long creationTime;
    private static final double COLLECTION_DISTANCE = 3.0D; // Distance to consider marker "reached"
    private static final long MIN_TIME_BEFORE_COLLECTION = 5000L; // 5 seconds minimum before collection
    private static final double MARKER_HEIGHT = 2.5D;
    private static final double PARTICLE_SPREAD = 0.25D;

    public DeathMarker(BlockPos pos, UUID playerUuid) {
        this.deathPos = pos;
        this.playerId = playerUuid;
        this.creationTime = System.currentTimeMillis();
    }

    public boolean isReached(ServerPlayer player) {
        if (player == null || !player.getUUID().equals(playerId)) return false;
        
        // Prevent immediate collection - marker must exist for at least 5 seconds
        if (System.currentTimeMillis() - creationTime < MIN_TIME_BEFORE_COLLECTION) {
            return false;
        }
        
        Vec3 deathLoc = Vec3.atCenterOf(deathPos);
        Vec3 playerLoc = player.position();
        double distance = deathLoc.distanceTo(playerLoc);
        
        return distance <= COLLECTION_DISTANCE;
    }

    public void tick(ServerPlayer player) {
        if (player == null || !player.getUUID().equals(playerId)) return;
        
        Level level = player.level();
        if (level.isClientSide()) return;

        // Calculate distance first
        Vec3 deathLoc = Vec3.atCenterOf(deathPos);
        Vec3 playerLoc = player.position();
        double distance = deathLoc.distanceTo(playerLoc);
        int distanceBlocks = (int) distance;

        // Create particles for the marker on server side
        if (level instanceof ServerLevel serverLevel) {
            double x = deathPos.getX() + 0.5;
            double y = deathPos.getY() + MARKER_HEIGHT;
            double z = deathPos.getZ() + 0.5;

            // More intense particles when player is getting close
            int particleCount;
            if (distance <= 50) {
                particleCount = 12; // Very intense particles when very close
            } else if (distance <= 99) {
                particleCount = 8;  // More particles when nearby
            } else {
                particleCount = 4;  // Standard particles when far
            }
            
            // Skull shape particles
            for (int i = 0; i < particleCount; i++) {
                double angle = (i * Math.PI / (particleCount / 2));
                double offsetX = Math.cos(angle) * PARTICLE_SPREAD;
                double offsetZ = Math.sin(angle) * PARTICLE_SPREAD;
                
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    x + offsetX, y, z + offsetZ,
                    1, 0, 0, 0, 0);
            }
            
            // Add collection effect when very close
            if (distance <= COLLECTION_DISTANCE) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    x, y, z, 5, 0.5, 0.5, 0.5, 0);
            }
        }

        // Display appropriate message based on distance
        String message;
        if (distance <= COLLECTION_DISTANCE) {
            message = "§a✓ Death Location Reached! §a✓";
        } else if (distance <= 50) {
            message = "§a⚔ Death Location Nearby §a⚔";
        } else if (distance <= 99) {
            message = "§e⚔ Death Location Nearby §e⚔";
        } else {
            message = "§c⚔ Death Location: " + distanceBlocks + " blocks away §c⚔";
        }
        
        player.displayClientMessage(Component.literal(message), true);
    }

    public BlockPos getDeathPos() {
        return deathPos;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
