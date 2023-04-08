package com.akrozora.customtooltips.mixin;

import com.akrozora.customtooltips.util.DataHelper;
import com.akrozora.customtooltips.util.TooltipModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.List;

@Mixin(ItemStack.class)
public class MixinItemStack {


    @Redirect(method = "getTooltipLines", at = @At( value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V" ))
    public void appendHoverMixin(Item item, ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag){
        boolean doAppend = true;
        for (int i = 0; i < DataHelper.modifierArrayList.size(); i++) {
            TooltipModifier modifier = DataHelper.modifierArrayList.get(i);
            if(modifier.item.test(stack)){
                tooltip.addAll(modifier.getTooltip());
                if(modifier.isReplace()) {
                    doAppend = false;
                }
                break;
            }
        }

        if(doAppend){
            item.appendHoverText(stack,level,tooltip,flag);
        }
    }
}
