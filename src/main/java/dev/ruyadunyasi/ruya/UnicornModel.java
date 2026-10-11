package dev.ruyadunyasi.ruya;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/**
 * Unicorn modeli: at gövdesi + alnında boynuz, gökkuşağı yeleli. Eyer modele dahildir.
 * Doku: assets/ruya_boyutu/textures/entity/unicorn.png (128x64)
 */
public class UnicornModel extends QuadrupedModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath("ruya_boyutu", "unicorn"), "main");

    public UnicornModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Gövde (+ eyer ve kuyruk gövdeye bağlı)
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -5.0F, -11.0F, 10.0F, 10.0F, 22.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F));
        body.addOrReplaceChild("saddle",
                CubeListBuilder.create().texOffs(64, 32).addBox(-5.0F, -5.0F, -4.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.ZERO);
        body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(104, 0).addBox(-1.5F, -1.0F, -0.5F, 3.0F, 10.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -4.0F, 11.0F, 0.6F, 0.0F, 0.0F));

        // Kafa dönüşü QuadrupedModel tarafından yönetilir. Boyun, kafaya bağlı eğik bir parçadır.
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, -11.0F));
        PartDefinition neck = head.addOrReplaceChild("neck",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F)
                        .texOffs(22, 0).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F)
                        .texOffs(48, 0).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F)
                        .texOffs(66, 0).addBox(-3.0F, -14.0F, 3.0F, 2.0F, 3.0F, 1.0F)
                        .texOffs(66, 0).addBox(1.0F, -14.0F, 3.0F, 2.0F, 3.0F, 1.0F)
                        .texOffs(80, 0).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F));
        neck.addOrReplaceChild("horn",
                CubeListBuilder.create().texOffs(72, 0).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 8.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -11.0F, 0.5F, 0.45F, 0.0F, 0.0F));

        // Bacaklar (QuadrupedModel bu isimleri bekler)
        CubeListBuilder leg = CubeListBuilder.create().texOffs(88, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F);
        root.addOrReplaceChild("right_hind_leg", leg, PartPose.offset(-3.0F, 13.0F, 9.0F));
        root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(3.0F, 13.0F, 9.0F));
        root.addOrReplaceChild("right_front_leg", leg, PartPose.offset(-3.0F, 13.0F, -9.0F));
        root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(3.0F, 13.0F, -9.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }
}
