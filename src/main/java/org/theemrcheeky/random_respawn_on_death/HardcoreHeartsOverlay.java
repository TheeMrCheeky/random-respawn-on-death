package org.theemrcheeky.random_respawn_on_death;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

// Disabled overlay approach - ClientHardcoreHandler is working successfully
public class HardcoreHeartsOverlay {
    private static final ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");
    
    //@SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        // Check if player should show hardcore hearts
        if (mc.player != null) {
            boolean shouldShowHardcoreHearts = mc.player.getPersistentData().getBoolean("random_respawn_hardcore_mode");
            
            if (shouldShowHardcoreHearts) {
                renderHardcoreHearts(event.getGuiGraphics(), mc.player);
            }
        }
    }
    
    private static void renderHardcoreHearts(GuiGraphics guiGraphics, LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;
        
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        
        // Calculate heart position (same as vanilla)
        int health = Mth.ceil(player.getHealth());
        int maxHealth = Mth.ceil(player.getMaxHealth());
        int absorptionAmount = Mth.ceil(player.getAbsorptionAmount());
        
        int healthRows = Mth.ceil((maxHealth + absorptionAmount) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        
        int left = screenWidth / 2 - 91;
        int top = screenHeight - 39;
        
        // Move up if there are multiple rows
        if (healthRows > 1) {
            top -= (healthRows - 1) * rowHeight;
        }
        
        RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
        
        // Render hardcore hearts over the existing ones
        for (int i = 0; i < 10; i++) {
            int x = left + i * 8;
            int y = top;
            
            if (i * 2 + 1 < health) {
                // Full hardcore heart
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 61, 0, 9, 9);
            } else if (i * 2 + 1 == health) {
                // Half hardcore heart
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 70, 0, 9, 9);
            } else if (i * 2 + 1 < maxHealth) {
                // Empty hardcore heart
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 79, 0, 9, 9);
            }
        }
    }
}
