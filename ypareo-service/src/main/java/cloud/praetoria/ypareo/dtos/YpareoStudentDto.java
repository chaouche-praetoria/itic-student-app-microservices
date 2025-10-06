package cloud.praetoria.ypareo.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YpareoStudentDto {
    private Long codeApprenant;
    private String login;
    private String nom;
    private String prenom;
    private String email;
    private String typeUtilisateur;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dateNaissance;

    private Long codeGroupe; 
}
