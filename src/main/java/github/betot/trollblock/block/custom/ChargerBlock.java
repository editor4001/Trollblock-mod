package github.betot.trollblock.block.custom;

import github.betot.trollblock.blockentity.ChargerBlockEntity;
import github.betot.trollblock.blockentity.TransmitterBlockEntity;
import github.betot.trollblock.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ChargerBlock extends Block implements EntityBlock {

    public static final IntegerProperty GLOWSTONE_AMOUNT =
            IntegerProperty.create("glowstone_amount", 0, 4);

    public static final IntegerProperty ENERGY_AMOUNT =
            IntegerProperty.create("energy_amount", 0, 200);

    public ChargerBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(GLOWSTONE_AMOUNT, 0)
                .setValue(ENERGY_AMOUNT, 0)
        );
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChargerBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GLOWSTONE_AMOUNT, ENERGY_AMOUNT);
    }

    public static int increaseGlowstoneAmount(Level level, BlockPos pos, int amount) {
        int final_int = 0;
        if(amount == 0){
            final_int = 1;
        } else if (amount == 1) {
            final_int = 2;
        }else if (amount == 2) {
            final_int = 3;
        }else if (amount == 3) {
            final_int = 4;
        }else if (amount == 4) {
            final_int = 4;
        }

        return final_int;
    }

    public static int getEnergyAmount(int glowstoneAmount) {
        return glowstoneAmount * 50;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        ItemStack usedItem = player.getItemInHand(interactionHand);

        if(usedItem.is(Items.GLOWSTONE_DUST)){
            int current_glowstone_amount = state.getValue(GLOWSTONE_AMOUNT);
            int new_glowstone_amount = increaseGlowstoneAmount(level, pos, current_glowstone_amount);

            if(state.getValue(GLOWSTONE_AMOUNT) == 4){
                return InteractionResult.FAIL;
            }else {

                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ChargerBlockEntity charger) {
                    charger.setGlowstone_amount(new_glowstone_amount);
                    charger.setEnergy(getEnergyAmount(new_glowstone_amount));
                }
                usedItem.shrink(1);
            }


        }

        // ========================
// ENERGY TABLET
// ========================

        if (usedItem.is(ModItems.ENERGY_TABLET.get()) || player.isShiftKeyDown()) {
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof ChargerBlockEntity charger) {
                ItemStackHandler inventory = charger.getInventory();

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
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {

        if (!level.isClientSide) {

            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof ChargerBlockEntity charger) {

                ItemStackHandler inv = charger.getInventory();

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


    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;

        return (lvl, pos, st, be) -> {
            if (be instanceof ChargerBlockEntity charger) {
                charger.tick(lvl, pos, st, charger.getInventory());
            }
        };
    }
}