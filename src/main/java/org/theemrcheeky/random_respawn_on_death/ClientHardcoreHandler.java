package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.lang.reflect.Field;

@EventBusSubscriber(modid = "random_respawn_on_death", value = Dist.CLIENT)
public class ClientHardcoreHandler {
    private static boolean isReflectionSetup = false;
    private static boolean originalHardcore = false;
    private static boolean isModifyingHearts = false;
    
    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        
        // Check if player should show hardcore hearts
        if (mc.player != null) {
            boolean shouldShowHardcoreHearts = mc.player.getPersistentData().getBoolean("random_respawn_hardcore_mode");
            
            if (shouldShowHardcoreHearts && !isModifyingHearts) {
                setupReflection();
                if (isReflectionSetup) {
                    try {
                        // Temporarily modify the game type to hardcore for rendering
                        setTemporaryHardcoreMode(true);
                    } catch (Exception e) {
                        System.err.println("Failed to set temporary hardcore mode: " + e.getMessage());
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onRenderGuiLayerPost(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        
        // Restore original state after rendering
        if (isModifyingHearts) {
            try {
                setTemporaryHardcoreMode(false);
            } catch (Exception e) {
                // Ignore restoration errors
            }
        }
    }
    
    private static void setupReflection() {
        if (isReflectionSetup) return;
        
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                // Store original hardcore state
                originalHardcore = mc.level.getLevelData().isHardcore();
                isReflectionSetup = true;
                System.out.println("Hardcore hearts reflection setup complete");
            }
        } catch (Exception e) {
            System.err.println("Failed to setup hardcore hearts reflection: " + e.getMessage());
        }
    }
    
    private static void setTemporaryHardcoreMode(boolean enable) throws Exception {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        
        if (enable && !isModifyingHearts) {
            // Try to temporarily modify the level data through reflection
            if (mc.level != null) {
                Object levelData = mc.level.getLevelData();
                Field[] fields = levelData.getClass().getDeclaredFields();
                
                for (Field field : fields) {
                    if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.setAccessible(true);
                        try {
                            boolean currentValue = field.getBoolean(levelData);
                            // Look for the hardcore field by testing different fields
                            if (currentValue == originalHardcore) {
                                field.setBoolean(levelData, true);
                                isModifyingHearts = true;
                                // Hardcore hearts enabled silently
                                break;
                            }
                        } catch (Exception e) {
                            // Continue to next field
                        }
                    }
                }
            }
        } else if (!enable && isModifyingHearts) {
            // Restore original hardcore state
            if (mc.level != null) {
                Object levelData = mc.level.getLevelData();
                Field[] fields = levelData.getClass().getDeclaredFields();
                
                for (Field field : fields) {
                    if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.setAccessible(true);
                        try {
                            boolean currentValue = field.getBoolean(levelData);
                            if (currentValue == true) { // Find the field we modified
                                field.setBoolean(levelData, originalHardcore);
                                isModifyingHearts = false;
                                break;
                            }
                        } catch (Exception e) {
                            // Continue to next field
                        }
                    }
                }
            }
        }
    }
}
