package dev.ruyadunyasi.ruya;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class UnicornRenderer extends MobRenderer<Unicorn, LivingEntityRenderState, UnicornModel> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("ruya_boyutu", "textures/entity/unicorn.png");

    public UnicornRenderer(EntityRendererProvider.Context context) {
        super(context, new UnicornModel(context.bakeLayer(UnicornModel.LAYER)), 0.75F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
