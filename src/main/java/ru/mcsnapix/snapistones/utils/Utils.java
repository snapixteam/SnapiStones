package ru.mcsnapix.snapistones.utils;

import lombok.experimental.UtilityClass;
import ru.mcsnapix.snapistones.xseries.XMaterial;

@UtilityClass
public class Utils {
    public String getItemStackName(XMaterial item) {
        return item.name();
    }
}
