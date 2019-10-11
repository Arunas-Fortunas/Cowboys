package provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Cowboy;

import java.io.IOException;

public class CowboysProvider {
    String cowboysJson = "[{\n" +
            "  \"name\": \"John\",\n" +
            "  \"health\": 10,\n" +
            "  \"damage\": 1\n" +
            "},\n" +
            "{\n" +
            "\"name\": \"Bill\",\n" +
            "\"health\": 8,\n" +
            "\"damage\": 2\n" +
            "},\n" +
            "{\n" +
            "\"name\": \"Sam\",\n" +
            "\"health\": 10,\n" +
            "\"damage\": 1\n" +
            "},\n" +
            "{\n" +
            "\"name\": \"Peter\",\n" +
            "\"health\": 5,\n" +
            "\"damage\": 3\n" +
            "},\n" +
            "{\n" +
            "\"name\": \"Philip\",\n" +
            "\"health\": 15,\n" +
            "\"damage\": 1\n" +
            "}]";

    public final Cowboy[] getCowboys() throws IOException {
        return new ObjectMapper().readValue(cowboysJson, Cowboy[].class);
    }
}
