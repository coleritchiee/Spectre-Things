package net.iicosahedra.spectrethings.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.iicosahedra.spectrethings.SpectreThings;
import net.iicosahedra.spectrethings.entity.SpiritEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpiritModel<T extends SpiritEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SpectreThings.MODID, "spirit"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart face;

    public SpiritModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.face = root.getChild("face");
        this.tail = body.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        body.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(17, 7)
                .addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("face", CubeListBuilder.create()
                .texOffs(32, 0)
                .addBox(-4.0F, -4.0F, -4.3F, 8.0F, 8.0F, 0.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.body.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.1f;
        this.face.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.1f;
        this.face.xRot = headPitch * Mth.DEG_TO_RAD * 0.1f;
        this.tail.xRot = 0.0F + Mth.cos(ageInTicks * 0.1F) * 0.1F;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
} 