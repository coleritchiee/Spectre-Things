package net.iicosahedra.spectrethings.setup;

import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.client.model.SpiritModel;
import net.iicosahedra.spectrethings.client.renderer.entity.SpiritRenderer;
import net.iicosahedra.spectrethings.worldgen.biome.SpectralEffects;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = SpectreThings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(()->{
            ItemBlockRenderTypes.setRenderLayer(Registration.SPECTRE_BLOCK.value(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.SPECTRE_CORE.value(), RenderType.translucent());
        });
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpiritModel.LAYER_LOCATION, SpiritModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.SPIRIT.get(), SpiritRenderer::new);
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
    }

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event){
        event.register(Registration.SPECTRE_EFFECTS, new SpectralEffects());
    }


}
