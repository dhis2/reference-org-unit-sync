//DEPS org.hisp.dhis.integration.sdk:dhis2-java-sdk:3.0.1

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.MaskingFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.camel.support.processor.DefaultMaskingFormatter;
import org.hisp.dhis.integration.sdk.api.RemoteDhis2ClientException;

public class DeadLetterQueueProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger( DeadLetterQueueProcessor.class );
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final MaskingFormatter MASKING_FORMATTER = new DefaultMaskingFormatter();

    @Override
    public void process(Exchange exchange) throws Exception {
        Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        final Map<String, Object> newBody;
        if (exception != null) {
            while (exception.getCause() != null) {
                exception = exception.getCause();
            }
            final String message;
            if (exception instanceof RemoteDhis2ClientException) {
                message = MASKING_FORMATTER.format(exception.toString());
            } else {
                message = MASKING_FORMATTER.format(exception.getMessage());
            }
            LOGGER.error(message, exception);
            newBody = Map.of("errorMessage", message, "body", exchange.getMessage().getBody());
        } else {
            newBody = Map.of("errorMessage", "See log file", "body", exchange.getMessage().getBody());
        }
        exchange.getMessage().setBody(OBJECT_MAPPER.writeValueAsString(newBody));
    }
}
