package org.theemrcheeky.random_respawn_on_death;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import org.theemrcheeky.random_respawn_on_death.network.HardcoreModePacket;
import org.theemrcheeky.random_respawn_on_death.network.ClientPacketHandler;

// Random Respawn on Death mod - randomly respawns players in the overworld within a configurable distance
@Mod(Random_respawn_on_death.MODID)
public class Random_respawn_on_death {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "random_respawn_on_death";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    public Random_respawn_on_death(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        
        // Register network packets
        modEventBus.addListener(this::registerPayloads);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

        // Register the death event handler
        NeoForge.EVENT_BUS.register(DeathEventHandler.class);
        
        // Register the death marker handler
        NeoForge.EVENT_BUS.register(DeathMarkerHandler.class);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register config screen factory for client side
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> ConfigScreen.createConfigScreen(parent));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Random Respawn on Death mod initialized!");
        LOGGER.info("Surface-only random respawn enabled with exact distance: {} blocks", Config.respawnDistance);
    }
    
    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0");
        registrar.playToClient(
            HardcoreModePacket.TYPE,
            HardcoreModePacket.STREAM_CODEC,
            ClientPacketHandler::handleHardcoreModePacket
        );
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Random Respawn on Death mod ready for server!");
    }

    // Client-side events
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Random Respawn on Death client setup complete!");
        }
    }
}
