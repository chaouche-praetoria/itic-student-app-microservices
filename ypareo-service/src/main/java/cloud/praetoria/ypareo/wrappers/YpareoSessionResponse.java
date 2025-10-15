package cloud.praetoria.ypareo.wrappers;

import java.util.List;

import cloud.praetoria.ypareo.dtos.YpareoCourseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YpareoSessionResponse {
    private List<YpareoCourseDto> cours;
}