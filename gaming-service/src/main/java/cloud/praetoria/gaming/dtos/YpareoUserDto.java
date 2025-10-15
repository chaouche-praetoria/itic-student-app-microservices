package cloud.praetoria.gaming.dtos;

import java.util.List;

import lombok.Data;

@Data
public class YpareoUserDto {
    private Long ypareoCode;
    private String firstName;
    private String lastName;
    private List<Long> codeGroupe;
}