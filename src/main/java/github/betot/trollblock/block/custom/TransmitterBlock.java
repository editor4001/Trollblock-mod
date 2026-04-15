package github.betot.trollblock.block.custom;

import github.betot.trollblock.blockentity.TransmitterBlockEntity;
import github.betot.trollblock.blockentity.TransmitterBlockEntity;
import github.betot.trollblock.event.ClientData;
import github.betot.trollblock.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TransmitterBlock extends Block implements EntityBlock {

    public static final BooleanProperty IS_RECEIVER =
            BooleanProperty.create("is_receiver");


    public static final EnumProperty<Mode> STATE =
            EnumProperty.create("state", Mode.class);

    public enum Mode implements StringRepresentable {
        READ("read"),
        TOGGLE("toggle");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public TransmitterBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(IS_RECEIVER, false)
                        .setValue(STATE, Mode.READ)
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TransmitterBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity entity, ItemStack stack) {

        super.setPlacedBy(level, pos, state, entity, stack);

        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof TransmitterBlockEntity self)) return;

        UUID myId = self.getLinkId();
        CompoundTag data = player.getPersistentData();

        // FIRST BLOCK
        if (!data.getBoolean("linking")) {

            data.putBoolean("linking", true);
            data.putUUID("link_source", myId);

            self.setLinker(player.getUUID());
            return;
        }

        UUID source = data.getUUID("link_source");

        self.setLinkedTo(source);
        self.setLinker(player.getUUID());
        if (!level.isClientSide) {
            ClientData.selectedLink = self.getLinkId();
        }

        for (BlockPos p : BlockPos.betweenClosed(
                pos.offset(-32, -32, -32),
                pos.offset(32, 32, 32))) {

            BlockEntity otherBe = level.getBlockEntity(p);

            if (!(otherBe instanceof TransmitterBlockEntity other)) continue;

            if (other.getLinkId().equals(source)) {

                other.setLinkedTo(myId);
                other.setLinker(player.getUUID());
                other.setChanged();
                break;
            }
        }

        data.putBoolean("linking", false);
        data.remove("link_source");
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);

        if (level.isClientSide) {
            ClientData.cached.remove(pos);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        ItemStack usedItem = player.getItemInHand(interactionHand);


        if (usedItem.is(ModItems.ENERGY_TABLET.get()) || player.isShiftKeyDown()) {
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof TransmitterBlockEntity transmitter) {
                ItemStackHandler inventory = transmitter.getInventory();

                // SHIFT = TAKE
                if (player.isShiftKeyDown()) {

                    ItemStack stack = inventory.getStackInSlot(0);

                    if (stack.isEmpty()) {
                        player.displayClientMessage(Component.literal("nothing..."), true);
                        return InteractionResult.SUCCESS;
                    }

                    inventory.setStackInSlot(0, ItemStack.EMPTY);

                    if (player.getItemInHand(interactionHand).isEmpty()) {
                        player.setItemInHand(interactionHand, stack);
                    } else {
                        player.addItem(stack);
                    }

                    return InteractionResult.SUCCESS;
                }

                // INSERT
                if (!inventory.getStackInSlot(0).isEmpty()) {
                    player.displayClientMessage(Component.literal("already have a tablet"), true);
                    return InteractionResult.SUCCESS;
                }

                ItemStack held = usedItem.copy();
                held.setCount(1);

                inventory.setStackInSlot(0, held);
                usedItem.shrink(1);

                return InteractionResult.SUCCESS;
            }
        }


        return super.use(state, level, pos, player, interactionHand, hitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_RECEIVER, STATE);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {

        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof TransmitterBlockEntity transmitter) {

            if (!transmitter.isReceiver()) return 0;

            return transmitter.isPowered(direction) ? 15 : 0;
        }

        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }


    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;

        return (lvl, pos, st, be) -> {
            if (be instanceof TransmitterBlockEntity transmitter) {
                transmitter.tick(lvl, pos, st,transmitter.getInventory());
            }
        };
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {

        if (!level.isClientSide) {

            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof TransmitterBlockEntity transmitter) {

                ItemStackHandler inv = transmitter.getInventory();

                for (int i = 0; i < inv.getSlots(); i++) {

                    ItemStack stack = inv.getStackInSlot(i);

                    if (!stack.isEmpty()) {
                        level.addFreshEntity(new ItemEntity(
                                level,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                stack.copy()
                        ));
                    }
                }
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }


}