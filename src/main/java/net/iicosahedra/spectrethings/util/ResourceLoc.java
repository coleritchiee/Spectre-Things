package net.iicosahedra.spectrethings.util;

import net.iicosahedra.spectrethings.SpectreThings;
import net.minecraft.resources.ResourceLocation;

public class ResourceLoc {
    public static ResourceLocation create(String path){
        return ResourceLocation.fromNamespaceAndPath(SpectreThings.MODID, path);
    }
}
