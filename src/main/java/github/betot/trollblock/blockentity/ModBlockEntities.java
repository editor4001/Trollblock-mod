package github.betot.trollblock.blockentity;

import github.betot.trollblock.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static github.betot.trollblock.Trollblock.MODID;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<TransmitterBlockEntity>> TRANSMITTER =
            BLOCK_ENTITIES.register("transmitter",
                    () -> BlockEntityType.Builder.of(
                            TransmitterBlockEntity::new,
                            ModBlocks.TRANSMITTER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<ChargerBlockEntity>> CHARGER =
            BLOCK_ENTITIES.register("charger",
                    () -> BlockEntityType.Builder.of(
                            ChargerBlockEntity::new,
                            ModBlocks.CHARGER.get()
                    ).build(null));
}