package global.maplink.emission.testUtils;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@RequiredArgsConstructor
public enum SampleFiles {

    EMISSION_REQUEST("emission/json/emissionRequest.json"),
    EMISSION_REQUEST_WITH_NULL_GET_FUEL_TYPE("emission/json/emissionRequestWithNullGetFuelType.json"),
    EMISSION_REQUEST_WITH_NULL_GET_TOTAL_DISTANCE("emission/json/emissionRequestWithNullGetTotalDistance.json"),
    EMISSION_REQUEST_WITH_NULL_GET_AUTONOMY("emission/json/emissionRequestWithNullGetAutonomy.json"),
    EMISSION_RESPONSE("emission/json/emissionResponse.json");

    private final String filePath;

    public byte[] load() {
        URL resource = SampleFiles.class.getClassLoader().getResource(filePath);
        try {
            assert resource != null;
            return Files.readAllBytes(Paths.get(resource.toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
