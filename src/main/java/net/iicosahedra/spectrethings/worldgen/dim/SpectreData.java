package net.iicosahedra.spectrethings.worldgen.dim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record SpectreData(
        ResourceKey<Level> originalDimension,
        BlockPos originalPosition
) {
    public static final Codec<SpectreData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dim").forGetter(SpectreData::originalDimension),
                    BlockPos.CODEC.fieldOf("pos").forGetter(SpectreData::originalPosition)
            ).apply(instance, SpectreData::new)
    );

    public SpectreData() {
        this(Level.OVERWORLD, BlockPos.ZERO);
    }
}