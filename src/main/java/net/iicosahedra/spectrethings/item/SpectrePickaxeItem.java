package net.iicosahedra.spectrethings.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;

public class SpectrePickaxeItem extends PickaxeItem {
    public SpectrePickaxeItem() {
        super(Tiers.IRON, new Item.Properties().attributes(PickaxeItem.createAttributes(Tiers.IRON, 1.0F, -2.8F)));
    }
} 