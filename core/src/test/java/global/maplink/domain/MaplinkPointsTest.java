package global.maplink.domain;

import lombok.SneakyThrows;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaplinkPointsTest {

    private static final String SAMPLE_POLYLINE = "pyynCfaw{GiCeC{DwD??iSnW@B";
    private static final List<String> SAMPLE_GEOHASHES = Arrays.asList(
            "6gycfmgep",
            "6gycfmupr",
            "6gycfqhus",
            "6gycfqhus",
            "6gycfqdp9",
            "6gycfqdp8"
    );
    private static final double[][] SAMPLE_POINTS = new double[][]{
            new double[]{
                    -23.56649,
                    -46.6538
            },
            new double[]{
                    -23.5658,
                    -46.65313
            },
            new double[]{
                    -23.56486,
                    -46.65221
            },
            new double[]{
                    -23.56486,
                    -46.65221
            },
            new double[]{
                    -23.56161,
                    -46.65613
            },
            new double[]{
                    -23.56162,
                    -46.65615
            }
    };

    @Test
    void shouldBeFillableFromDoubleArray() {
        MaplinkPoints points = MaplinkPoints.from(
                -23.56649,
                -46.6538,
                -23.5658,
                -46.65313,
                -23.56486,
                -46.65221,
                -23.56486,
                -46.65221,
                -23.56161,
                -46.65613,
                -23.56162,
                -46.65615
        );

        assertThatMatchWithSample(points);
    }

    @Test
    void shouldFailWhenInvalidDoubleArrayIsPassed() {
        assertThatThrownBy(() -> MaplinkPoints.from(-23.56649, -46.6538, -23.5658)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldSerializeAsValidPolyline() {
        MaplinkPoints points = MaplinkPoints.from(SAMPLE_POINTS);

        assertThatMatchWithSample(points);

        assertThat(points.toPolyline()).isEqualTo(SAMPLE_POLYLINE);
    }

    @Test
    void shouldReadSelfWritedPolyline() {
        MaplinkPoints points = MaplinkPoints.from(randSampleLine(40));
        String polyline = points.toPolyline();
        assertThat(MaplinkPoints.fromPolyline(polyline)).isEqualTo(points);
    }

    @Test
    void shouldDeserializeFromPolyline() {
        MaplinkPoints points = MaplinkPoints.fromPolyline(SAMPLE_POLYLINE);

        assertThatMatchWithSample(points);
    }

    @Test
    @SneakyThrows
    void shouldDeserializeFromLargePolylineWithWrongLastPoint() {
        Path file = Paths.get(getClass().getResource("/polylines/large-polyline-wrong-last-point").toURI());
        MaplinkPoints points = MaplinkPoints.fromPolyline(new String(Files.readAllBytes(file), StandardCharsets.UTF_8));

        assertThat(points).hasSize(11109);
        assertThat(points).first()
                .isEqualTo(points.first())
                .isEqualTo(new MaplinkPoint(-26.30467,-48.84922));
        assertThat(points).last()
                .isEqualTo(points.last())
                .isEqualTo(new MaplinkPoint(-24.02653,-47.17346));
    }

    @Test
    void shouldReadSelfWrittenGeohashing() {
        int geohashSize = 11;
        MaplinkPoints points = MaplinkPoints.from(randSampleLine(50));
        List<String> geohash = points.toGeohash(geohashSize);
        MaplinkPoints fromGeohash = MaplinkPoints.fromGeohash(geohash);

        range(0, points.size()).forEach(i -> {
            MaplinkPoint point = points.get(i);
            MaplinkPoint geohashPoint = fromGeohash.get(i);
            assertThat(point.getLatitude()).isCloseTo(geohashPoint.getLatitude(), Offset.offset(0.0001));
            assertThat(point.getLongitude()).isCloseTo(geohashPoint.getLongitude(), Offset.offset(0.0001));
        });
    }

    @Test
    void shouldSerializeAsValidGeohash() {
        MaplinkPoints points = MaplinkPoints.from(SAMPLE_POINTS);
        assertThat(points.toGeohash()).isEqualTo(SAMPLE_GEOHASHES);
    }

    @Test
    void shouldExtractAsListIteratorOrStream() {
        MaplinkPoints points = MaplinkPoints.from(SAMPLE_POINTS);
        MaplinkPoint[] samplePoints = Arrays.stream(SAMPLE_POINTS).map(MaplinkPoint::from).toArray(MaplinkPoint[]::new);
        assertThat(points.toList()).containsExactly(samplePoints);
        assertThat(points.stream()).containsExactly(samplePoints);
        Iterator<MaplinkPoint> expected = Arrays.stream(samplePoints).iterator();
        for (MaplinkPoint point : points) {
            assertThat(point).isEqualTo(expected.next());
        }
        assertThat(expected.hasNext()).isFalse();
    }

    @Test
    void emptyShouldReturnNullOnFirstLastOrPoint() {
        MaplinkPoints points = MaplinkPoints.from(new double[0][2]);
        assertThat(points.first()).isNull();
        assertThat(points.last()).isNull();
        assertThat(points.get(3)).isNull();
    }

    private double[][] randSampleLine(int lenght) {
        return new Random()
                .ints(lenght, 0, SAMPLE_POINTS.length)
                .mapToObj(i -> SAMPLE_POINTS[i])
                .toArray(double[][]::new);
    }

    private void assertThatMatchWithSample(MaplinkPoints points) {
        assertThat(points).first()
                .isEqualTo(points.first())
                .isEqualTo(MaplinkPoint.from(SAMPLE_POINTS[0]));
        assertThat(points).last()
                .isEqualTo(points.last())
                .isEqualTo(MaplinkPoint.from(SAMPLE_POINTS[5]));

        range(0, SAMPLE_POINTS.length).forEach(i ->
                assertThat(points).element(i)
                        .isEqualTo(MaplinkPoint.from(SAMPLE_POINTS[i]))
        );
    }
}