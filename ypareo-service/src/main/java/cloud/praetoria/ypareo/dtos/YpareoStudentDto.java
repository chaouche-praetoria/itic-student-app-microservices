package cloud.praetoria.ypareo.dtos;

import java.time.LocalDate;

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
    private LocalDate dateNaissance;
    private Long codeGroupe; 
}
