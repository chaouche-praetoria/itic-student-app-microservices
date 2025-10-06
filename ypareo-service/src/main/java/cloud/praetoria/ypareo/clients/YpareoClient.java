package cloud.praetoria.ypareo.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cloud.praetoria.ypareo.dtos.YpareoCourseDto;
import cloud.praetoria.ypareo.dtos.YpareoStudentDto;
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

    public List<YpareoStudentDto> getAllStudents() {
        log.info("Calling YParéo API: /r/v1/utilisateur/apprenants");

        Map<String, YpareoStudentDto> responseMap = webClient.get()
                .uri(baseUrl + "/utilisateur/apprenants")
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

}
