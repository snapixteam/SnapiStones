package ru.mcsnapix.snapistones.mysql;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.enums.Column;
import ru.mcsnapix.snapistones.utils.serializer.LocationSerializer;
import ru.mcsnapix.snapistones.utils.serializer.MemberSerializer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@UtilityClass
public class RegionDatabaseManager {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private final SnapiStones plugin = SnapiStones.get();

    @SneakyThrows
    public void createRegion(String region, String owner) {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        plugin.getMySQL()
                .execute("INSERT INTO regions (region_name, owners_name, creation_date) VALUES (?,?,?)",
                        region, owner, timestamp);
    }

    @SneakyThrows
    public void removeRegion(String region) {
        plugin.getMySQL()
                .execute("DELETE FROM regions WHERE `region_name` = ?", region);
    }

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
    public boolean hasRegionData(String region) {
        boolean result;

        try (PreparedStatement statement = plugin.getMySQL().prepareStatement("SELECT * FROM regions WHERE `region_name` = ?", region)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                result = resultSet.next();
            }
        }

        return result;
    }

    @SneakyThrows
    private void updateRegionData(String region, Column column, String value) {
        plugin.getMySQL().execute("UPDATE regions SET "+column.getColumns()+" = ? WHERE region_name = ?", value, region);
    }

    public List<String> getMembers(String region, Column column) {
        String sMember = getRegionData(region, column);
        if (sMember == null) {
            return null;
        }
        return MemberSerializer.deserialized(sMember);
    }

    public boolean hasMembers(String region, Column column) {
        String sMember = getRegionData(region, column);
        if (sMember == null) {
            return false;
        }
        return true;
    }

    public void addMembers(String region, String name, Column column) {
        List<String> existingMembers = getMembers(region, column);
        List<String> members;

        if (existingMembers == null) {
            members = new ArrayList<>();
        } else {
            members = new ArrayList<>(existingMembers);
        }

        members.add(name);
        String sMember = MemberSerializer.serialized(members);
        updateRegionData(region, column, sMember);
    }

    public void removeMembers(String region, String name, Column column) {
        List<String> members = getMembers(region, column);
        if (members != null) {
            members.remove(name);
        }

        String sMember = MemberSerializer.serialized(members);
        updateRegionData(region, column, sMember);
    }

    public Location getLocation(String region) {
        String sLocation = getRegionData(region, Column.LOCATION);
        if (sLocation == null) {
            return null;
        }
        return LocationSerializer.getDeserializedLocation(sLocation);
    }

    public String hasLocation(String region) {
        String sLocation = getRegionData(region, Column.LOCATION);
        if (sLocation == null) {
            return "§cне установлена";
        }
        return "§aустановлена";
    }

    public void setLocation(String region, Location location) {
        String dLocation = LocationSerializer.getSerializedLocation(location);
        updateRegionData(region, Column.LOCATION, dLocation);
    }

    @SneakyThrows
    public String getCreationRegionFormattedDate(String region) {
        try (PreparedStatement statement = plugin.getMySQL().prepareStatement("SELECT * FROM regions WHERE `region_name` = ?", region)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Timestamp time = resultSet.getTimestamp("creation_date");
                    return DATE_FORMAT.format(time);
                }
            }
        }
        return "Нет информации";
    }
}