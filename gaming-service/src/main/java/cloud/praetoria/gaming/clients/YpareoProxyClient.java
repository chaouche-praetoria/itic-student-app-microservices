package cloud.praetoria.gaming.clients;

import java.util.List;

import cloud.praetoria.gaming.dtos.YpareoTrainerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cloud.praetoria.gaming.dtos.YpareoGroupDto;
import cloud.praetoria.gaming.dtos.YpareoStudentDto;

@Component
public class YpareoProxyClient {

    private final WebClient webClient;

    public YpareoProxyClient(@Value("${ypareo.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<YpareoStudentDto> getAllStudents() {
        return webClient.get()
                .uri("/students")
                .retrieve()
                .bodyToFlux(YpareoStudentDto.class)
                .collectList()
                .block();
    }

    public List<YpareoTrainerDto> getAllTrainers() {
        return webClient.get()
                .uri("/trainers")
                .retrieve()
                .bodyToFlux(YpareoTrainerDto.class)
                .collectList()
                .block();
    }

    public List<YpareoGroupDto> getGroupsByTrainer(Long trainerId) {
        return webClient.get()
                .uri("/groups/trainer/sync/{id}", trainerId)
                .retrieve()
                .bodyToFlux(YpareoGroupDto.class)
                .collectList()
                .block();
    }

    public List<YpareoGroupDto> getAllGroups() {
        return webClient.get()
                .uri("/groups")
                .retrieve()
                .bodyToFlux(YpareoGroupDto.class)
                .collectList()
                .block();
    }
}
