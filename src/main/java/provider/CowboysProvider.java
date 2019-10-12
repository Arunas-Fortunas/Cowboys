package provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Cowboy;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

public class CowboysProvider {
    public final Cowboy[] getCowboys() throws IOException {
        return new ObjectMapper().readValue(ResourceUtils.getFile("classpath:Cowboys.json"), Cowboy[].class);
    }
}
