package ru.mcsnapix.snapistones.modules.home;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.interfaces.IModule;
import ru.mcsnapix.snapistones.mysql.MySQL;
import ru.mcsnapix.snapistones.utils.LocationSerializer;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

@Getter
public class Home implements IModule {
    private Settings settings;
    private final MySQL mySQL = SnapiStones.get().getMySQL();

    @Override
    public void loadModule(SnapiStones main) {
        settings = new Settings("modules/home", false);
        Bukkit.getLogger().info("§fМодуль §aHome §fзагружен");
    }

    public Location getHomeLoc(String region) {
        String location = "";
        try {
            CachedRowSet crs = mySQL.getCRS("SELECT * FROM snapistones_homes WHERE `Region` = ?", region);
            while (crs.next()) {
                location = crs.getString(2);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return LocationSerializer.getDeserializedLocation(location);
    }

    public void addHome(String region, Location location) {
        try {
            mySQL.execute("DELETE FROM snapistones_homes WHERE `region` = ?", region);
            mySQL.execute("INSERT INTO snapistones_homes VALUES (?, ?)", region, LocationSerializer.getSerializedLocation(location));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}