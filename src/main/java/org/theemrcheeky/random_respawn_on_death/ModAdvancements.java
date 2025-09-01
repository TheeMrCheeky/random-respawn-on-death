package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.GameType;
import net.minecraft.world.Difficulty;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.CommandEvent;
import org.theemrcheeky.random_respawn_on_death.network.HardcoreModePacket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Random_respawn_on_death.MODID)
public class ModAdvancements {
    private static final Map<UUID, Boolean> FIRST_DEATH_MARKER_COLLECTED = new HashMap<>();
    private static final Map<UUID, Boolean> HEALTH_REDUCED = new HashMap<>();
    private static final Map<UUID, Boolean> CURSE_BREAKER_GRANTED = new HashMap<>();
    private static final Map<UUID, Integer> DEATH_COUNT = new HashMap<>();
    private static final Map<UUID, GameType> ORIGINAL_GAME_MODE = new HashMap<>();
    private static final Map<UUID, Difficulty> ORIGINAL_WORLD_DIFFICULTY = new HashMap<>();
    private static final Map<UUID, Boolean> ORIGINAL_HARDCORE_STATUS = new HashMap<>();
    
    public static void grantFirstDeathMarkerAdvancement(ServerPlayer player) {
        UUID playerId = player.getUUID();
        
        // Check if player has already gotten this achievement
        if (FIRST_DEATH_MARKER_COLLECTED.getOrDefault(playerId, false)) {
            return;
        }
        
        // Mark as completed
        FIRST_DEATH_MARKER_COLLECTED.put(playerId, true);
        
        // Grant the advancement
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(
            Random_respawn_on_death.MODID, 
            "first_death_marker"
        );
        
        var advancementHolder = player.server.getAdvancements().get(advancementId);
        if (advancementHolder != null) {
            player.getAdvancements().award(advancementHolder, "collected_first_marker");
        }
        
        // Send a custom message as well
        player.sendSystemMessage(Component.literal(
            "§6🏆 Achievement Unlocked: §eBack to the Scene §6🏆"
        ));
        
        // Give player a jack-o'-lantern
        ItemStack jackOLantern = new ItemStack(Items.JACK_O_LANTERN, 1);
        if (!player.getInventory().add(jackOLantern)) {
            player.drop(jackOLantern, false);
        }
        
        // Send message about the jack-o'-lantern
        player.sendSystemMessage(Component.literal(
            "§6You received a §eJack-o'-Lantern §6as a reward!"
        ));
        
        // Reduce health to half
        reduceHealthToHalf(player);
        
        // Auto-grant root advancement on first player interaction
        grantRootAdvancement(player);
    }
    
