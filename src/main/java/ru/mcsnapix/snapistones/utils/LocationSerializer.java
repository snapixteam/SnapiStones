package ru.mcsnapix.snapistones.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;


@UtilityClass
public class LocationSerializer {
    public String getSerializedLocation(Location loc) {
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getName() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }

    public Location getDeserializedLocation(String s) {
        String [] parts = s.split(";");
        if (parts.length == 0) {
            return null;
        }
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        World w = Bukkit.getServer().getWorld(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        return new Location(w, x, y, z, yaw, pitch);
    }
}
