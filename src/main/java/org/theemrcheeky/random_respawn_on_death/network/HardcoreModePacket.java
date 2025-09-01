package org.theemrcheeky.random_respawn_on_death.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.theemrcheeky.random_respawn_on_death.Random_respawn_on_death;

public record HardcoreModePacket(boolean hardcoreMode) implements CustomPacketPayload {
    
    public static final Type<HardcoreModePacket> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(Random_respawn_on_death.MODID, "hardcore_mode")
    );
    
    public static final StreamCodec<FriendlyByteBuf, HardcoreModePacket> STREAM_CODEC = 
        StreamCodec.composite(
            StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean),
            HardcoreModePacket::hardcoreMode,
            HardcoreModePacket::new
        );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
