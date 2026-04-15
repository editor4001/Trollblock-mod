package github.betot.trollblock.item.custom;

import github.betot.trollblock.block.ModBlocks;
import github.betot.trollblock.block.custom.TransmitterBlock;
import github.betot.trollblock.blockentity.TransmitterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ScrewdriverItem extends Item {

    public ScrewdriverItem(Properties properties) {
        super(properties);
    }



    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);

        if (!state.is(ModBlocks.TRANSMITTER.get())) {
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof TransmitterBlockEntity self)) {
            return InteractionResult.PASS;
        }

        // 🔁 SHIFT = switch mode READ / TOGGLE
        if (player.isShiftKeyDown()) {

            TransmitterBlock.Mode newMode =
                    state.getValue(TransmitterBlock.STATE) == TransmitterBlock.Mode.READ
                            ? TransmitterBlock.Mode.TOGGLE
                            : TransmitterBlock.Mode.READ;

            level.setBlock(pos,
                    state.setValue(TransmitterBlock.STATE, newMode),
                    3);

            player.displayClientMessage(
                    Component.literal("Mode: " + newMode.getSerializedName()),
                    true
            );

            return InteractionResult.SUCCESS;
        }

        // 🧠 MODE READ = juste info
        if (state.getValue(TransmitterBlock.STATE) == TransmitterBlock.Mode.READ) {

            boolean receiver = state.getValue(TransmitterBlock.IS_RECEIVER);

            player.displayClientMessage(
                    Component.literal(receiver ? "receiver" : "sender"),
                    true
            );

            return InteractionResult.SUCCESS;
        }
        boolean newValue = !state.getValue(TransmitterBlock.IS_RECEIVER);

        self.setManualOverride(true);

        level.setBlock(pos,
                state.setValue(TransmitterBlock.IS_RECEIVER, newValue),
                3);

        self.setReceiver(newValue);

        if (self.getLinkedTo() != null) {

            for (BlockPos p : BlockPos.betweenClosed(
                    pos.offset(-32, -32, -32),
                    pos.offset(32, 32, 32))) {

                BlockEntity otherBe = level.getBlockEntity(p);

                if (!(otherBe instanceof TransmitterBlockEntity other)) continue;

                if (self.getLinkedTo().equals(other.getLinkId())) {

                    other.setManualOverride(true);

                    level.setBlock(p,
                            level.getBlockState(p).setValue(
                                    TransmitterBlock.IS_RECEIVER,
                                    !newValue
                            ),
                            3);

                    other.setReceiver(!newValue);

                    break;
                }
            }
        }

        player.displayClientMessage(
                Component.literal(newValue ? "sender → receiver" : "receiver → sender"),
                true
        );

        return InteractionResult.SUCCESS;
    }
}