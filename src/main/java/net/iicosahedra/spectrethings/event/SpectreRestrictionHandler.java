package net.iicosahedra.spectrethings.event;

import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.setup.Registration;
import net.iicosahedra.spectrethings.worldgen.dim.SpectreData;
import net.iicosahedra.spectrethings.worldgen.dim.SpectreHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SpectreThings.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SpectreRestrictionHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.level().dimension() == Registration.SPECTRE_LEVEL_KEY) {
                SpectreHandler handler = SpectreHandler.get(serverPlayer.server);
                handler.checkAccess(serverPlayer);
            }
        }
    }
}
