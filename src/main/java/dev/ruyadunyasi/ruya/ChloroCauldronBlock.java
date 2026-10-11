package dev.ruyadunyasi.ruya;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Kloroful dolu kazan: 1-3 seviye. Etkileşimi RuyaBoyutuMod içindeki UseBlockCallback yönetir. */
public class ChloroCauldronBlock extends Block {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 3);

    public ChloroCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 3));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Blocks.CAULDRON.defaultBlockState().getShape(level, pos, context);
    }
}
