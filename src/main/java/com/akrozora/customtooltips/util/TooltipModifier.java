package com.akrozora.customtooltips.util;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class TooltipModifier {
    public final Ingredient item;
    public List<MutableComponent> tooltip;
    private boolean replace = false;
    public TooltipModifier(Ingredient item, List<MutableComponent> tooltip){
        this.item = item;
        this.tooltip = tooltip;
    }
    public void setReplace(boolean replace) {
        this.replace = replace;
    }
    public boolean isReplace() {
        return this.replace;
    }
    public List<MutableComponent> getTooltip() {
        return this.tooltip;
    }
}
