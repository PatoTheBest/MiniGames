package me.patothebest.gamecore.player;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.util.Callback;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
public class PlayerSkinCache implements Module {

    private static final String FALLBACK_STEVE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI3YWY5ZTQ0MTEyMTdjN2RlOWM2MGFjYmQzYzNmZDY1MTk3ODMzMzJhMWIzYmM1NmZiZmNlOTA3MjFlZjM1In19fQ==";
    private final Map<String, String> HEAD_CACHE = new ConcurrentHashMap<>();
    private final Map<String, Queue<Callback<String>>> HEAD_CACHE_CALLBACK = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final PluginScheduler pluginScheduler;

    @Inject private PlayerSkinCache(PluginScheduler pluginScheduler) {
        this.pluginScheduler = pluginScheduler;
    }

    public void getPlayerSkin(String playerName, Callback<String> callback) {
        try {
            readLock.lock();
            if (HEAD_CACHE.containsKey(playerName)) {
                callback.call(HEAD_CACHE.get(playerName));
            } else {
                if (HEAD_CACHE_CALLBACK.containsKey(playerName)) {
                    HEAD_CACHE_CALLBACK.get(playerName).add(callback);
                } else {
                    HEAD_CACHE_CALLBACK.put(playerName, new ConcurrentLinkedQueue<>());
                    HEAD_CACHE_CALLBACK.get(playerName).add(callback);
                    cacheHead(playerName);
                }
            }
        } finally {
            readLock.unlock();
        }
    }

    private void cacheHead(String playerName) {
        pluginScheduler.runTaskAsynchronously(() -> {
            String headValue = getHeadValue(playerName);
            try {
                writeLock.lock();
                HEAD_CACHE.put(playerName, headValue);
            } finally {
                writeLock.unlock();
            }

            Queue<Callback<String>> callbacks = HEAD_CACHE_CALLBACK.remove(playerName);
            pluginScheduler.runTask(() -> {
                while (!callbacks.isEmpty()) {
                    callbacks.poll().call(headValue);
                }
            });
        });
    }

    private String getHeadValue(String name) {
        try {
            String result = getURLContent("https://api.mojang.com/users/profiles/minecraft/" + name);
            Gson g = new Gson();
            JsonObject obj = g.fromJson(result, JsonObject.class);
            String uid = obj.get("id").toString().replace("\"", "");
            String signature = getURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);
            obj = g.fromJson(signature, JsonObject.class);
            String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
            String decoded = new String(Base64.getDecoder().decode(value));
            obj = g.fromJson(decoded, JsonObject.class);
            String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
            byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
            return new String(Base64.getEncoder().encode(skinByte));
        } catch (Exception e) {
            return FALLBACK_STEVE;
        }
    }

    private String getURLContent(String urlStr) throws MalformedURLException {
        StringBuilder sb = new StringBuilder();
        URL url = new URL(urlStr);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception ignored) { }
        return sb.toString();
    }

}
