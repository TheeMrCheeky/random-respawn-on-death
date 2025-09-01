package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = Random_respawn_on_death.MODID)
public class HardcoreModeReminder {
    
    private static int tickCounter = 0;
    private static final int REMINDER_INTERVAL = 1200; // 60 seconds at 20 tps
    
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        
        if (tickCounter >= REMINDER_INTERVAL) {
            tickCounter = 0;
            
            // Check all players for hardcore mode
            event.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (ModAdvancements.isInTemporaryHardcoreMode(player)) {
                    // Send actionbar reminder
                    player.displayClientMessage(Component.literal(
                        "§c💀 CURSED HEARTS §c💀 §7- Death is Permanent - §6Eat Golden Apples to Escape"
                    ), true);
                    
                    // Occasional particle effect
                    if (Math.random() < 0.1) { // 10% chance per reminder
                        // Send wither particle effect around player
                        player.serverLevel().sendParticles(
                            net.minecraft.core.particles.ParticleTypes.SMOKE,
                            player.getX(), player.getY() + 2, player.getZ(),
                            3, 0.5, 0.5, 0.5, 0.02
                        );
                    }
                }
            });
        }
    }
}
