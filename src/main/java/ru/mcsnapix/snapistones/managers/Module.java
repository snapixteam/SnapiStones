package ru.mcsnapix.snapistones.managers;

import lombok.Getter;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.modules.customflags.CustomFlagsModule;
import ru.mcsnapix.snapistones.modules.holograms.HologramModule;
import ru.mcsnapix.snapistones.modules.home.HomeModule;
import ru.mcsnapix.snapistones.modules.menu.MenuModule;
import ru.mcsnapix.snapistones.modules.upgrade.UpgradeModule;

@Getter
public class Module {
    private final SnapiStones plugin = SnapiStones.get();
    private HologramModule holograms;
    private HomeModule home;
    private CustomFlagsModule customFlags;
    private MenuModule menu;
    private UpgradeModule upgrade;

    public Module() {
        if (check("hologram") && plugin.isPluginEnable("DecentHolograms")) {
            holograms = new HologramModule();
            holograms.loadModule(plugin);
        }
        if (check("home")) {
            home = new HomeModule();
            home.loadModule(plugin);
        }
        if (check("customFlags")) {
            customFlags = new CustomFlagsModule();
            customFlags.loadModule(plugin);
        }
        if (check("menu")) {
            menu = new MenuModule();
            menu.loadModule(plugin);
        }

        upgrade = new UpgradeModule();
        upgrade.loadModule(plugin);
    }

    public boolean check(String moduleName) {
        return plugin.getConfig().isSet("modules." + moduleName) && plugin.getConfig().getBoolean("modules." + moduleName);
    }
}
