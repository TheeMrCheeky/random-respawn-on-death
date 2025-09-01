package org.theemrcheeky.random_respawn_on_death;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = Random_respawn_on_death.MODID)
public class GoldenAppleHandler {
    
    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Finish event) {
        // Check if the entity is a server player
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        
        // Check if they ate a golden apple
        if (event.getItem().getItem() == Items.GOLDEN_APPLE) {
            // Restore health if they have the cursed health condition
            if (ModAdvancements.isHealthReduced(player)) {
                ModAdvancements.restoreHealthFromGoldenApple(player);
            }
        }
    }
}
