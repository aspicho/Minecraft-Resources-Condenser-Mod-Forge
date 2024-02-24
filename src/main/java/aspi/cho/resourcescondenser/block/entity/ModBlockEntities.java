package aspi.cho.resourcescondenser.block.entity;

import aspi.cho.resourcescondenser.ResourcesCondenser;
import aspi.cho.resourcescondenser.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ResourcesCondenser.MOD_ID);

    public static final RegistryObject<BlockEntityType<CondenserBlockEntity>> CONDENSER =
            BLOCK_ENTITIES.register("condenser", () -> BlockEntityType.Builder.of(CondenserBlockEntity::new,
                    ModBlocks.CONDENSER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
