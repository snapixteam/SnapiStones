package ru.mcsnapix.snapistones.modules.upgrade.managers;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.enums.Column;
import ru.mcsnapix.snapistones.utils.serializer.EffectSerializer;

import javax.sql.rowset.CachedRowSet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@UtilityClass
public class UpgradeDatabaseManager {
    private final SnapiStones plugin = SnapiStones.get();
    private final Settings settings = plugin.getModuleManager().getUpgrade().getSettings();

    @SneakyThrows
    public String getRegionData(String region, Column column) {
        String result = null;

        try (PreparedStatement statement = plugin.getMySQL().prepareStatement("SELECT * FROM regions WHERE `region_name` = ?", region)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = resultSet.getString(column.getColumns());
                }
            }
        }

        return result;
    }

    @SneakyThrows
    private void updateRegionData(String region, Column column, String value) {
        plugin.getMySQL().execute("UPDATE regions SET "+column.getColumns()+" = ? WHERE region_name = ?", value, region);
    }

    public List<String> getBoughtEffects(String region) {
        String effect = getRegionData(region, Column.EFFECTS);
        if (effect == null) {
            return null;
        }
        return EffectSerializer.deserialized(effect);
    }

    public void addBoughtEffect(String region, String name) {
        List<String> effects = getBoughtEffects(region);
        effects.add(name);
        String sEffects = EffectSerializer.serialized(effects);
        updateRegionData(region, Column.EFFECTS, sEffects);
    }

    public List<String> getActiveEffects(String region) {
        String effect = getRegionData(region, Column.ACTIVE_EFFECTS);
        if (effect == null) {
            return null;
        }
        return EffectSerializer.deserialized(effect);
    }

    public void addActiveEffect(String region, String name) {
        List<String> effects = getBoughtEffects(region);
        effects.add(name);
        String sEffects = EffectSerializer.serialized(effects);
        updateRegionData(region, Column.ACTIVE_EFFECTS, sEffects);
    }

    public boolean hasActiveEffect(String region) {
        List<String> activeEffect = getActiveEffects(region);
        return activeEffect != null;
    }

    public int getMaxMembers(String region, Column column) {
        String level = getRegionData(region, column);

        ConfigurationSection limit = settings.getConfig().getConfigurationSection("Max-Limit");
        ConfigurationSection members;

        if (column.getColumn() == 9) {
            members = limit.getConfigurationSection("Owners");
        } else {
            members = limit.getConfigurationSection("Members");
        }

        int amount = members.getConfigurationSection("1").getInt("Amount");

        for (String s : members.getKeys(false)) {
            if (s.equals(level)) {
                amount = members.getConfigurationSection(level).getInt("Amount");
            }
        }

        return amount;
    }

    public void addLevelMaxMembers(String region, Column column, int level) {
        level++;
        updateRegionData(region, column, Integer.toString(level));
    }
}
