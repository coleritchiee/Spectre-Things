package net.iicosahedra.spectrethings.event;

import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.setup.Registration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = SpectreThings.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !stack.has(Registration.ANCHORED.get())) {
            return;
        }

        boolean isAnchored = stack.getOrDefault(Registration.ANCHORED.get(), false);

        if (isAnchored) {
            event.getToolTip().add(Component.translatable("tooltip.spectrethings.anchored").withStyle(ChatFormatting.AQUA));
        }
    }
} 