package dev.ruyadunyasi.ruya;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RuyaBoyutuClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(RuyaBoyutuMod.NIGHTMARE_ZOMBIE, NightmareZombieRenderer::new);
    }
}
