package net.iicosahedra.spectrethings.attachment;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.ArrayList;
import java.util.List;

public record KeptItemsData(List<ItemStack> items) {
    public static final Codec<KeptItemsData> CODEC = Codec.list(ItemStack.CODEC).xmap(
            KeptItemsData::new, KeptItemsData::items
    ).fieldOf("kept_items").codec();

    public KeptItemsData() {
        this(new ArrayList<>());
    }

}