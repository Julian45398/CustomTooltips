package com.akrozora.customtooltips.util;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class TooltipModifier {
    public final Ingredient item;
    public List<MutableComponent> tooltip;
    public DataHelper.State state = DataHelper.State.TOP;
    public TooltipModifier(Ingredient item, List<MutableComponent> tooltip){
        this.item = item;
        this.tooltip = tooltip;
    }
    public void setStatefromString(String string) {
        DataHelper.State state = DataHelper.State.getStateFromString(string);
        this.state = state;
    }
    public void setState(DataHelper.State state) {
        this.state = state;
    }
    public DataHelper.State getState() {
        return this.state;
    }

    public void setAllColor(TextColor color){
        for (int i = 0; i < this.tooltip.size(); i++) {
            this.tooltip.get(i).withStyle(Style.EMPTY.withColor(color));
        }
    }

    public List<MutableComponent> getTooltip() {
        return this.tooltip;
    }



}
