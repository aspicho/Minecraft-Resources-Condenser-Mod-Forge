package aspi.cho.resourcescondenser.block.custom;

import aspi.cho.resourcescondenser.block.entity.CondenserBlockEntity;
import aspi.cho.resourcescondenser.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class Condenser extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public Condenser(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
       builder.add(FACING);
    }

    /* BLOCK ENTITY */

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pBlockState, Level pLevel, BlockPos pBlockPos, BlockState newBlockState, boolean IsMoving) {
        if (pBlockState.getBlock() != newBlockState.getBlock()){
            BlockEntity blockEntity = pLevel.getBlockEntity(pBlockPos);
            if (blockEntity instanceof CondenserBlockEntity){
                ((CondenserBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pBlockState, pLevel, pBlockPos, newBlockState, IsMoving);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()){
            BlockEntity entity = level.getBlockEntity(blockPos);
            if(entity instanceof CondenserBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer) player), (CondenserBlockEntity)entity, blockPos);
            } else {
                throw new IllegalStateException("Container provider is missing");
            }
        }

        return super.use(blockState, level, blockPos, player, hand, hitResult);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CondenserBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {


        return createTickerHelper(type, ModBlockEntities.CONDENSER.get(), CondenserBlockEntity::tick);
    }
}
