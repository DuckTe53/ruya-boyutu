package dev.ruyadunyasi.ruya;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Kloroful dolu kazan. Seviye 1-3 (su kazanı gibi). Kloroful Kovası boş kazana dökülünce
 * seviye 3 olur, her cam şişe bir seviye azaltır ve 1 adet Kloroful verir.
 */
public class ChlorofulCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<ChlorofulCauldronBlock> CODEC = simpleCodec(ChlorofulCauldronBlock::new);
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;
    public static final CauldronInteraction.InteractionMap INTERACTIONS =
            CauldronInteraction.newInteractionMap("chloroful");

    public ChlorofulCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties, INTERACTIONS);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 3));
    }

    @Override
    public MapCodec<ChlorofulCauldronBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(LEVEL) == 3;
    }

    /** Etkileşimleri kaydeder (mod başlarken bir kez çağrılır). */
    public static void registerInteractions() {
        // Boş kazana Kloroful Kovası dök
        CauldronInteraction.EMPTY.map().put(KaranfilMod.CHLOROFUL_BUCKET, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
                level.setBlockAndUpdate(pos, KaranfilMod.CHLOROFUL_CAULDRON.defaultBlockState().setValue(LEVEL, 3));
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return InteractionResult.SUCCESS;
        });

        // Dolu kazandan cam şişe ile Kloroful al
        INTERACTIONS.map().put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(RuyaBoyutuMod.CHLOROFUL)));
                int current = state.getValue(LEVEL);
                level.setBlockAndUpdate(pos, current <= 1
                        ? Blocks.CAULDRON.defaultBlockState()
                        : state.setValue(LEVEL, current - 1));
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return InteractionResult.SUCCESS;
        });
    }
}
