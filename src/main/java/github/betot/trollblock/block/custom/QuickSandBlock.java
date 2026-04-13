package github.betot.trollblock.block.custom;

import github.betot.trollblock.block.ModBlocks;
import github.betot.trollblock.entity.DirtyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class QuickSandBlock extends Block {

    public static boolean message_sended = false;

    public QuickSandBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        entity.setDeltaMovement(
                entity.getDeltaMovement().x * 0.3,
                Math.max(entity.getDeltaMovement().y, -0.001),
                entity.getDeltaMovement().z * 0.3
        );

        entity.fallDistance = 0;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
                                        BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {

        entity.setDeltaMovement(
                entity.getDeltaMovement().x * 0.4,
                Math.max(entity.getDeltaMovement().y, -0.0000001),
                entity.getDeltaMovement().z * 0.4
        );

        entity.fallDistance = 0;

        if(ModBlocks.QUICK_SAND.get() == state.getBlock()) {
            ((DirtyEntity)entity).trollblock$setDurty(true);
        }
    }
}

