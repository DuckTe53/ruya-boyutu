package dev.ruyadunyasi.ruya;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bordo kırmızı Karanfil. Normal çiçeklerden farkı: Nightmare biyomunun zemini olan
 * kızıl nylium ve netherrack üzerine de dikilebilir.
 */
public class KaranfilBlock extends FlowerBlock {

    public KaranfilBlock(BlockBehaviour.Properties properties) {
        super(MobEffects.NIGHT_VISION, 5.0F, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return super.mayPlaceOn(state, level, pos)
                || state.is(Blocks.CRIMSON_NYLIUM)
                || state.is(Blocks.NETHERRACK);
    }
}
