package me.wurgo.allsounds;

import com.google.common.collect.Maps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageDefinition;
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
import java.util.*;

public class AllSounds {
    public static final String MOD_NAME = "AllSounds";
    public static final String MOD_ID = MOD_NAME.toLowerCase();

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static int total = 0;
    private static int completed = 0;

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

    public static void clearSounds() throws IOException {
        JSONArray sounds = getSounds();
        sounds.clear();

        File soundsFile = new File(FabricLoader.getInstance().getConfigDir() + "/sounds.json");
        FileWriter writer = new FileWriter(soundsFile);

        writer.write(sounds.toJSONString());
        writer.flush();

        log("Cleared sounds.");
    }

    public static ArrayList<String> getRemainingSounds() throws IOException {
        ArrayList<String> remaining = new ArrayList<>();
        JSONArray done = getSounds();
        Map<String, String> map = sortByValue(loadLangFile());

        map.forEach((v, k) -> {
            if (v.startsWith("subtitles") && !done.contains(v)) {
                remaining.add(v);
            }
        });

        return remaining;
    }

    public static List<Integer> getMinMax() throws IOException {
        List<Integer> list = new ArrayList<>();

        JSONArray done = getSounds();
        Map<String, String> lang = loadLangFile();

        total = 0;
        completed = 0;

        lang.forEach((v, k) -> {
            if (v.startsWith("subtitles")) {
                total++;
                if (done.contains(v)) {
                    completed++;
                }
            }
        });

        list.add(completed);
        list.add(total);

        return list;
    }

    private static Map<String, String> loadLangFile() {
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        List<LanguageDefinition> definitions = new ArrayList<>();
        definitions.add(MinecraftClient.getInstance().getLanguageManager().getLanguage());

        Map<String, String> map = Maps.newHashMap();

        for (LanguageDefinition languageDefinition : definitions) {
            String string = String.format("lang/%s.json", languageDefinition.getCode());

            for (String string2 : resourceManager.getAllNamespaces()) {
                try {
                    Identifier identifier = new Identifier(string2, string);

                    for (Resource resource : resourceManager.getAllResources(identifier)) {
                        try {
                            try (InputStream inputStream = resource.getInputStream()) {
                                Language.load(inputStream, map::put);
                            }
                        } catch (IOException var17) {
                            LOGGER.warn("Failed to load translations from {}", resource, var17);
                        }
                    }
                } catch (FileNotFoundException ignored) {
                } catch (Exception var11) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", string2, string, var11.toString());
                }
            }
        }

        return map;
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static void log(String message) {
        LOGGER.info(message);
    }
}
