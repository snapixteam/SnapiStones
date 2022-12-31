package ru.mcsnapix.snapistones.modules.customflags.utils;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.ExitFlag;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import ru.mcsnapix.snapistones.modules.customflags.flags.FarewellFlagHandler;
import ru.mcsnapix.snapistones.modules.customflags.flags.GreetingFlagHandler;

import java.util.List;

@UtilityClass
public class FlagUtil {
    public final Flag<String> GREET_ACTION = new StringFlag("greeting-action");
    public final Flag<String> FAREWELL_ACTION = new StringFlag("farewell-action");
    private final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

    public void register() {
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(GreetingFlagHandler.FACTORY, ExitFlag.FACTORY);
        sessionManager.registerHandler(FarewellFlagHandler.FACTORY, ExitFlag.FACTORY);
    }

    public static void initFlag(ProtectedRegion region, Player player, List<String> flag) {
        for (String s : flag) {
            String[] ss = s.split(" ");
            String sFlag = ss[0];

            StringBuilder values = new StringBuilder();
            for (String arg : ss) {
                values.append(arg).append(" ");
            }
            String value = values.toString().replace(sFlag + " ", "");

            if (sFlag.equals("greeting-action") || sFlag.equals("farewell-action")) {
                setRegionFlag(region, sFlag, value, player);
                continue;
            }
            Flag<?> existing = registry.get(sFlag);
            StateFlag stateFlag = (StateFlag) existing;
            if (value.equalsIgnoreCase("allow")) {
                region.setFlag(stateFlag, StateFlag.State.ALLOW);
            } else {
                region.setFlag(stateFlag, StateFlag.State.DENY);
            }
        }
    }

    private void setRegionFlag(ProtectedRegion region, String sFlag, String value, Player player) {
        if (sFlag.equals("greeting-action")) {
            region.setFlag(GREET_ACTION, value.replace("%player%", player.getDisplayName()));
        }
        if (sFlag.equals("farewell-action")) {
            region.setFlag(FAREWELL_ACTION, value.replace("%player%", player.getDisplayName()));
        }
    }
}
