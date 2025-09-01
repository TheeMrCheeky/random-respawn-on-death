package org.theemrcheeky.random_respawn_on_death;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// Configuration class for Random Respawn on Death mod
@EventBusSubscriber(modid = Random_respawn_on_death.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Random respawn distance configuration with GUI-friendly settings
    public static final ModConfigSpec.IntValue RESPAWN_DISTANCE = BUILDER
            .comment("Exact distance in blocks from death point to randomly respawn")
            .comment("Player will spawn at exactly this distance in a random direction")
            .comment("Minimum: 100 blocks, Maximum: 10000 blocks")
            .defineInRange("respawnDistance", 500, 100, 10000);

    static final ModConfigSpec SPEC = BUILDER.build();

    // Public static field for accessing the respawn distance
    public static int respawnDistance = 500;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        respawnDistance = RESPAWN_DISTANCE.get();
    }
}
