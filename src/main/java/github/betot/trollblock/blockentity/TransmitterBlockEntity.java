package github.betot.trollblock.blockentity;

import github.betot.trollblock.block.custom.TransmitterBlock;
import github.betot.trollblock.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TransmitterBlockEntity extends BlockEntity {


    private final ItemStackHandler inventory =  new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            TransmitterBlockEntity.this.setChanged();
        }
    };

    private final LazyOptional<ItemStackHandler> optional = LazyOptional.of(() -> this.inventory);

    private UUID linkId;
    private UUID linkedTo = null;

    private boolean receiver = false;
    private UUID linker;

    private boolean manualOverride = false;

    private int energy = 0;

    private boolean can_work = false;

    private int step = 0;

    public static final Set<BlockPos> cachedLinkedBlocks = new HashSet<>();

    private CompoundTag getRedstoneMap(Level level, BlockPos pos) {

        CompoundTag tag = new CompoundTag();

        for (Direction dir : Direction.values()) {

            int power = level.getSignal(pos, dir);

            tag.putBoolean(dir.getName(), power > 0);
        }

        return tag;
    }

    private CompoundTag getEmptyRedstoneMap(Level level, BlockPos pos) {

        CompoundTag tag = new CompoundTag();

        for (Direction dir : Direction.values()) {

            int power = 0;

            tag.putBoolean(dir.getName(), false);
        }

        return tag;
    }

    public TransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRANSMITTER.get(), pos, state);
        this.linkId = UUID.randomUUID();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (linkId == null) {
            linkId = UUID.randomUUID();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putUUID("linkId", linkId);
        tag.putBoolean("receiver", receiver);
        tag.putInt("energy", energy);
        tag.putBoolean("can_work", can_work);
        tag.putInt("step", step);

        if (linkedTo != null) tag.putUUID("linkedTo", linkedTo);
        if (linker != null) tag.putUUID("linker", linker);
        tag.put("Inventory",this.inventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.inventory.deserializeNBT(tag.getCompound("Inventory"));

        linkId = tag.hasUUID("linkId") ? tag.getUUID("linkId") : UUID.randomUUID();
        linkedTo = tag.hasUUID("linkedTo") ? tag.getUUID("linkedTo") : null;
        linker = tag.hasUUID("linker") ? tag.getUUID("linker") : null;

        receiver = tag.getBoolean("receiver");
        energy = tag.getInt("energy");
        can_work = tag.getBoolean("can_work");
        step = tag.getInt("step");
    }

    public UUID getLinkId() {
        return linkId;
    }

    public UUID getLinkedTo() {
        return linkedTo;
    }

    public void setLinkedTo(UUID linkedTo) {
        this.linkedTo = linkedTo;
        setChanged();
    }

    public boolean isManualOverride() {
        return manualOverride;
    }

    public void setManualOverride(boolean manualOverride) {
        this.manualOverride = manualOverride;
        this.setChanged();
    }

    public boolean isReceiver() {
        return receiver;
    }

    public void setReceiver(boolean receiver) {
        this.receiver = receiver;
        setChanged();
    }

    public void setLinker(UUID linker) {
        this.linker = linker;
        this.setChanged();
    }

    public void safeUpdate(Level level, BlockPos pos, BlockState newState) {
        level.setBlock(pos, newState, 3);
        this.setChanged();
    }

    private CompoundTag receivedPower = new CompoundTag();

    public CompoundTag getReceivedPower() {
        return receivedPower;
    }

    public void receiveRedstone(CompoundTag tag) {
        this.receivedPower = tag.copy();
        this.setChanged();

        if (level != null && worldPosition != null) {
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }

    public void sendToReceiver(Level level, BlockPos pos, CompoundTag redstoneData) {

        if (linkedTo == null) return;

        if(this.can_work) {
            for (BlockPos p : BlockPos.betweenClosed(
                    pos.offset(-16, -16, -16),
                    pos.offset(16, 16, 16))) {

                BlockEntity be = level.getBlockEntity(p);

                if (!(be instanceof TransmitterBlockEntity other)) continue;
                if (!linkedTo.equals(other.getLinkId())) continue;

                if (other.isReceiver()) {
                    other.receiveRedstone(redstoneData);
                    level.updateNeighborsAt(p, other.getBlockState().getBlock());
                }

                return;
            }
        }
    }

    public boolean isPowered(Direction dir) {
        return receivedPower.getBoolean(dir.getName());
    }

    // 🔥 LOGIQUE RESEAU
    public void tick(Level level, BlockPos pos, BlockState state, ItemStackHandler inventory) {

        this.setStep(this.getStep() + 1);

        this.can_work = true;

        CompoundTag redstone = getEmptyRedstoneMap(level, pos);
        sendToReceiver(level, pos, redstone);

        this.can_work = false;

        if (level.isClientSide) return;
        if (linkedTo == null) return;
        if (manualOverride) return;
        if (receiver) return;

        //#========RECEIVER LOGIC (déplacer ici sinon si pas de tablet dans le block lors du posage les block ne se pair pas)
        for (BlockPos p : BlockPos.betweenClosed(
                pos.offset(-16, -16, -16),
                pos.offset(16, 16, 16))) {

            BlockEntity be = level.getBlockEntity(p);

            if (!(be instanceof TransmitterBlockEntity other)) continue;
            if (!linkedTo.equals(other.getLinkId())) continue;

            if (!manualOverride) {

                boolean shouldBeReceiver =
                        linkId.compareTo(other.getLinkId()) > 0;

                if (receiver != shouldBeReceiver) {

                    receiver = shouldBeReceiver;

                    BlockState newState = state.setValue(
                            TransmitterBlock.IS_RECEIVER,
                            receiver
                    );

                    safeUpdate(level, pos, newState);
                    level.sendBlockUpdated(pos, state, newState, 3);
                }
            }

        }

        ItemStack tablet = inventory.getStackInSlot(0);

        if (tablet.isEmpty() || !tablet.is(ModItems.ENERGY_TABLET.get())) {
            this.can_work = false;
            return;
        }

        int energyCost = 1;
        int remaining = tablet.getMaxDamage() - tablet.getDamageValue();

        if (remaining < energyCost) {
            this.can_work = false;
            return;
        }

        this.can_work = true;

        if(this.getStep() >= 100) {
            tablet.setDamageValue(tablet.getDamageValue() + energyCost);
            this.setStep(0);
        }

        inventory.setStackInSlot(0, tablet);

        redstone = getRedstoneMap(level, pos);
        sendToReceiver(level, pos, redstone);

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return this.optional.cast();
        }

        return super.getCapability(cap);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        this.optional.invalidate();
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ItemStack getStack() {
        return this.inventory.getStackInSlot(0);
    }

    public void setStack(ItemStack stack) {
        this.inventory.setStackInSlot(0, stack);
        setChanged();
    }

    public LazyOptional<ItemStackHandler> getOptional() {
        return this.optional;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public boolean isCan_work() {
        return can_work;
    }

    public void setCan_work(boolean can_work) {
        this.can_work = can_work;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}