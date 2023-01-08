package ru.mcsnapix.snapistones.api;

import lombok.experimental.UtilityClass;
import ru.mcsnapix.snapistones.SnapiStones;
import ru.mcsnapix.snapistones.managers.Protection;
import ru.mcsnapix.snapistones.xseries.XMaterial;

@UtilityClass
public class SnapiStonesAPI {
    private final Protection protection = SnapiStones.get().getProtection();
    private final SnapiStones plugin = SnapiStones.get();

    public boolean isProtectedBlock(XMaterial item) {
        return plugin.getProtection().isProtectedBlock(item);
    }

    public static Protection getProtection() {
        return protection;
    }
}
