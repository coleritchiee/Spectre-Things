package net.iicosahedra.spectrethings.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tiers;

public class SpectreShovelItem extends ShovelItem {
    public SpectreShovelItem() {
        super(Tiers.IRON, new Item.Properties().attributes(ShovelItem.createAttributes(Tiers.IRON, 1.5F, -3.0F)));
    }
} 