    private static void reduceHealthToHalf(ServerPlayer player) {
        UUID playerId = player.getUUID();
        
        // Check if health is already reduced for this player
        if (HEALTH_REDUCED.getOrDefault(playerId, false)) {
            return;
        }

        AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            double currentMaxHealth = maxHealthAttribute.getBaseValue();
            double newMaxHealth = currentMaxHealth / 2.0;
            maxHealthAttribute.setBaseValue(newMaxHealth);
            
            // Set current health to the new max health
            player.setHealth((float) newMaxHealth);
            
            HEALTH_REDUCED.put(playerId, true);
            
            // Check if player is in survival mode and switch to hardcore
            GameType currentGameMode = player.gameMode.getGameModeForPlayer();
            if (currentGameMode == GameType.SURVIVAL) {
                ORIGINAL_GAME_MODE.put(playerId, currentGameMode);
                
                // Store original world settings
                Difficulty originalDifficulty = player.serverLevel().getDifficulty();
                ORIGINAL_WORLD_DIFFICULTY.put(playerId, originalDifficulty);
                
                // Store original hardcore status (for future use)
                boolean originalHardcore = player.serverLevel().getLevelData().isHardcore();
                ORIGINAL_HARDCORE_STATUS.put(playerId, originalHardcore);
                
                // Set world to hardcore mode for visual heart effects
                setWorldHardcoreMode(player, true);
                
                // Set world difficulty to Hard and lock it
                if (originalDifficulty != Difficulty.HARD) {
                    player.server.setDifficulty(Difficulty.HARD, true);
                    // Also force the difficulty locked flag
                    player.server.setDifficultyLocked(true);
                }
                
                // Send hardcore mode packet to client for visual effects
                try {
                    PacketDistributor.sendToPlayer(player, new HardcoreModePacket(true));
                } catch (Exception e) {
                    // Continue if packet fails
                }
                
                // Send hardcore warning message
                player.sendSystemMessage(Component.literal(
                    "§4☠ HARDCORE MODE ACTIVATED! ☠"
                ));
                player.sendSystemMessage(Component.literal(
                    "§c💀 Hearts are now cursed - death is permanent!"
                ));
                player.sendSystemMessage(Component.literal(
                    "§c⚔ World difficulty locked to HARD!"
                ));
                player.sendSystemMessage(Component.literal(
                    "§cCommands are disabled in hardcore mode!"
                ));
                player.sendSystemMessage(Component.literal(
                    "§eEat golden apples to restore your health and escape hardcore mode."
                ));
                
                // Send persistent actionbar reminder
                player.displayClientMessage(Component.literal(
                    "§c💀 CURSED HEARTS - Death is Permanent §c💀"
                ), true); // true = actionbar
            }
            
            // Send message about health reduction
            player.sendSystemMessage(Component.literal(
                "§cYour health has been cursed! Eat golden apples to restore it."
            ));
        }
    }
    
    private static void setWorldHardcoreMode(ServerPlayer player, boolean hardcore) {
        try {
            ServerLevel level = player.serverLevel();
            ServerLevelData levelData = (ServerLevelData) level.getLevelData();
            
            // Use reflection to modify the hardcore field in ServerLevelData
            java.lang.reflect.Field hardcoreField = null;
            
            // Get all fields to debug the available field names
            java.lang.reflect.Field[] allFields = levelData.getClass().getDeclaredFields();
            System.out.println("Available fields in ServerLevelData:");
            for (java.lang.reflect.Field field : allFields) {
                if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                    System.out.println("  " + field.getName() + " (" + field.getType().getSimpleName() + ")");
                }
            }
            
            // Try different field names that might contain hardcore setting
            // Including obfuscated field names and more variations
            String[] possibleFieldNames = {
                "hardcore", "isHardcore", "hardcoreMode", "hardCore",
                "f_78626_", "f_78627_", "f_78628_", "f_78629_", "f_78630_", // Common obfuscated patterns
                "m_78626_", "m_78627_", "m_78628_", "m_78629_", "m_78630_", // Method-style obfuscation
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l" // Single letter obfuscation
            };
            
            for (String fieldName : possibleFieldNames) {
                try {
                    hardcoreField = levelData.getClass().getDeclaredField(fieldName);
                    hardcoreField.setAccessible(true);
                    // Check if this is likely the hardcore field by checking its type
                    if (hardcoreField.getType() == boolean.class || hardcoreField.getType() == Boolean.class) {
                        System.out.println("Trying field: " + fieldName);
                        break;
                    }
                } catch (NoSuchFieldException e) {
                    // Try the next field name
                    hardcoreField = null;
                }
            }
            
            // If still not found, try to find by examining all boolean fields
            if (hardcoreField == null) {
                for (java.lang.reflect.Field field : allFields) {
                    if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.setAccessible(true);
                        try {
                            // Check if this could be the hardcore field by looking at current value
                            boolean currentValue = field.getBoolean(levelData);
                            System.out.println("Boolean field " + field.getName() + " = " + currentValue);
                            
                            // If this matches the current hardcore status, it might be the right field
                            if (currentValue == level.getLevelData().isHardcore()) {
                                hardcoreField = field;
                                System.out.println("Found potential hardcore field: " + field.getName());
                                break;
                            }
                        } catch (Exception e) {
                            // Skip this field
                        }
                    }
                }
            }
            
            if (hardcoreField != null) {
                hardcoreField.setAccessible(true);
                hardcoreField.setBoolean(levelData, hardcore);
                System.out.println("Successfully set world hardcore mode to: " + hardcore + " using field: " + hardcoreField.getName());
                
                // Force update client-side by sending difficulty packet
                if (hardcore) {
                    // Send hardcore hearts by setting difficulty and hardcore flag
                    player.connection.send(new ClientboundChangeDifficultyPacket(Difficulty.HARD, true));
                } else {
                    // Send normal hearts
                    Difficulty currentDifficulty = ORIGINAL_WORLD_DIFFICULTY.get(player.getUUID());
                    if (currentDifficulty == null) currentDifficulty = Difficulty.NORMAL;
                    player.connection.send(new ClientboundChangeDifficultyPacket(currentDifficulty, false));
                }
            } else {
                System.err.println("Could not find hardcore field in ServerLevelData");
                
                // Fallback: Use client-side packet only
                if (hardcore) {
                    player.connection.send(new ClientboundChangeDifficultyPacket(Difficulty.HARD, true));
                    System.out.println("Using fallback hardcore hearts display");
                } else {
                    Difficulty currentDifficulty = ORIGINAL_WORLD_DIFFICULTY.get(player.getUUID());
                    if (currentDifficulty == null) currentDifficulty = Difficulty.NORMAL;
                    player.connection.send(new ClientboundChangeDifficultyPacket(currentDifficulty, false));
                    System.out.println("Using fallback normal hearts display");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Failed to set world hardcore mode: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void restoreHealthFromGoldenApple(ServerPlayer player) {
        UUID playerId = player.getUUID();
        
        // Only restore health if it was previously reduced
        if (!HEALTH_REDUCED.getOrDefault(playerId, false)) {
            return;
        }
        
        AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            double currentMaxHealth = maxHealthAttribute.getBaseValue();
            double newMaxHealth = Math.min(currentMaxHealth + 2.0, 20.0); // Add 1 heart (2 health points), max 20
            maxHealthAttribute.setBaseValue(newMaxHealth);
            
            // Heal the player by 1 heart
            player.heal(2.0f);
            
            // Send message about health restoration
            player.sendSystemMessage(Component.literal(
                "§aThe golden apple restored 1 heart! §e(" + (int)(newMaxHealth / 2) + "/10 hearts)"
            ));
            
            // If health is fully restored, remove the flag and restore game mode
            if (newMaxHealth >= 20.0) {
                HEALTH_REDUCED.put(playerId, false);
                
                // Restore original game mode if it was changed to hardcore
                GameType originalGameMode = ORIGINAL_GAME_MODE.remove(playerId);
                Difficulty originalDifficulty = ORIGINAL_WORLD_DIFFICULTY.remove(playerId);
                Boolean originalHardcore = ORIGINAL_HARDCORE_STATUS.remove(playerId);
                
                if (originalGameMode != null) {
                    // Restore world hardcore mode to original state
                    if (originalHardcore != null) {
                        setWorldHardcoreMode(player, originalHardcore);
                    }
                    
                    // Restore hardcore mode packet to client
                    try {
                        PacketDistributor.sendToPlayer(player, new HardcoreModePacket(false));
                    } catch (Exception e) {
                        // Continue if packet fails
                    }
                    
                    // Restore original world difficulty and unlock it
                    if (originalDifficulty != null && originalDifficulty != Difficulty.HARD) {
                        player.server.setDifficulty(originalDifficulty, true);
                        // Unlock difficulty if it was originally unlocked
                        player.server.setDifficultyLocked(false);
                    }
                    
                    // Restore normal heart display with original difficulty
                    try {
                        Difficulty currentDifficulty = originalDifficulty != null ? originalDifficulty : player.serverLevel().getDifficulty();
                        player.connection.send(new ClientboundChangeDifficultyPacket(
                            currentDifficulty, false
                        ));
                    } catch (Exception e) {
                        // Fallback: just continue without visual change
                    }
                    
                    player.sendSystemMessage(Component.literal(
                        "§a✓ HARDCORE MODE DEACTIVATED! ✓"
                    ));
                    player.sendSystemMessage(Component.literal(
                        "§a💚 Hearts restored to normal - death is no longer permanent!"
                    ));
                    if (originalDifficulty != null) {
                        player.sendSystemMessage(Component.literal(
                            "§a⚔ World difficulty restored to " + originalDifficulty.getDisplayName().getString().toUpperCase() + "!"
                        ));
                    }
                    player.sendSystemMessage(Component.literal(
                        "§aCommands are now available again!"
                    ));
                }
                
                player.sendSystemMessage(Component.literal(
                    "§2Your health has been fully restored!"
                ));
                
                // Grant curse breaker advancement
                grantCurseBreakerAdvancement(player);
            }
        }
    }
    
    public static void grantRootAdvancement(ServerPlayer player) {
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(
            Random_respawn_on_death.MODID, 
            "root"
        );
        
        var advancementHolder = player.server.getAdvancements().get(advancementId);
        if (advancementHolder != null) {
            player.getAdvancements().award(advancementHolder, "first_death");
        }
    }
    
    public static void grantCurseBreakerAdvancement(ServerPlayer player) {
        UUID playerId = player.getUUID();
        
        // Check if player has already gotten this achievement
        if (CURSE_BREAKER_GRANTED.getOrDefault(playerId, false)) {
            return;
        }
        
        // Mark as completed
        CURSE_BREAKER_GRANTED.put(playerId, true);
        
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(
            Random_respawn_on_death.MODID, 
            "curse_breaker"
        );
        
        var advancementHolder = player.server.getAdvancements().get(advancementId);
        if (advancementHolder != null) {
            player.getAdvancements().award(advancementHolder, "restored_health");
        }
        
        player.sendSystemMessage(Component.literal(
            "§6🏆 Achievement Unlocked: §eCurse Breaker §6🏆"
        ));
    }
    
    public static void incrementDeathCount(ServerPlayer player) {
        UUID playerId = player.getUUID();
        int currentDeaths = DEATH_COUNT.getOrDefault(playerId, 0) + 1;
        DEATH_COUNT.put(playerId, currentDeaths);
        
        // Grant Death Collector at 5 deaths
        if (currentDeaths == 5) {
            grantDeathCollectorAdvancement(player);
        }
        
        // Grant Master of Fate at 10 deaths
        if (currentDeaths == 10) {
            grantMasterOfFateAdvancement(player);
        }
    }
    
    public static void grantDeathCollectorAdvancement(ServerPlayer player) {
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(
            Random_respawn_on_death.MODID, 
            "death_collector"
        );
        
        var advancementHolder = player.server.getAdvancements().get(advancementId);
        if (advancementHolder != null) {
            player.getAdvancements().award(advancementHolder, "collected_deaths");
        }
        
        player.sendSystemMessage(Component.literal(
            "§6🏆 Achievement Unlocked: §eDeath Collector §6🏆"
        ));
    }
    
    public static void grantMasterOfFateAdvancement(ServerPlayer player) {
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(
            Random_respawn_on_death.MODID, 
            "master_of_fate"
        );
        
        var advancementHolder = player.server.getAdvancements().get(advancementId);
        if (advancementHolder != null) {
            player.getAdvancements().award(advancementHolder, "mastered_fate");
        }
        
        player.sendSystemMessage(Component.literal(
            "§6🏆 Achievement Unlocked: §eMaster of Fate §6🏆"
        ));
        
        // Give diamond block as reward
        ItemStack diamond = new ItemStack(Items.DIAMOND_BLOCK, 1);
        if (!player.getInventory().add(diamond)) {
            player.drop(diamond, false);
        }
        
        player.sendSystemMessage(Component.literal(
            "§bYou received a §eDiamond Block §bas a reward for your mastery!"
        ));
    }
    
    public static boolean isInTemporaryHardcoreMode(ServerPlayer player) {
        UUID playerId = player.getUUID();
        return ORIGINAL_GAME_MODE.containsKey(playerId) && HEALTH_REDUCED.getOrDefault(playerId, false);
    }
    
    public static void handleHardcoreDeath(ServerPlayer player) {
        UUID playerId = player.getUUID();
        if (isInTemporaryHardcoreMode(player)) {
            // Remove the player's temporary hardcore status
            ORIGINAL_GAME_MODE.remove(playerId);
            HEALTH_REDUCED.put(playerId, false);
            
            // Send dramatic death message
            player.sendSystemMessage(Component.literal(
                "§4☠ GAME OVER ☠"
            ));
            player.sendSystemMessage(Component.literal(
                "§cYou died while cursed! The hardcore challenge has ended your journey."
            ));
            
            // Set player to spectator mode to simulate hardcore death
            player.setGameMode(GameType.SPECTATOR);
            
            // Optional: You could also kick the player or change world to hardcore
            // For now, we'll just put them in spectator mode as a "death penalty"
        }
    }
    
    public static boolean isHealthReduced(ServerPlayer player) {
        return HEALTH_REDUCED.getOrDefault(player.getUUID(), false);
    }
    
    public static void resetPlayerData(UUID playerId) {
        FIRST_DEATH_MARKER_COLLECTED.remove(playerId);
        HEALTH_REDUCED.remove(playerId);
        CURSE_BREAKER_GRANTED.remove(playerId);
        DEATH_COUNT.remove(playerId);
        ORIGINAL_GAME_MODE.remove(playerId);
    }
    
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        // Clear achievement data on server restart
        FIRST_DEATH_MARKER_COLLECTED.clear();
        HEALTH_REDUCED.clear();
        CURSE_BREAKER_GRANTED.clear();
        DEATH_COUNT.clear();
        ORIGINAL_GAME_MODE.clear();
    }
    
    @SubscribeEvent
    public static void onPlayerJoin(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Auto-grant root advancement so the tab shows up
            grantRootAdvancement(player);
        }
    }
    
    @SubscribeEvent
    public static void onCommandExecution(CommandEvent event) {
        if (event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer player) {
            if (isInTemporaryHardcoreMode(player)) {
                // Block all commands except a few essential ones
                String command = event.getParseResults().getReader().getString().toLowerCase();
                
                // Allow only essential commands
                if (!command.startsWith("/help") && !command.startsWith("/list") && !command.startsWith("/tell") && 
                    !command.startsWith("/msg") && !command.startsWith("/me") && !command.startsWith("/say")) {
                    
                    event.setCanceled(true);
                    player.sendSystemMessage(Component.literal(
                        "§c✘ Commands are disabled while in hardcore mode!"
                    ));
                    player.sendSystemMessage(Component.literal(
                        "§eEat golden apples to restore your health and regain command access."
                    ));
                }
            }
        }
    }
    
    // Add a server tick event to maintain difficulty lock during cursed phase
    @SubscribeEvent
    public static void onServerTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        // Check all players and maintain difficulty lock for cursed players
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (isInTemporaryHardcoreMode(player)) {
                // Ensure difficulty stays at HARD during cursed phase
                if (player.server.getWorldData().getDifficulty() != Difficulty.HARD) {
                    player.server.setDifficulty(Difficulty.HARD, true);
                    player.server.setDifficultyLocked(true);
                }
            }
        }
    }
}
