package cloud.praetoria.ypareo.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DaillySessionDto {
	private LocalDate date;
	private List<SessionDto> sessions;
	private Integer totalSessions;
	private String totalDuree; 
}
