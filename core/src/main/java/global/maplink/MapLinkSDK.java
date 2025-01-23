package global.maplink;

import global.maplink.credentials.MapLinkCredentials;
import global.maplink.domain.PointsMode;
import global.maplink.env.Environment;
import global.maplink.extensions.SdkExtension;
import global.maplink.extensions.SdkExtensionCatalog;
import global.maplink.http.HttpAsyncEngine;
import global.maplink.http.UserAgent;
import global.maplink.json.JsonMapper;
import global.maplink.token.TokenProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.joining;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Slf4j
public class MapLinkSDK {
    private static MapLinkSDK INSTANCE = null;

    private final MapLinkCredentials credentials;

    private final Environment environment;

    private final HttpAsyncEngine http;

    private final UserAgent userAgent;

    private final JsonMapper jsonMapper;

    private final TokenProvider tokenProvider;

    private final PointsMode pointsMode;

    private final Collection<SdkExtension> extensions;

    private void postConstruct() {
        extensions.forEach(e -> e.initialize(this));
        http.initialize(this);
        log.info(
                "Initialized MapLink SDK with [Environment: {}] [HttpEngine: {}] [JsonMapper: {}] [Extensions: {}]",
                environment,
                http.getClass().getName(),
                jsonMapper.getClass().getName(),
                extensions.stream().map(SdkExtension::getName).collect(joining(", "))
        );
    }

    public static Configurator configure() {
        return new Configurator();
    }

    public static MapLinkSDK getInstance() {
        if (INSTANCE == null) {
            throw new MapLinkNotConfiguredException();
        }
        return INSTANCE;
    }

    public static void resetConfiguration() {
        INSTANCE = null;
    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class Configurator {

        private Optional<MapLinkCredentials> credentials = Optional.empty();

        private Optional<Environment> environment = Optional.empty();

        private Optional<HttpAsyncEngine> engine = Optional.empty();

        private Optional<UserAgent> agent = Optional.empty();

        private Optional<JsonMapper> mapper = Optional.empty();

        private Optional<PointsMode> pointsMode = Optional.empty();

        private final Collection<SdkExtension> extensions = new HashSet<>();

        public Configurator with(MapLinkCredentials credentials) {
            this.credentials = Optional.of(credentials);
            return this;
        }

        public Configurator with(HttpAsyncEngine engine) {
            this.engine = Optional.of(engine);
            return this;
        }


        public Configurator with(UserAgent agent) {
            this.agent = Optional.of(agent);
            return this;
        }

        public Configurator with(Environment environment) {
            this.environment = Optional.of(environment);
            return this;
        }

        public Configurator with(JsonMapper mapper) {
            this.mapper = Optional.of(mapper);
            return this;
        }

        public Configurator with(SdkExtension extension) {
            this.extensions.add(extension);
            return this;
        }

        public Configurator with(SdkExtensionCatalog catalog) {
            this.extensions.addAll(catalog.getAll());
            return this;
        }

        public Configurator with(PointsMode pointsMode) {
            this.pointsMode = Optional.of(pointsMode);
            return this;
        }

        public void initialize() {
            if (INSTANCE != null)
                throw new IllegalStateException("MapLinkSDK already has been configured");
            val http = engine.orElseGet(HttpAsyncEngine::loadDefault);
            val jsonMapper = mapper.orElseGet(JsonMapper::loadDefault);
            val env = environment.orElseGet(Environment::loadDefault);
            INSTANCE = new MapLinkSDK(
                    credentials.orElseGet(MapLinkCredentials::loadDefault),
                    environment.orElseGet(Environment::loadDefault),
                    http,
                    agent.orElseGet(UserAgent::loadDefault),
                    jsonMapper,
                    TokenProvider.create(http, env, jsonMapper, true),
                    pointsMode.orElseGet(PointsMode::loadDefault),
                    unmodifiableCollection(extensions)
            );
            INSTANCE.postConstruct();
        }
    }
}
