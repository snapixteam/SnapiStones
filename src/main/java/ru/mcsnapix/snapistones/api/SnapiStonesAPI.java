package ru.mcsnapix.snapistones.api;

import lombok.experimental.UtilityClass;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.xseries.XMaterial;

@UtilityClass
public class SnapiStonesAPI {
    private final SnapiStones plugin = SnapiStones.get();

    public boolean isProtectedBlock(XMaterial item) {
        return plugin.getProtection().isProtectedBlock(item);
    }
}
