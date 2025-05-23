package net.iicosahedra.spectrethings.item;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;

public class SpectreAxeItem extends AxeItem {
    public SpectreAxeItem() {
        super(Tiers.IRON, new Item.Properties().attributes(AxeItem.createAttributes(Tiers.IRON, 6.0F, -3.1F)));
    }
} 