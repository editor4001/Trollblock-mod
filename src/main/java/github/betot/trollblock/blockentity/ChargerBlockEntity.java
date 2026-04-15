package github.betot.trollblock.blockentity;

import github.betot.trollblock.block.ModBlocks;
import github.betot.trollblock.block.custom.ChargerBlock;
import github.betot.trollblock.block.custom.TransmitterBlock;
import github.betot.trollblock.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ChargerBlockEntity extends BlockEntity {

    private final ItemStackHandler inventory =  new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            ChargerBlockEntity.this.setChanged();
        }
    };

    private final LazyOptional<ItemStackHandler> optional = LazyOptional.of(() -> this.inventory);

    private int glowstone_amount = 0;
    private int energy = 0;

    public ChargerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHARGER.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("glowstone_amount",glowstone_amount);
        tag.putInt("energy",energy);
        tag.put("Inventory",this.inventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.inventory.deserializeNBT(tag.getCompound("Inventory"));

        if(tag.contains("glowstone_amount")){
            this.glowstone_amount = tag.getInt("glowstone_amount");
        }

        if(tag.contains("energy")){
            this.energy = tag.getInt("energy");
        }

    }

    public int getGlowstone_amount() {
        return glowstone_amount;
    }

    public void setGlowstone_amount(int glowstone_amount) {
        this.glowstone_amount = glowstone_amount;
        setChanged();
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        setChanged();
    }

    public static void safeUpdate(Level level, BlockPos pos, BlockState newState) {
        level.setBlock(pos, newState, 3);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ItemStackHandler inventory) {
        if (level.isClientSide) return;

        ChargerBlockEntity self = (ChargerBlockEntity) level.getBlockEntity(pos);
        if (self == null) return;

        if (state.is(ModBlocks.CHARGER.get())) {


            if (inventory.getStackInSlot(0).is(ModItems.ENERGY_TABLET.get())) {

                ItemStack item = inventory.getStackInSlot(0);

                int damage = item.getDamageValue();

                if (damage > 0 && self.getEnergy() > 0) {

                    int energyUsed = Math.min(damage, self.getEnergy());

                    if (damage - energyUsed <= 0) {

                        inventory.setStackInSlot(0, new ItemStack(Items.AIR));
                        inventory.setStackInSlot(0, new ItemStack(ModItems.ENERGY_TABLET.get()));

                    } else {
                        inventory.setStackInSlot(0, new ItemStack(Items.AIR));
                        ItemStack energyTablet = new ItemStack(ModItems.ENERGY_TABLET.get());
                        energyTablet.setDamageValue(damage - energyUsed);

                        inventory.setStackInSlot(0, energyTablet);
                    }

                    self.setEnergy(self.getEnergy() - energyUsed);
                }
            }


            if(self.energy == 0){
                self.glowstone_amount = 0;
            }else if(self.energy > 0 && self.energy <= 50){
                self.glowstone_amount = 1;
            }else if(self.energy > 50 && self.energy <= 100){
                self.glowstone_amount = 2;
            }else if(self.energy > 100 && self.energy <= 150){
                self.glowstone_amount = 3;
            }else if(self.energy > 150 && self.energy <= 200){
                self.glowstone_amount = 4;
            }

            BlockState newState = state
                    .setValue(ChargerBlock.GLOWSTONE_AMOUNT, self.getGlowstone_amount())
                    .setValue(ChargerBlock.ENERGY_AMOUNT, self.getEnergy());

            if (newState != state) {
                safeUpdate(level, pos, newState);
            }
        }
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
}