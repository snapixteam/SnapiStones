package ru.mcsnapix.snapistones.utils.serializer;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class MemberSerializer {
    public String serialized(List<String> list) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i));
            if (i != list.size() - 1) {
                builder.append(";");
            }
        }

        return builder.toString();
    }

    public List<String> deserialized(String s) {
        String[] parts = s.split(";");
        return Arrays.asList(parts);
    }
}
