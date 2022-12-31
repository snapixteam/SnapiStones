package ru.mcsnapix.snapistones.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.xseries.XMaterial;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@UtilityClass
public class Utils {
    private final SnapiStones plugin = SnapiStones.get();
    private final Logger logger = plugin.getSLF4JLogger();

    public String getItemStackName(XMaterial item) {
        return item.name();
    }

    public void logError(Throwable e) {
        logger.error("An exception occurred with message: {}", e.getMessage());
    }

    @Nullable
    public static UUID getPlayerUuid(@Nonnull String name, boolean onlineMode, @Nullable Long timestamp) throws IOException {
        if (!onlineMode) return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
        try (BufferedReader reader = createReader("https://api.mojang.com/users/profiles/minecraft/" + name
                + (timestamp == null ? "" : "?at=" + timestamp))) {
            JsonElement element = readJson(reader);
            if (element == null) return null; //Игрок не найден
            JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject == null) return null;
            if (!jsonObject.has("id"))
                throw new IOException("Некорректные данные профиля игрока " + name);
            String uuid = jsonObject.get("id").getAsString();
            return parseUUID(uuid, false);
        } catch (IOException e) {
            throw new IOException("Сервера Mojang недоступны", e);
        }
    }

    @Nullable
    private static JsonElement readJson(@Nonnull BufferedReader reader) throws IOException {
        String jsonInput = reader.readLine();
        if (jsonInput == null || jsonInput.isEmpty()) return null;
        return new JsonParser().parse(jsonInput);
    }

    @NonNull
    private static BufferedReader createReader(@NonNull String url) throws IOException {
        return new BufferedReader(new InputStreamReader(new URL(url).openStream()));
    }

    @NonNull
    private static UUID parseUUID(@NonNull String input, boolean hasSplitters) {
        if (hasSplitters) {
            if (input.length() != 36) throw new IllegalArgumentException("Некорректный UUID: " + input);
        } else {
            if (input.length() != 32) throw new IllegalArgumentException("Некорректный UUID: " + input);
            input = input.replaceAll("(.{8})(.{4})(.{4})(.{4})(.+)", "$1-$2-$3-$4-$5");
        }
        return UUID.fromString(input);
    }

    public boolean hasPlayedBefore(String name) {
        boolean onlineMode = Bukkit.getOnlineMode();

        UUID uuid = null;

        try {
            uuid = Utils.getPlayerUuid(name, onlineMode, null);
        } catch (IOException e) {
            Utils.logError(e);
        }

        return Bukkit.getServer().getOfflinePlayer(uuid).hasPlayedBefore();
    }
}
