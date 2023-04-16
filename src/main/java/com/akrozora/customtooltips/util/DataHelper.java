package com.akrozora.customtooltips.util;

import com.akrozora.customtooltips.CustomTooltips;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.crafting.Ingredient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataHelper {
    public static TextColor DefaultColor = null;
    public static State DefaultState = State.TOP;
    public enum State{
        TOP("top"),
        BOTTOM("bottom"),
        REPLACE("replace");

        private final String state;
        State(String state){
            this.state = state;
        }

        public static State getStateFromString(String state){
            if(Objects.equals(state, TOP.state)){
                return TOP;
            } else if (Objects.equals(state, BOTTOM.state)){
                return BOTTOM;
            } else if (Objects.equals(state, REPLACE.state)){
                return REPLACE;
            } else {
                return TOP;
            }
        }

    }
    private static final String PATH_DIRECTORY = "config/CustomTooltips";
    private static final String DEFAULT = "default";
    private static final String TOOLTIPS = "tooltips";
    private static final String STATE = "state";
    private static final String TARGET = "target";
    private static final String TEXT = "text";
    private static final String COLOR = "color";
    private static final String TEXTCOMPONENT = "tooltip";
    private static final String TEXTCOMPONENT_LINE = "tooltip_line";
    private static final String TRANSLATECOMPONENT = "translate";
    private static final String TRANSLATECOMPONENT_LINE = "translate_line";
    public static ArrayList<TooltipModifier> modifierArrayList = new ArrayList<>();
    public static ArrayList<TooltipModifier> createList(){
        List<JsonElement> jsonElementList= getFilesFromDirectory();
        ArrayList<TooltipModifier> list = new ArrayList<>();
        for (int i = 0; i < jsonElementList.size(); i++) {
            JsonArray jsonArray = getArrayFromElement(jsonElementList.get(i));
            for (int j = 0; j < jsonArray.size(); j++) {
                JsonObject component = jsonArray.get(j).getAsJsonObject();
                TooltipModifier tooltip = createTooltip(component);
                if(tooltip.item!=Ingredient.EMPTY){
                    list.add(tooltip);
                }
            }
        }
        return list;
    }
    public static TooltipModifier createTooltip(JsonObject component){
        Ingredient item = Ingredient.EMPTY;
        List<MutableComponent> tooltipText = new ArrayList<>();

        if (component.has(TARGET)){
            if(component.get(TARGET).isJsonObject()) {
                item = Ingredient.fromJson(component.get(TARGET).getAsJsonObject());
            }
        } else {
            CustomTooltips.LOGGER.warn("JSON file has to contain member \"" + TARGET + "\"");
        }


        if(component.has("item")||component.has("tag")){
            item = Ingredient.fromJson(component);
        }
        if (!component.has(TEXT)){
            CustomTooltips.LOGGER.warn("JSON file has to contain member \"" + TEXT + "\"");
        } else if (component.get(TEXT).isJsonArray()){
            tooltipText = getTooltipsFromArray(component.getAsJsonArray(TEXT));
        } else if (component.get(TEXT).isJsonObject()) {
            tooltipText.add(getComponentFromObject(component.get(TEXT).getAsJsonObject()));
        }
        TooltipModifier tooltip = new TooltipModifier(item, tooltipText);

        if(component.has(STATE)){
            tooltip.setStatefromString(component.get(STATE).getAsString());
        } else {
            tooltip.setState(DefaultState);
        }
        return tooltip;
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
        return (TranslatableComponent) setComponentColor(object, translatableComponent);
    }
    private static TextComponent getTextComponent(JsonObject object, String key){
        TextComponent textComponent = new TextComponent(object.get(key).getAsString());
        return (TextComponent) setComponentColor(object, textComponent);
    }

    private static MutableComponent setComponentColor(JsonObject object, MutableComponent inputComponent){
        if (object.has(COLOR)) {
            System.out.println(inputComponent.toString());
            System.out.println("Object has COLOR: "+ object.get(COLOR).getAsString());

            inputComponent.setStyle(Style.EMPTY.withColor(TextColor.parseColor(object.get(COLOR).getAsString())));
        } else if (DefaultColor !=null) {
            System.out.println("Object doesn't have COLOR!!");
            inputComponent.withStyle(Style.EMPTY.withColor(DefaultColor));
        }
        return inputComponent;
    }

    private static List<MutableComponent> getTooltipsFromArray(JsonArray componentArray){
        List<MutableComponent> tooltips = new ArrayList<>();
        if(componentArray.size()==0){
            CustomTooltips.LOGGER.warn("The size of the Array \""+TEXT+ "\" cannot be zero");
            return tooltips;
        }
        int lineIndex = 0;
        System.out.println("Getting TOOLTIPS from ARRAY!!");
        for (int i = 0; i < componentArray.size(); i++) {
            if(!componentArray.get(i).isJsonObject()) continue;
            JsonObject object = componentArray.get(i).getAsJsonObject();
            if(object.has(TEXTCOMPONENT_LINE)||object.has(TRANSLATECOMPONENT_LINE)||i==0){
                tooltips.add(getComponentFromObject(object));
                System.out.println(getComponentFromObject(object));
                if(i!=0) lineIndex++;
            } else {
                tooltips.get(lineIndex).append(getComponentFromObject(object));
            }
        }
        return tooltips;
    }

    public static void setDefaultSettings(JsonObject object){
        if(object.has(COLOR)){
             DefaultColor = TextColor.parseColor(object.get(COLOR).getAsString());
        }
        if(object.has(STATE)){
            DefaultState = State.getStateFromString(object.get(STATE).getAsString());
        }
    }

    public static JsonArray getArrayFromElement(JsonElement element){
        JsonArray jsonArray = new JsonArray();
        if(element.isJsonArray()){
            jsonArray = element.getAsJsonArray();
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if(object.has(DEFAULT)){
                JsonObject defaultField = object.get(DEFAULT).getAsJsonObject();
                setDefaultSettings(defaultField);
            }
            if(object.has(TOOLTIPS)){
                jsonArray = object.getAsJsonArray(TOOLTIPS);
            }
            if(!object.has(TOOLTIPS)&& !object.has(DEFAULT)) {
                CustomTooltips.LOGGER.warn("The Json file is not Properly written: if you dont have fields \""+DEFAULT+"\" or \""+TOOLTIPS+"\" just use an Array");
            }
        }
        return jsonArray;

    }

    public static JsonElement getJsonElement(File file) {
        JsonElement jsonFile = new JsonObject();
        try {
            // create Gson instance
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(file.toPath());
            // convert book object to JSON file
            jsonFile = gson.fromJson(reader, JsonElement.class);
            // close writer
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonFile;
    }

    public static List<JsonElement> getFilesFromDirectory(){
        List<JsonElement> jsonElements = new ArrayList<>();
        File directory = new File(PATH_DIRECTORY);
        if(directory.isDirectory()){
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                     jsonElements.add(getJsonElement(file));
                }
            }
        } else {
            CustomTooltips.LOGGER.warn("Directory \"CustomTooltips\" doesn't exist or is not a directory");
        }
        return jsonElements;
    }

    public static void createDirectory(){
        File directory = new File(PATH_DIRECTORY);
        if(directory.mkdirs()){
            createExampleJson(directory);
        } else if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            List<File> jsonFiles = new ArrayList<>();
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    jsonFiles.add(file);
                }
            }
            if(jsonFiles.size()==0){
                createExampleJson(directory);
            }
        }
    }
    public static void createExampleJson(File directory){
        File exampleFile = new File(directory, "example.json");
        try {
            String jsonInhalt =
                    "{ \"default\":{\n" +
                    "  \"color\": \"green\",\n" +
                    "  \"state\": \"bottom\"\n" +
                    "},\n" +
                    "  \"tooltips\": [\n" +
                    "    {\n" +
                    "      \"item\": \"minecraft:music_disc_cat\",\n" +
                    "      \"text\": {\"tooltip\": \"By default any Tooltip is set on Top of existing ones and the default color is white\"}\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"item\": \"minecraft:music_disc_otherside\",\n" +
                    "      \"text\": {\"tooltip\": \"you can set your own default color and position if you create another field \\\"default\\\"\"}\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"item\": \"minecraft:music_disc_blocks\",\n" +
                    "      \"text\": {\"tooltip\": \"if you don`t want to set new default setting you can just write the json file as an Array\"}\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"item\": \"minecraft:music_disc_chirp\",\n" +
                    "      \"text\": {\"tooltip\": \"you can write as many JSON files as you want\"}\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"item\": \"minecraft:music_disc_chirp\",\n" +
                    "      \"text\": {\"tooltip\": \"if you have two duplicate items only the first will be added as tooltip\"}\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"item\": \"minecraft:music_disc_13\",\n" +
                    "      \"text\": [\n" +
                    "        {\"tooltip_line\": \"The\", \"color\": \"#ff2929\"},\n" +
                    "        {\"tooltip_line\": \"Tooltip\", \"color\": \"#ff7029\"},\n" +
                    "        {\"tooltip_line\": \"can\", \"color\": \"#ffea29\"},\n" +
                    "        {\"tooltip_line\": \"be\", \"color\": \"#8dff29\"},\n" +
                    "        {\"tooltip_line\": \"an\", \"color\": \"#29ffc9\"},\n" +
                    "        {\"tooltip_line\": \"Array\", \"color\": \"#2965ff\"},\n" +
                    "        {\"tooltip_line\": \"so\", \"color\": \"#ff29d8\"},\n" +
                    "        {\"tooltip_line\": \"you\", \"color\": \"#ff2929\"},\n" +
                    "        {\"tooltip_line\": \"can\", \"color\": \"green\"},\n" +
                    "        {\"tooltip_line\": \"set\", \"color\": \"red\"},\n" +
                    "        {\"tooltip_line\": \"every\", \"color\": \"yellow\"},\n" +
                    "        {\"tooltip_line\": \"color\", \"color\": \"dark_red\"},\n" +
                    "        {\"tooltip_line\": \"individually\", \"color\": \"aqua\"}\n" +
                    "      ],\n" +
                    "      \"state\": \"replace\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"item\": \"minecraft:music_disc_pigstep\",\n" +
                    "      \"text\": {\"tooltip\": \"You can add to existing Tooltips and set the Position with \\\"state\\\"\"},\n" +
                    "      \"state\": \"bottom\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"tag\": \"minecraft:planks\",\n" +
                    "      \"text\": {\"tooltip\": \"You can add Tooltips to any Tag\"}\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            FileWriter writer = new FileWriter(exampleFile);
            writer.write(jsonInhalt);
            writer.close();
            System.out.println("JSON-Datei erstellt: " + exampleFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
