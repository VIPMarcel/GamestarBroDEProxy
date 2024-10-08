package vip.marcel.gamestarbro.proxy.utils.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UUIDFetcher {

    public static final long FEBRUARY_2015 = 1422748800000L;
    private static Gson gson;
    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
    private static Map<String, UUID> uuidCache;
    private static Map<UUID, String> nameCache;
    private static ExecutorService pool;
    private String name;
    private UUID id;

    public static void getUUID(final String name, final Consumer<UUID> action) {
        UUIDFetcher.pool.execute(() -> action.accept(getUUID(name)));
    }

    public static UUID getUUID(final String name) {
        return getUUIDAt(name, System.currentTimeMillis());
    }

    public static void getUUIDAt(final String name, final long timestamp, final Consumer<UUID> action) {
        UUIDFetcher.pool.execute(() -> action.accept(getUUIDAt(name, timestamp)));
    }

    public static UUID getUUIDAt(String name, final long timestamp) {
        name = name.toLowerCase();
        if(UUIDFetcher.uuidCache.containsKey(name)) {
            return UUIDFetcher.uuidCache.get(name);
        }
        try {
            final HttpURLConnection connection = (HttpURLConnection)new URL(String.format(UUID_URL, name, timestamp / 1000L)).openConnection();
            connection.setReadTimeout(5000);
            final UUIDFetcher data = (UUIDFetcher)UUIDFetcher.gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), (Class)UUIDFetcher.class);
            UUIDFetcher.uuidCache.put(name, data.id);
            UUIDFetcher.nameCache.put(data.id, data.name);
            return data.id;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getName(final UUID uuid, final Consumer<String> action) {
        UUIDFetcher.pool.execute(() -> action.accept(getName(uuid)));
    }

    public static String getName(final UUID uuid) {
        if(UUIDFetcher.nameCache.containsKey(uuid)) {
            return UUIDFetcher.nameCache.get(uuid);
        }
        try {
            final HttpURLConnection connection = (HttpURLConnection)new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);
            final UUIDFetcher[] nameHistory = (UUIDFetcher[])UUIDFetcher.gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), (Class)UUIDFetcher[].class);
            final UUIDFetcher currentNameData = nameHistory[nameHistory.length - 1];
            UUIDFetcher.uuidCache.put(currentNameData.name.toLowerCase(), uuid);
            UUIDFetcher.nameCache.put(uuid, currentNameData.name);
            return currentNameData.name;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clearCache() {
        UUIDFetcher.uuidCache.clear();
        UUIDFetcher.nameCache.clear();
    }

    static {
        UUIDFetcher.gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
        UUIDFetcher.uuidCache = new HashMap<>();
        UUIDFetcher.nameCache = new HashMap<>();
        UUIDFetcher.pool = Executors.newCachedThreadPool();
    }

}
