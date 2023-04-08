package com.akrozora.customtooltips;

import com.akrozora.customtooltips.util.TooltipModifier;
import com.akrozora.customtooltips.util.DataHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.ArrayList;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(CustomTooltips.MOD_ID)
public class CustomTooltips
{
    // Directly reference a slf4j logger
    public static final String MOD_ID = "customtooltips";
    public static final Logger LOGGER = LogUtils.getLogger();


    public CustomTooltips()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);



        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CustomTooltipsConfig.SPEC, "Tooltips-client-config.toml");
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void clientSetup(final FMLClientSetupEvent event){

    }
}
