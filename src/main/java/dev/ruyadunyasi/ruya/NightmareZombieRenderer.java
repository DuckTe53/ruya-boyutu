package dev.ruyadunyasi.ruya;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;

public class NightmareZombieRenderer extends ZombieRenderer {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RuyaBoyutuMod.MOD_ID, "textures/entity/nightmare_zombie.png");

    public NightmareZombieRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(ZombieRenderState state) {
        return TEXTURE;
    }
}
