package com.akrozora.customtooltips.util;

import com.akrozora.customtooltips.CustomTooltips;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.crafting.Ingredient;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DataHelper {

    public static boolean hasDefault = false;
    public static TextColor defaultColor = null;
    public static boolean doReplace = false;


    private static final String DEFAULT = "default";
    private static final String REPLACE = "replace";
    private static final String TARGET = "target";
    private static final String TEXT = "text";
    private static final String COLOR = "color";
    private static final String TEXTCOMPONENT = "tooltip";
    private static final String TEXTCOMPONENT_LINE = "tooltip_line";
    private static final String TRANSLATECOMPONENT = "translate";
    private static final String TRANSLATECOMPONENT_LINE = "translate_line";
    public static ArrayList<TooltipModifier> modifierArrayList = new ArrayList<>();




    public static ArrayList<TooltipModifier> createList(){
        JsonArray jsonArray = getFile();
        ArrayList<TooltipModifier> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject component = jsonArray.get(i).getAsJsonObject();
            TooltipModifier tooltip;
            Ingredient item = Ingredient.EMPTY;
            List<MutableComponent> tooltipText = new ArrayList<>();
            if (component.has(TARGET)){
                if(component.get(TARGET).isJsonObject()) {
                    item = Ingredient.fromValues(Stream.of(Ingredient.valueFromJson(component.get(TARGET).getAsJsonObject())));
                }
            } else {
                CustomTooltips.LOGGER.warn("JSON file has to contain member \"" + TARGET + "\"");
            }
            if (!component.has(TEXT)){
                CustomTooltips.LOGGER.warn("JSON file has to contain member \"" + TEXT + "\"");
            } else if (component.get(TEXT).isJsonArray()){

                tooltipText = getTooltipsFromArray(component.getAsJsonArray(TEXT));

            } else if (component.get(TEXT).isJsonObject()) {

                tooltipText.add(getComponentFromObject(component.get(TEXT).getAsJsonObject()));

            }
            tooltip = new TooltipModifier(item, tooltipText);

            if(component.has(REPLACE)){
                tooltip.setReplace(component.get(REPLACE).getAsBoolean());
            }
            list.add(tooltip);
        }
        return list;
    }
    private static BaseComponent getComponentFromObject(JsonObject object){
        BaseComponent tooltip = new TextComponent("");
        if (object.has(TEXTCOMPONENT)&&object.has(TRANSLATECOMPONENT)) {
            CustomTooltips.LOGGER.warn("You cannot have two text Components in one Spot");

        } else if (object.has(TEXTCOMPONENT)) {
            tooltip = getTextComponent(object, TEXTCOMPONENT);

        } else if (object.has(TEXTCOMPONENT_LINE)) {
            tooltip = getTextComponent(object, TEXTCOMPONENT_LINE);

        } else if (object.has(TRANSLATECOMPONENT)) {
            tooltip = getTranslatable(object, TRANSLATECOMPONENT);

        } else if (object.has(TRANSLATECOMPONENT_LINE)) {
            tooltip = getTranslatable(object, TRANSLATECOMPONENT_LINE);

        } else {
            CustomTooltips.LOGGER.warn("You cannot have a field without Text");
        }
        return tooltip;
    }

    private static TranslatableComponent getTranslatable(JsonObject object, String key){
        TranslatableComponent translatableComponent = new TranslatableComponent(object.get(key).getAsString());
        if (object.has(COLOR)) {
            object.get(COLOR).getAsString();
            translatableComponent = (TranslatableComponent) translatableComponent.setStyle(Style.EMPTY.withColor(TextColor.parseColor(object.get(COLOR).getAsString())));
        }
        return translatableComponent;
    }
    private static TextComponent getTextComponent(JsonObject object, String key){
        TextComponent textComponent = new TextComponent(object.get(key).getAsString());
        if (object.has(COLOR)) {
            object.get(COLOR).getAsString();
            textComponent = (TextComponent) textComponent.setStyle(Style.EMPTY.withColor(TextColor.parseColor(object.get(COLOR).getAsString())));
        }
        return textComponent;
    }

    private static List<MutableComponent> getTooltipsFromArray(JsonArray componentArray){
        List<MutableComponent> tooltips = new ArrayList<>();
        if(componentArray.size()==0){
            CustomTooltips.LOGGER.warn("The size of the Array \""+TEXT+ "\" cannot be zero");
            return tooltips;
        }
        int lineIndex = 0;
        for (int i = 0; i < componentArray.size(); i++) {
            if(!componentArray.get(i).isJsonObject()) continue;
            JsonObject object = componentArray.get(i).getAsJsonObject();
            if(object.has(TEXTCOMPONENT_LINE)||object.has(TRANSLATECOMPONENT_LINE)||i==0){
                tooltips.add(getComponentFromObject(object));
                if(i!=0) lineIndex++;
            } else {
                tooltips.get(lineIndex).append(getComponentFromObject(object));
            }
        }
        return tooltips;
    }

    public static void setDefaultSettings(JsonObject object){
        if(object.has(DEFAULT)){
            JsonObject defaultField = object.get(DEFAULT).getAsJsonObject();
            if(defaultField.has(COLOR)){
                defaultField.get(COLOR).getAsString();
            }
        }
    }

    public static JsonArray getFile() {
        JsonArray modifier = new JsonArray();
        try {
            // create Gson instance
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("config/tooltipTest.json"));
            // convert book object to JSON file
            modifier = gson.fromJson(reader, JsonArray.class);
            // close writer
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return modifier;
    }
}
