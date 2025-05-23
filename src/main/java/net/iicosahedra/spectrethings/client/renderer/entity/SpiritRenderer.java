package net.iicosahedra.spectrethings.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.client.model.SpiritModel;
import net.iicosahedra.spectrethings.entity.SpiritEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector4f;
import net.iicosahedra.spectrethings.client.renderer.layer.SpiritGelLayer;
import net.minecraft.client.model.geom.EntityModelSet;

import javax.annotation.Nullable;

public class SpiritRenderer extends MobRenderer<SpiritEntity, SpiritModel<SpiritEntity>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpectreThings.MODID, "textures/entity/spirit.png");

    public SpiritRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritModel<>(context.bakeLayer(SpiritModel.LAYER_LOCATION)), 0.6f);
        this.addLayer(new SpiritGelLayer(this, context.getModelSet()));
    }

    public static ResourceLocation getTextureLocationStatic() {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getTextureLocation(SpiritEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(SpiritEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(SpiritEntity entity, PoseStack poseStack, float partialTickTime) {
        super.scale(entity, poseStack, partialTickTime);
    }
} 