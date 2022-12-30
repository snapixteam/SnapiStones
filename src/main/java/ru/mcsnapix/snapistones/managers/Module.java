package ru.mcsnapix.snapistones.managers;

import lombok.Getter;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.modules.customflags.CustomFlags;
import ru.mcsnapix.snapistones.modules.holograms.Hologram;
import ru.mcsnapix.snapistones.modules.menu.Menu;
import ru.mcsnapix.snapistones.modules.regionsui.RegionSUI;

@Getter
public class Module {
    private final SnapiStones plugin = SnapiStones.get();
    private Hologram holograms;
    private CustomFlags customFlags;
    private RegionSUI regionSUI;
    private Menu menu;

    public Module() {
        if (check("hologram") && plugin.isPluginEnable("DecentHolograms")) {
            holograms = new Hologram();
            holograms.loadModule(plugin);
        }
        if (check("customFlags")) {
            customFlags = new CustomFlags();
            customFlags.loadModule(plugin);
        }
        if (check("regionSUI")) {
            regionSUI = new RegionSUI();
            regionSUI.loadModule(plugin);
        }
        if (check("menu")) {
            menu = new Menu();
            menu.loadModule(plugin);
        }
    }

    public boolean check(String moduleName) {
        return plugin.getConfig().isSet("modules." + moduleName) && plugin.getConfig().getBoolean("modules." + moduleName);
    }

}
