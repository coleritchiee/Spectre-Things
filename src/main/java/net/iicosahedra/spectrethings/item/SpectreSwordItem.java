package net.iicosahedra.spectrethings.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class SpectreSwordItem extends SwordItem {
    public SpectreSwordItem() {
        super(Tiers.IRON, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F)));
    }
} 