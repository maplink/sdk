package global.maplink.trip.schema.solution;

import global.maplink.emission.schema.EmissionResponse;
import global.maplink.freight.schema.FreightCalculationResponse;
import global.maplink.geocode.schema.Address;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@Data
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class TripResponse {
    private final String id;
    private final String clientId;
    private final Long totalDistance;
    private final Long totalNominalDuration;
    private final Double averageSpeed;
    private final BigDecimal tollCosts;
    private final BigDecimal routeFreightCost;
    private final List<SolutionLeg> legs;
    private final List<CrossedBorderResponse> crossedBorders;
    private final FreightCalculationResponse freight;
    private final EmissionResponse emission;
    private final Address startAddress;
    private final Address endAddress;
    private final String source;
    private final LocalDate createdAt;
    private final LocalDate expireIn;
}
