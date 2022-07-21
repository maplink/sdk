package global.maplink.geocode.suggestions;

import global.maplink.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static global.maplink.geocode.common.SampleFiles.SUGGESTIONS_RESPONSE;
import static global.maplink.geocode.common.Type.ROAD;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class SuggestionsResponseTest {
    public static final String FIRST_ID = "b77c04eb-92b1-4383-aaf8-d1f40ff50f9a";
    private final JsonMapper mapper = JsonMapper.loadDefault();

    @Test
    public void mustBeCreatedByJson() {
        SuggestionsResponse response = mapper.fromJson(SUGGESTIONS_RESPONSE.load(), SuggestionsResponse.class);
        assertThat(response.getFound()).isEqualTo(317);
        assertThat(response.getResults()).hasSize(10);
        assertThat(response.getMostRelevant().getType()).isEqualTo(ROAD);
        assertThat(response.getMostRelevant().getId()).isEqualTo(FIRST_ID);
    }

    @Test
    public void mustReturnNullOnEmptyResultsForMostRelevant() {
        SuggestionsResponse emptyResult = new SuggestionsResponse(0, emptyList());
        assertThat(emptyResult.getMostRelevant()).isNull();
        assertThat(emptyResult.getResults()).isEmpty();
        assertThat(emptyResult.getFound()).isZero();
    }

    @Test
    public void mustReturnNullOnNullResultsForMostRelevant() {
        SuggestionsResponse emptyResult = new SuggestionsResponse(0, null);
        assertThat(emptyResult.getMostRelevant()).isNull();
        assertThat(emptyResult.getResults()).isNull();
        assertThat(emptyResult.getFound()).isZero();
    }
}
