package aspi.cho.resourcescondenser.block;

import aspi.cho.resourcescondenser.ResourcesCondenser;
import aspi.cho.resourcescondenser.block.custom.Condenser;
import aspi.cho.resourcescondenser.block.custom.StrangeMatterBlock;
import aspi.cho.resourcescondenser.item.ModCreativeModeTab;
import aspi.cho.resourcescondenser.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ResourcesCondenser.MOD_ID);

    public static final RegistryObject<Block> STRANGE_MATTER_BLOCK = registerBlock("strange_matter_block",
            () -> new StrangeMatterBlock(
                    BlockBehaviour.Properties.of(Material.STONE)
                            .strength(6f)
                            .requiresCorrectToolForDrops()
                            .lightLevel(state -> state.getValue(StrangeMatterBlock.LIT) ? 15 : 0)),
            ModCreativeModeTab.RES_CONDENS_TAB);

    public static final RegistryObject<Block> CONDENSER = registerBlock("condenser",
            () -> new Condenser(
                    BlockBehaviour.Properties.of(Material.METAL)
                            .strength(1.5f)
                            .noOcclusion()),
            ModCreativeModeTab.RES_CONDENS_TAB);


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registryBlockItem(name, toReturn, tab);
        return  toReturn;
    }

    private static <T extends Block> RegistryObject<BlockItem> registryBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
