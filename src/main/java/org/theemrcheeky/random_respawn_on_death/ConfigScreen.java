package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;
    private EditBox respawnDistanceField;
    private Button saveButton;
    private Button cancelButton;
    private Button defaultButton;
    private int tempRespawnDistance;
    
    // UI Constants
    private static final int BUTTON_WIDTH = 70;
    private static final int BUTTON_HEIGHT = 20;
    private static final int FIELD_WIDTH = 150;
    private static final int FIELD_HEIGHT = 20;
    private static final int VALID_COLOR = 0xFFFFFF;
    private static final int INVALID_COLOR = 0xFF5555;
    private static final int BACKGROUND_COLOR = 0xC0101010;
    private static final int MIN_DISTANCE = 100;
    private static final int MAX_DISTANCE = 10000;
    private static final int DEFAULT_DISTANCE = 500;

    public ConfigScreen(Screen parentScreen) {
        super(Component.translatable("config.random_respawn_on_death.title"));
        this.parentScreen = parentScreen;
        this.tempRespawnDistance = Config.respawnDistance;
    }

    @Override
    protected void init() {
        super.init();

        final int centerX = this.width / 2;
        final int startY = this.height / 4;
        
        // Create input field with label
        this.addRenderableWidget(new net.minecraft.client.gui.components.StringWidget(
            centerX - FIELD_WIDTH / 2, 
            startY + 5, 
            FIELD_WIDTH, 
            FIELD_HEIGHT,
            Component.translatable("config.random_respawn_on_death.respawn_distance_label"),
            this.font
        ));
        
        this.respawnDistanceField = new EditBox(
            this.font, 
            centerX - FIELD_WIDTH / 2, 
            startY + FIELD_HEIGHT + 10, 
            FIELD_WIDTH, 
            FIELD_HEIGHT, 
            Component.empty()
        );
        this.respawnDistanceField.setValue(String.valueOf(this.tempRespawnDistance));
        this.respawnDistanceField.setResponder(this::onRespawnDistanceChanged);
        this.respawnDistanceField.setTooltip(Tooltip.create(Component.translatable("config.random_respawn_on_death.range_hint")));
        this.addRenderableWidget(this.respawnDistanceField);
        
        // Create buttons with optimized positioning
        int buttonY = startY + FIELD_HEIGHT * 2 + 20;
        
        this.saveButton = Button.builder(Component.literal("Save"), button -> this.saveAndClose(button))
            .bounds(centerX - BUTTON_WIDTH - 5, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Component.translatable("config.random_respawn_on_death.save")))
            .build();
            
        this.cancelButton = Button.builder(Component.literal("Cancel"), button -> this.onClose())
            .bounds(centerX + 5, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Component.translatable("config.random_respawn_on_death.cancel")))
            .build();
            
        this.defaultButton = Button.builder(Component.literal("Default"), button -> this.resetToDefault())
            .bounds(centerX - BUTTON_WIDTH / 2, buttonY + BUTTON_HEIGHT + 10, BUTTON_WIDTH, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Component.translatable("config.random_respawn_on_death.reset_default")))
            .build();

        // Add buttons to screen
        this.addRenderableWidget(this.saveButton);
        this.addRenderableWidget(this.cancelButton);
        this.addRenderableWidget(this.defaultButton);
    }

    private boolean isValidDistance(int distance) {
        return distance >= MIN_DISTANCE && distance <= MAX_DISTANCE;
    }

    private void onRespawnDistanceChanged(String value) {
        try {
            int distance = Integer.parseInt(value);
            boolean isValid = isValidDistance(distance);
            if (isValid) {
                this.tempRespawnDistance = distance;
            }
            this.respawnDistanceField.setTextColor(isValid ? VALID_COLOR : INVALID_COLOR);
            this.saveButton.active = isValid;
        } catch (NumberFormatException e) {
            this.respawnDistanceField.setTextColor(INVALID_COLOR);
            this.saveButton.active = false;
        }
    }

    private void resetToDefault() {
        this.tempRespawnDistance = DEFAULT_DISTANCE;
        this.respawnDistanceField.setValue(String.valueOf(DEFAULT_DISTANCE));
        this.respawnDistanceField.setTextColor(VALID_COLOR);
        this.saveButton.active = true;
    }

    private void saveAndClose(Button button) {
        if (isValidDistance(this.tempRespawnDistance)) {
            Config.RESPAWN_DISTANCE.set(this.tempRespawnDistance);
            Config.respawnDistance = this.tempRespawnDistance;
            Config.SPEC.save();
        }
        this.onClose();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parentScreen);
        }
    }

    @Override
    public void render(@javax.annotation.Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render dark semi-transparent background
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.fill(0, 0, this.width, this.height, BACKGROUND_COLOR);
        
        // Draw title with shadow for better visibility
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, VALID_COLOR);
        
        // Render all widgets
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public static Screen createConfigScreen(Screen parentScreen) {
        return new ConfigScreen(parentScreen);
    }
}
