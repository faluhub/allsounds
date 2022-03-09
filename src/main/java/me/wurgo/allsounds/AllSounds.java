package me.wurgo.allsounds;

import com.google.common.collect.Maps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllSounds {
    public static final String MOD_NAME = "AllSounds";
    public static final String MOD_ID = MOD_NAME.toLowerCase();

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ArrayList<String> playedSounds = new ArrayList<>();

    public static JSONArray getSounds() throws IOException {
        JSONParser parser = new JSONParser();

        File soundsFile = new File(FabricLoader.getInstance().getConfigDir() + "/sounds.json");
        if (soundsFile.createNewFile()) { log("Created 'sounds.json'"); }

        FileReader reader = new FileReader(soundsFile);
        try {
            Object obj = parser.parse(reader);
            return (JSONArray) obj;
        } catch (ParseException e) {
            return new JSONArray();
        }
    }

    public static void addSound(String sound) throws IOException {
        JSONArray sounds = getSounds();
        sounds.add(sound);

        File soundsFile = new File(FabricLoader.getInstance().getConfigDir() + "/sounds.json");
        FileWriter writer = new FileWriter(soundsFile);

        writer.write(sounds.toJSONString());
        writer.flush();

        log("Added sound '" + sound + "' to played sounds.");
    }

    public static ArrayList<String> getRemainingSounds() throws IOException {
        ArrayList<String> remaining = new ArrayList<>();
        JSONArray done = getSounds();

        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        List<LanguageDefinition> definitions = new ArrayList<>();
        definitions.add(MinecraftClient.getInstance().getLanguageManager().getLanguage());

        Map<String, String> map = Maps.newHashMap();

        for (LanguageDefinition languageDefinition : definitions) {
            String string = String.format("lang/%s.json", languageDefinition.getCode());

            for (String string2 : resourceManager.getAllNamespaces()) {
                try {
                    Identifier identifier = new Identifier(string2, string);
                    loadLangFile(resourceManager.getAllResources(identifier), map);
                } catch (FileNotFoundException ignored) {
                } catch (Exception var11) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", string2, string, var11.toString());
                }
            }
        }

        map.forEach((v, k) -> {
            if (v.startsWith("subtitles") && !done.contains(v)) {
                remaining.add(v);
            }
        });

        return remaining;
    }

    private static void loadLangFile(List<Resource> resources, Map<String, String> translationMap) {
        for (Resource resource : resources) {
            try {
                try (InputStream inputStream = resource.getInputStream()) {
                    Language.load(inputStream, translationMap::put);
                }
            } catch (IOException var17) {
                LOGGER.warn("Failed to load translations from {}", resource, var17);
            }
        }
    }

    public static void log(String message) {
        LOGGER.info(message);
    }
}
