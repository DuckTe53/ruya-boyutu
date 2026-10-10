package dev.ruyadunyasi.ruya;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class KaranfilClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Çiçeğin şeffaf kısımları doğru çizilsin
        BlockRenderLayerMap.putBlock(KaranfilMod.KARANFIL, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(KaranfilMod.CHLOROFUL_CAULDRON, ChunkSectionLayer.CUTOUT);
    }
}
