package net.iicosahedra.spectrethings.setup;

import net.iicosahedra.spectrethings.SpectreThings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = SpectreThings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class SetupEvents {
    @SubscribeEvent
    static void registerAttributes(EntityAttributeModificationEvent event) {

    }

    @SubscribeEvent
    static void registerRegistries(NewRegistryEvent event){

    }
}
