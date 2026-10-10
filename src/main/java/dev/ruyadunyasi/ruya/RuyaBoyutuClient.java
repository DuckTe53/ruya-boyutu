package dev.ruyadunyasi.ruya;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.resources.Identifier;

public class RuyaBoyutuClient implements ClientModInitializer {
    private static final Identifier HEROBRINE_TEXTURE =
            Identifier.fromNamespaceAndPath(RuyaBoyutuMod.MOD_ID, "textures/entity/herobrine_silhouette.png");
    private static final Identifier STEVE_TEXTURE =
            Identifier.fromNamespaceAndPath(RuyaBoyutuMod.MOD_ID, "textures/entity/steve_silhouette.png");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(RuyaBoyutuMod.NIGHTMARE_ZOMBIE, NightmareZombieRenderer::new);
        EntityRendererRegistry.register(RuyaBoyutuMod.HEROBRINE_SILHOUETTE,
                context -> new SilhouetteRenderer(context, HEROBRINE_TEXTURE));
        EntityRendererRegistry.register(RuyaBoyutuMod.STEVE_SILHOUETTE,
                context -> new SilhouetteRenderer(context, STEVE_TEXTURE));
    }
}
