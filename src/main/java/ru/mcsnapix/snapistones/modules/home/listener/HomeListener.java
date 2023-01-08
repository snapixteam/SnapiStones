package ru.mcsnapix.snapistones.modules.home.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.mcsnapix.snapistones.api.events.region.RegionRemoveEvent;
import ru.mcsnapix.snapistones.config.Settings;
import ru.mcsnapix.snapistones.modules.home.Home;

public class HomeListener implements Listener {
    private final Home home;

    public HomeListener(Home home) {
        this.home = home;
    }

    @EventHandler
    public void onRegionRemove(RegionRemoveEvent event) {
        home.removeHome(event.getRegion().getId());
    }
}
