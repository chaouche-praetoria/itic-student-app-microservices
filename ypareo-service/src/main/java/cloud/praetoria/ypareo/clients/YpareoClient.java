package cloud.praetoria.ypareo.clients;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cloud.praetoria.ypareo.dtos.YpareoCourseDto;
import cloud.praetoria.ypareo.dtos.YpareoGroupDto;
import cloud.praetoria.ypareo.dtos.YpareoStudentDto;
import cloud.praetoria.ypareo.dtos.YpareoTrainerDto;
import cloud.praetoria.ypareo.wrappers.YpareoSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class YpareoClient {

    private final WebClient webClient;

    @Value("${ypareo.api.base-url}")
    private String baseUrl;

    @Value("${ypareo.auth.token}")
    private String xAuthToken;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public List<YpareoStudentDto> getAllStudents() {
    	String uri = baseUrl + "/utilisateur/apprenants";
    	
        log.info("Calling YParéo API for groups: {}", uri);

        Map<String, YpareoStudentDto> responseMap = webClient.get()
                .uri(uri)
                .header("X-Auth-Token", xAuthToken)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "Server Error " + resp.statusCode().value() + ": " + body))))
                .bodyToMono(new ParameterizedTypeReference<Map<String, YpareoStudentDto>>() {})
                .block();

        if (responseMap == null) {
            log.warn("No students returned from YParéo API");
            return List.of();
        }

        return new ArrayList<>(responseMap.values());
    }
    
    public List<YpareoCourseDto> getCoursesForStudent(Long studentId, String startDate, String endDate) {
        String uri = String.format("%s/r/v1/planning/%s/%s/apprenant/%d", baseUrl, startDate, endDate, studentId);
        log.info("Calling YParéo API: {}", uri);

        return webClient.get()
                .uri(uri)
                .header("X-Auth-Token", xAuthToken)
                .retrieve()
                .bodyToFlux(YpareoCourseDto.class)
                .collectList()
                .block();
    }

    public List<YpareoGroupDto> getAllGroups() {
    	String uri = baseUrl + "/formation-longue/groupes";
        log.info("Calling YParéo API for groups: {}", uri);

        Map<String, YpareoGroupDto> responseMap = webClient.get()
                .uri(uri)
                .header("X-Auth-Token", xAuthToken)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                resp.bodyToMono(String.class).defaultIfEmpty("")
                        .flatMap(body -> Mono.error(new RuntimeException(
                                "Server Error " + resp.statusCode().value() + ": " + body))))
                .bodyToMono(new ParameterizedTypeReference<Map<String, YpareoGroupDto>>() {})
                .block();

        if (responseMap == null) {
            log.warn("No groups returned from YParéo API");
            return List.of();
        }

        return new ArrayList<>(responseMap.values());
    }

    public List<YpareoTrainerDto> getAllTrainers() {
        String uri = baseUrl + "/personnels";
        log.info("Calling YParéo API for trainers: {}", uri);

        Map<String, YpareoTrainerDto> responseMap = webClient.get()
                .uri(uri)
                .header("X-Auth-Token", xAuthToken)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "Server Error " + resp.statusCode().value() + ": " + body))))
                .bodyToMono(new ParameterizedTypeReference<Map<String, YpareoTrainerDto>>() {})
                .block();

        if (responseMap == null) {
            log.warn("No trainers returned from YParéo API");
            return List.of();
        }

        return new ArrayList<>(responseMap.values());
    }
    
    public List<YpareoCourseDto> getSessionsForStudent(Long studentId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching sessions for student {} from {} to {}", studentId, startDate, endDate);

        String uri = String.format("%s/planning/%s/%s/apprenant/%d",
                baseUrl,
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER),
                studentId
        );

        try {
            YpareoSessionResponse response = webClient.get()
                    .uri(uri)
                    .header("X-Auth-Token", xAuthToken)
                    .retrieve()
                    .bodyToMono(YpareoSessionResponse.class)
                    .block();

            if (response == null || response.getCours() == null || response.getCours().isEmpty()) {
                log.warn("No sessions found for student {}", studentId);
                return List.of();
            }

            return response.getCours();

        } catch (Exception e) {
            log.error("Error fetching sessions from YParéo for student {}", studentId, e);
            return List.of();
        }
    }

  
}
