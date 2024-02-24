package aspi.cho.resourcescondenser;

import aspi.cho.resourcescondenser.block.ModBlocks;
import aspi.cho.resourcescondenser.block.entity.ModBlockEntities;
import aspi.cho.resourcescondenser.config.ResourcesCondenserCommonConfigs;
import aspi.cho.resourcescondenser.item.ModItems;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import aspi.cho.resourcescondenser.screen.CondenserScreen;
import aspi.cho.resourcescondenser.screen.ModMenuTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Hashtable;

@Mod(ResourcesCondenser.MOD_ID)
public class ResourcesCondenser {
    public static final String MOD_ID = "resources_condenser";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static String text;
    public static Hashtable<String, Hashtable<String, Double>> CONDENSE_ITEM_DIMENSION_DEPENDENCY;

    public ResourcesCondenser() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ResourcesCondenserCommonConfigs.SPEC, "resourcescondenser-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Gson g = new Gson();
        Type listType = new TypeToken<Hashtable<String, Hashtable<String, Double>>>(){}.getType();
        String CondenseItmDimDependency = ResourcesCondenserCommonConfigs.CondenseItemDimensionDependencyString.get();
        CONDENSE_ITEM_DIMENSION_DEPENDENCY = g.fromJson(CondenseItmDimDependency, listType);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.CONDENSER_MENU.get(), CondenserScreen::new);
        }
    }
}
