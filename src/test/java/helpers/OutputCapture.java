package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.output.TeeOutputStream;
import uk.gov.companieshouse.logging.EventType;

public class OutputCapture implements AutoCloseable {

    private final PrintStream originalOut;
    private final ByteArrayOutputStream outBuffer;
    private final TeeOutputStream teeOut;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final PrintStream capturedStream;

    public OutputCapture() {
        this.originalOut = System.out;
        this.outBuffer = new ByteArrayOutputStream();
        this.teeOut = new TeeOutputStream(originalOut, outBuffer);
        this.capturedStream = new PrintStream(teeOut, true, StandardCharsets.UTF_8);
        System.setOut(this.capturedStream);
        System.out.flush();
    }

    /**
     * This should give a String representation of everything that has been sent to standard out
     * since this class has been initialised or since reset() was called
     *
     * @return A String of everything captured by this class
     */
    public synchronized String getOutput() {
        return outBuffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * Return JSON lines in output as a JsonNode. <br>
     * <b>Note:</b> Most lines in the output will likely not be JSON this will skip over the ones
     * that are not
     *
     * @return List<JsonNode> containing the data within the JSON output lines
     */
    public synchronized List<JsonNode> getJsonEntries() {
        return getOutput().lines()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return MAPPER.readTree(s);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Find the entry where "event" == eventName and return the entire entry at the given index.
     *
     * @param eventName The name of the event to search for
     * @param index     The index of the event to find (0-based)
     * @return Optional containing the JsonNode if found, otherwise empty
     */
    public synchronized Optional<JsonNode> findEntryByEvent(EventType eventName, int index) {
        return getJsonEntries().stream()
                .filter(
                        node -> node.has("event")
                                && eventName.getName().equals(node.get("event").asText())
                )
                .skip(index)
                .findFirst();
    }

    /**
     * Retrieve the "data" field from the entry where "event" == eventName at the specified index.
     *
     * @param eventName The name of the event to search for
     * @param index     The index of the event to find (0-based)
     * @return Optional containing the "data" field if found, otherwise empty
     */
    public Optional<JsonNode> findDataByEvent(EventType eventName, int index) {
        return findEntryByEvent(eventName, index).map(node -> node.get("data"));
    }

    public long findAmountByEvent(EventType eventName) {
        return getJsonEntries().stream()
                .filter(
                        node -> node.has("event")
                                && eventName.getName().equals(node.get("event").asText())
                )
                .count();
    }

    /**
     * Clears stdout buffer, allowing for fresh captures in subsequent tests.
     */
    public synchronized void reset() {
        outBuffer.reset();
    }

    @Override
    public void close() throws IOException {
        try {
            teeOut.flush();
            capturedStream.flush();
        } finally {
            System.setOut(originalOut);
        }
    }
}
