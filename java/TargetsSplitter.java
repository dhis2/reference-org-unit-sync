import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;

public class TargetsSplitter {

    private static final Pattern TARGET_PROPERTY_PATTERN = Pattern.compile("^target\\.([\\d]+)\\.(.+)");
    private static final int PROPERTY_INDEX = 1;
    private static final int PROPERTY_VALUE = 2;

    public List<Map<String, String>> split(Exchange exchange) {
        Map<Integer, Map<String, String>> targetsByIndex = new HashMap<>();

        Map<String, Object> properties = exchange.getContext().getPropertiesComponent().loadPropertiesAsMap();
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            Matcher matcher = TARGET_PROPERTY_PATTERN.matcher(property.getKey());
            if (matcher.matches()) {
                int index = Integer.parseInt(matcher.group(PROPERTY_INDEX));
                Map<String, String> target = targetsByIndex.getOrDefault(index, new HashMap<>());
                target.put(matcher.group(PROPERTY_VALUE), (String) property.getValue());
                target.putIfAbsent("index", String.valueOf(index));
                targetsByIndex.put(index, target);
            }
        }

        List<Map<String, String>> targets = new ArrayList<>(targetsByIndex.values());
        for (Map<String, String> target : targets) {
            target.putIfAbsent("idScheme", "uid");
            target.putIfAbsent("endpointUri", "direct:dhis2Target");
            target.putIfAbsent("fieldsRequireApproval", "");
        }

        return targets;
    }
}