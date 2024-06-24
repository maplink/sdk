package global.maplink.planning.schema.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(force = true)
public class PositionInTimeWindow {

	public static final String INDIFFERENT = "INDIFFERENT";
	public static final String FIRST = "FIRST";
	public static final String LAST = "LAST";
}
