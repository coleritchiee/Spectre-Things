package net.iicosahedra.spectrethings.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.iicosahedra.spectrethings.client.model.SpiritModel;
import net.iicosahedra.spectrethings.client.renderer.entity.SpiritRenderer;
import net.iicosahedra.spectrethings.entity.SpiritEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpiritGelLayer extends RenderLayer<SpiritEntity, SpiritModel<SpiritEntity>> {

    private static final ResourceLocation GEL_TEXTURE = SpiritRenderer.getTextureLocationStatic();
    private final ModelPart gelCube;

    public SpiritGelLayer(LivingEntityRenderer<SpiritEntity, SpiritModel<SpiritEntity>> parentRenderer, EntityModelSet modelSet) {
        super(parentRenderer);
        this.gelCube = parentRenderer.getModel().root().getChild("body");
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SpiritEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {

            float squishFactor = Mth.lerp(entity.level().getGameTime() % 10 / 10.0f + partialTicks / 10.0f, 0.95f, 1.05f);
            float scale = 1.01f;
            poseStack.pushPose();
            poseStack.translate(this.gelCube.x / 16.0F, this.gelCube.y / 16.0F, this.gelCube.z / 16.0F);
            poseStack.scale(squishFactor * scale, (1.0f / squishFactor) * scale, squishFactor * scale);
            poseStack.translate(-this.gelCube.x / 16.0F, -this.gelCube.y / 16.0F, -this.gelCube.z / 16.0F);

            RenderType renderType = RenderType.entityTranslucent(GEL_TEXTURE);
            VertexConsumer vertexconsumer = buffer.getBuffer(renderType);


            this.gelCube.render(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F));

            poseStack.popPose();
        }
    }
} 