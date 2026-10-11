package dev.ruyadunyasi.ruya;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RuyaExtrasClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(UnicornModel.LAYER, UnicornModel::createBodyLayer);
        EntityRendererRegistry.register(RuyaExtrasMod.UNICORN, UnicornRenderer::new);
    }
}
