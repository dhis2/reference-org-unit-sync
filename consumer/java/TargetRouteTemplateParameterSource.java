import org.apache.camel.BeanInject;
import org.apache.camel.BindToRegistry;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteTemplateParameterSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides the parameters for creating a subscriber route per target in the `metadataSubscriberRouteTemplate` route
 * template.
 */
@BindToRegistry
public class TargetRouteTemplateParameterSource implements RouteTemplateParameterSource {

    public static final String ADMIN_DHIS2_USER_ID = "M5zQapPyTZI";
    public static final String DHIS2_DIRECT_ENDPOINT_NAME = "dhis2Target";
    public static final String IMPORT_UID_SCHEME = "uid";
    public static final String ALLOWED_OPERATIONS = "c,u,d";

    private static final Pattern TARGET_PROPERTY_PATTERN = Pattern.compile("^target\\.([\\d]+)\\.(.+)");
    private static final int PROPERTY_INDEX = 1;
    private static final int PROPERTY_VALUE = 2;

    @BeanInject
    private CamelContext camelContext;

    private Map<String, Map<String, String>> targets;

    @Override
    public Map<String, Object> parameters(String routeId) {
        String index = routeId.substring(routeId.length() - 1);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(TEMPLATE_ID, "metadataSubscriberRouteTemplate");
        parameters.put("clientId", targets.get(index).get("dhis2ApiUrl"));
        parameters.putAll(targets.get(index));

        return parameters;
    }

    @Override
    public Set<String> routeIds() {
        Set<String> routeIds = new HashSet<>();
        Map<String, Object> properties = camelContext.getPropertiesComponent().loadPropertiesAsMap();
        targets = new HashMap<>();
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            Matcher matcher = TARGET_PROPERTY_PATTERN.matcher(property.getKey());
            if (matcher.matches()) {
                String index = matcher.group(PROPERTY_INDEX);
                Map<String, String> target = targets.getOrDefault(index, new HashMap<>());
                target.put(matcher.group(PROPERTY_VALUE), (String) property.getValue());
                target.putIfAbsent("index", index);
                targets.put(index, target);
            }
        }

        for (Map.Entry<String, Map<String, String>> target : targets.entrySet()) {
            routeIds.add("metadataSubscriberRoute[" + target.getValue().get("dhis2ApiUrl") + "]-" + target.getKey());
            target.getValue().putIfAbsent("idScheme", IMPORT_UID_SCHEME);
            target.getValue().putIfAbsent("camelDirectEndpointName", DHIS2_DIRECT_ENDPOINT_NAME);
            target.getValue().putIfAbsent("fieldsRequireApproval", "");
            target.getValue().putIfAbsent("messageConversationUserId", ADMIN_DHIS2_USER_ID);
            target.getValue().putIfAbsent("allowedOperations", ALLOWED_OPERATIONS);
        }

        return routeIds;
    }
}