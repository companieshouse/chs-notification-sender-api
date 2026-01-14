package helpers.utils;


import static org.junit.Assert.assertArrayEquals;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.OutputCapture;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import uk.gov.companieshouse.logging.EventType;

public class OutputAssertions {

    /**
     * Helper to extract the “data” object from a captured log entry.
     *
     * @param capture the OutputCapture instance containing the logs
     * @param event   the log level/event name (“debug”, “info”, “error”, etc.)
     * @return the “data” JsonNode for that entry
     * @throws AssertionError if the entry or its “data” field is missing
     */
    public static JsonNode getDataFromLogMessage(OutputCapture capture, EventType event,
            String message) {
        var eventName = event.getName();
        var entries = capture.getJsonEntries();
        if (entries.isEmpty()) {
            throw new AssertionError("No log entries found for event: " + eventName);
        }
        var matchingEventEntries = entries.stream()
                .filter(e -> e.has("event") && eventName.equals(e.get("event").asText()))
                .filter(e -> e.has("data") && e.get("data").has("message"))
                .map(e -> e.get("data"))
                .toList();

        if (matchingEventEntries.isEmpty()) {
            throw new AssertionError(
                    "No log entries found for event: '" + eventName + "' with a 'data.message' field.");
        }

        return matchingEventEntries.stream()
                .filter(e -> message.equals(e.get("message").asText()))
                .findFirst()
                .orElseThrow(() -> {
                    String actualMessages = matchingEventEntries.stream()
                            .map(e -> e.get("message").asText())
                            .distinct()
                            .reduce((a, b) -> a + "\n" + b)
                            .orElse("[no messages found]");
                    return new AssertionError(
                            "No log entry found with message:\n  Expected: " + message
                                    + "\n  Found:    " + actualMessages);
                });
    }

    /**
     * Helper to get the log entry holding the message sought.
     *
     * @param capture the OutputCapture instance containing the logs
     * @param event   the log level/event name (“debug”, “info”, “error”, etc.)
     * @param message the log message sought
     * @return the “data” JsonNode for that entry
     * @throws AssertionError if no such entry can be found
     */
    public static JsonNode getLogMessage(OutputCapture capture,
                                         EventType event,
                                         String message) {
        var eventName = event.getName();
        var entries = capture.getJsonEntries();
        if (entries.isEmpty()) {
            throw new AssertionError("No log entries found for event: " + eventName);
        }
        var messageNodesSought =  entries.stream()
                .filter(e -> e.has("event") && eventName.equals(e.get("event").asText()))
                .filter(e -> e.has("data")
                        && e.get("data").has("message")
                        && e.get("data").get("message").asText().contains(message))
                .findFirst();
        if (messageNodesSought.isEmpty()) {
            throw new AssertionError("No message '" + message + "' found.");
        }
        return messageNodesSought.get();
    }

    /**
     * Helper to assert that a JSON node has a specific field and that the field's value matches the
     * expected value.
     *
     * @param jsonNode      the JsonNode to check
     * @param fieldName     the name of the field to check
     * @param expectedValue the expected value of the field
     */
    public static void assertJsonHasAndEquals(JsonNode jsonNode, String fieldName,
            String expectedValue) {
        Assertions.assertNotNull(jsonNode,
                "JSON node for field '" + fieldName + "' should not be null");
        Assertions.assertTrue(jsonNode.has(fieldName), "JSON should contain field: " + fieldName);
        Assertions.assertEquals(expectedValue, jsonNode.get(fieldName).asText(),
                "Field '" + fieldName + "' should match expected");
    }

    /**
     * Helper to assert that a JSON node has a specific array field and that each element matches
     * the expected values (in order).
     *
     * @param jsonNode       the JsonNode to check
     * @param fieldName      the name of the array field to check
     * @param expectedValues the expected values of the array elements
     */
    public static void assertJsonHasAndEquals(JsonNode jsonNode, String fieldName,
            String[] expectedValues) {
        Assertions.assertNotNull(jsonNode,
                "JSON node for field '" + fieldName + "' should not be null");
        Assertions.assertTrue(jsonNode.has(fieldName),
                "JSON should contain field: " + fieldName);

        JsonNode arrayNode = jsonNode.get(fieldName);
        Assertions.assertTrue(arrayNode.isArray(),
                "Field '" + fieldName + "' should be an array");

        Assertions.assertEquals(expectedValues.length, arrayNode.size(),
                "Array '" + fieldName + "' length should match expected");

        var actualValues = new String[arrayNode.size()];
        for (int i = 0; i < expectedValues.length; i++) {
            actualValues[i] = arrayNode.get(i).asText();
        }

        Arrays.sort(actualValues);
        Arrays.sort(expectedValues);

        assertArrayEquals(expectedValues, actualValues);
    }
}
