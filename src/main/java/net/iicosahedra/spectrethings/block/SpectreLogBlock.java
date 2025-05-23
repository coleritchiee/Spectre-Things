package net.iicosahedra.spectrethings.block;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class SpectreLogBlock extends RotatedPillarBlock {
    public SpectreLogBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                .strength(2.0F)
                .sound(SoundType.WOOD)
        );
    }
} 