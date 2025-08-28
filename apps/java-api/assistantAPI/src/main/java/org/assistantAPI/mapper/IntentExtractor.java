package org.assistantAPI.mapper;

import lombok.experimental.UtilityClass;
import org.assistantAPI.dto.IntentDTO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class IntentExtractor {

    private static final Pattern PATTERN =
            Pattern.compile("(?i)(iphone)\\s*(\\d+)\\D+(\\d+)\\s*гб");

    public static IntentDTO map(String intent) {
        Matcher m = PATTERN.matcher(intent);
        if (m.find()) {
            String title = m.group(1).toLowerCase();
            String model = m.group(2);
            int storage = Integer.parseInt(m.group(3));
            return new IntentDTO(title, model, storage);
        }
        return null;
    }

}
