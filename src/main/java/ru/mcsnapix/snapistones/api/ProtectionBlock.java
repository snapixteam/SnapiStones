package ru.mcsnapix.snapistones.api;


import lombok.Builder;
import lombok.Getter;
import ru.mcsnapix.snapistones.xseries.XMaterial;

@Builder
@Getter
public class ProtectionBlock {
    private final XMaterial item;
    private final String symbol;
    private final int radiusX;
    private final int radiusY;
    private final int radiusZ;
}
