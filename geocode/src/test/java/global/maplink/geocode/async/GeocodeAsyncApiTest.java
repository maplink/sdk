package global.maplink.geocode.async;

import global.maplink.MapLinkSDK;
import global.maplink.credentials.InvalidCredentialsException;
import global.maplink.credentials.MapLinkCredentials;
import global.maplink.geocode.geocode.GeocodeRequest;
import global.maplink.geocode.reverse.ReverseRequest.Entry;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static global.maplink.env.EnvironmentCatalog.HOMOLOG;
import static global.maplink.geocode.common.Defaults.DEFAULT_CLIENT_ID;
import static global.maplink.geocode.common.Defaults.DEFAULT_SECRET;
import static global.maplink.geocode.common.Type.ZIPCODE;
import static global.maplink.geocode.geocode.GeocodeRequest.multi;
import static global.maplink.geocode.reverse.ReverseRequest.entry;
import static global.maplink.geocode.utils.EnvCredentialsHelper.withEnvCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class GeocodeAsyncApiTest {

    @AfterEach
    void cleanupSDK() {
        MapLinkSDK.resetConfiguration();
    }

    @Test
    void mustBeInstantiableWithGetInstance() {
        configureWith(MapLinkCredentials.ofKey(DEFAULT_CLIENT_ID, DEFAULT_SECRET));
        val instance = GeocodeAsyncAPI.getInstance();
        assertThat(instance).isNotNull();
    }

    @Test
    void mustFailWithInvalidCredentials() {
        configureWith(MapLinkCredentials.ofKey(DEFAULT_CLIENT_ID, DEFAULT_SECRET));
        val instance = GeocodeAsyncAPI.getInstance();
        assertThatThrownBy(() -> instance.suggestions("Rua Afonso Celso").get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void mustReturnAutocompleteWithTypeQueryCorrectly() {
        withEnvCredentials(credentials -> {
            configureWith(credentials);
            val instance = GeocodeAsyncAPI.getInstance();
            val result = instance.suggestions("São Paulo", ZIPCODE).get();
            assertThat(result.getResults()).isNotEmpty();
            assertThat(result.getFound()).isNotZero();
            assertThat(result.getResults()).allMatch(v -> v.getType() == ZIPCODE);
        });
    }

    @Test
    void mustReturnAutocompleteWithoutTypeQueryCorrectly() {
        withEnvCredentials(credentials -> {
            configureWith(credentials);
            val instance = GeocodeAsyncAPI.getInstance();
            val result = instance.suggestions("Rua Afonso Celso").get();
            assertThat(result.getResults()).isNotEmpty();
            assertThat(result.getFound()).isNotZero();
        });
    }

    @Test
    void mustReturnSuggestionsOnSingleGeocode() {
        withEnvCredentials(credentials -> {
            configureWith(credentials);
            val instance = GeocodeAsyncAPI.getInstance();
            val result = instance.geocode(
                    GeocodeRequest.ofCity("sp", "São Paulo", "SP")
            ).get();
            assertThat(result.getResults()).hasSizeGreaterThan(1);
            assertThat(result.getFound()).isGreaterThan(1);
            assertThat(result.getById("sp")).isNotEmpty();
        });
    }

    @Test
    void mustReturnOneResultByRequestInMultiGeocode() {
        withEnvCredentials(credentials -> {
            configureWith(credentials);
            val instance = GeocodeAsyncAPI.getInstance();
            val result = instance.geocode(multi(
                    GeocodeRequest.ofCity("sp", "São Paulo", "SP"),
                    GeocodeRequest.ofCity("pr", "Paraná", "PR"),
                    GeocodeRequest.of("addr")
                            .state("SP")
                            .city("São Paulo")
                            .district("Jardim Paulista")
                            .road("Alameda Campinas")
                            .number(579)
                            .build()

            )).get();
            assertThat(result.getResults()).hasSize(3);
            assertThat(result.getFound()).isEqualTo(3);
            assertThat(result.getById("sp")).isNotEmpty();
            assertThat(result.getById("pr")).isNotEmpty();
            assertThat(result.getById("addr")).isNotEmpty();
        });
    }


    @Test
    void mustReturnOneResultByRequestInReverse() {
        withEnvCredentials(credentials -> {
            configureWith(credentials);
            val instance = GeocodeAsyncAPI.getInstance();
            val result = instance.reverse(
                    entry("sp", -23.6818334, -46.8823662),
                    entry("pr", -25.494945, -49.3598374, 500),
                    Entry.builder()
                            .id("addr")
                            .lat(-23.5666682)
                            .lon(-46.6558011)
                            .build()

            ).get();
            assertThat(result.getResults()).hasSize(3);
            assertThat(result.getFound()).isEqualTo(3);
            assertThat(result.getById("sp")).isNotEmpty();
            assertThat(result.getById("pr")).isNotEmpty();
            assertThat(result.getById("addr")).isNotEmpty();
        });
    }

    private void configureWith(MapLinkCredentials credentials) {
        MapLinkSDK.resetConfiguration();
        MapLinkSDK.configure()
                .with(HOMOLOG)
                .with(credentials)
                .initialize();
    }
}
