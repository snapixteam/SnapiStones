package ru.mcsnapix.snapistones.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.xseries.XMaterial;

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
}
