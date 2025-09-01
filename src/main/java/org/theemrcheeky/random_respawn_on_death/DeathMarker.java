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
    private final long startTime;
    private static final int DURATION_TICKS = 200; // 10 seconds
    private static final double MARKER_HEIGHT = 2.5D;
    private static final double PARTICLE_SPREAD = 0.25D;

    public DeathMarker(BlockPos pos, UUID playerUuid) {
        this.deathPos = pos;
        this.playerId = playerUuid;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > DURATION_TICKS * 50;
    }

    public void tick(ServerPlayer player) {
        if (player == null || !player.getUUID().equals(playerId)) return;
        
        Level level = player.level();
        if (level.isClientSide()) return;

        // Create particles for the marker on server side
        if (level instanceof ServerLevel serverLevel) {
            double x = deathPos.getX() + 0.5;
            double y = deathPos.getY() + MARKER_HEIGHT;
            double z = deathPos.getZ() + 0.5;

            // Skull shape particles
            for (int i = 0; i < 4; i++) {
                double angle = (i * Math.PI / 2);
                double offsetX = Math.cos(angle) * PARTICLE_SPREAD;
                double offsetZ = Math.sin(angle) * PARTICLE_SPREAD;
                
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    x + offsetX, y, z + offsetZ,
                    1, 0, 0, 0, 0);
            }
        }

        // Calculate and display distance
        Vec3 deathLoc = Vec3.atCenterOf(deathPos);
        Vec3 playerLoc = player.position();
        int distance = (int) deathLoc.distanceTo(playerLoc);
        
        String message = "§c⚔ Death Location: " + distance + " blocks away §c⚔";
        player.displayClientMessage(Component.literal(message), true);
    }

    public BlockPos getDeathPos() {
        return deathPos;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
