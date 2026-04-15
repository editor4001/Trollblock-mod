package github.betot.trollblock.block;

import github.betot.trollblock.block.custom.ChargerBlock;
import github.betot.trollblock.block.custom.QuickSandBlock;
import github.betot.trollblock.block.custom.SoilBlock;
import github.betot.trollblock.block.custom.TransmitterBlock;
import github.betot.trollblock.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static github.betot.trollblock.Trollblock.MODID;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);



    public static final RegistryObject<Block> SOIL = registerBlock("soil",
            () -> new SoilBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).sound(SoundType.ROOTED_DIRT).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> STEEL = registerBlock("steel",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.ANVIL).explosionResistance(2400.5f).pushReaction(PushReaction.BLOCK).strength(100.5f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> QUICK_SAND = registerBlock("quick_sand",
            () -> new QuickSandBlock(BlockBehaviour.Properties.copy(Blocks.SAND).sound(SoundType.SAND).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> TRANSMITTER = registerBlock("transmitter",
            () -> new TransmitterBlock(BlockBehaviour.Properties.copy(Blocks.REDSTONE_BLOCK).sound(SoundType.STONE).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> CHARGER = registerBlock("charger",
            () -> new ChargerBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).sound(SoundType.STONE).requiresCorrectToolForDrops()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
