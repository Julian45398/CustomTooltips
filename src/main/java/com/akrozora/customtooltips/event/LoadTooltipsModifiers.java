package com.akrozora.customtooltips.event;


import com.akrozora.customtooltips.CustomTooltips;
import com.akrozora.customtooltips.util.DataHelper;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CustomTooltips.MOD_ID)
public class LoadTooltipsModifiers {


    @SubscribeEvent
    public static void createTooltipList(TagsUpdatedEvent evt) {
        DataHelper.modifierArrayList = DataHelper.createList();
    }
    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent evt) {
        DataHelper.modifierArrayList = DataHelper.createList();
    }
}
