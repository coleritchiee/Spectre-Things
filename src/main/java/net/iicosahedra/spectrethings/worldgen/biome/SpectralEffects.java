package net.iicosahedra.spectrethings.worldgen.biome;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class SpectralEffects extends DimensionSpecialEffects {
    public SpectralEffects() {
        super(10000f, false, SkyType.NONE, true, true);
    }

    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        return false;
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 vec3, float v) {
        return vec3.scale(1.5);
    }

    @Override
    public boolean isFoggyAt(int i, int i1) {
        return false;
    }
}
