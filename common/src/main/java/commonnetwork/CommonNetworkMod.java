package commonnetwork;

import commonnetwork.networking.DelayedPacketRegistrationHandler;
import commonnetwork.networking.PacketRegistrar;
import commonnetwork.networking.PacketRegistrationHandler;
import commonnetwork.networking.data.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CommonNetworkMod
{
    private final PacketRegistrationHandler packetRegistration;
    private static DelayedPacketRegistrationHandler delayedHandler;
    public static CommonNetworkMod INSTANCE;

    public CommonNetworkMod(PacketRegistrationHandler packetRegistration)
    {
        INSTANCE = this;
        this.packetRegistration = packetRegistration;
        getDelayedHandler().registerQueuedPackets(packetRegistration);
    }

    /**
     * Fabric does not enforce load order, so we may have to delay packet registrations.
     *
     * @return the handler;
     */
    private static DelayedPacketRegistrationHandler getDelayedHandler()
    {
        if (delayedHandler == null)
        {
            delayedHandler = new DelayedPacketRegistrationHandler();
        }
        return delayedHandler;
    }

    public static <T> PacketRegistrar registerPacket(ResourceLocation packetIdentifier, Class<T> packetClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, Consumer<PacketContext<T>> handler)
    {
        if (INSTANCE != null)
        {
            return INSTANCE.packetRegistration.registerPacket(packetIdentifier, packetClass, encoder, decoder, handler);
        }
        else
        {
            return getDelayedHandler().registerPacket(packetIdentifier, packetClass, encoder, decoder, handler);
        }
    }

    public PacketRegistrationHandler getPacketRegistration()
    {
        return packetRegistration;
    }
}
