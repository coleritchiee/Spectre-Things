package net.iicosahedra.spectrethings.block;

import net.iicosahedra.spectrethings.setup.Registration;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class SpectreSaplingBlock extends SaplingBlock {
    public SpectreSaplingBlock() {
        super(Registration.SPECTRE_TREE, Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.GRASS)
        );
    }
} 