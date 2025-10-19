package cloud.praetoria.auth.services;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cloud.praetoria.auth.dtos.StudentInfoDto;
import cloud.praetoria.auth.dtos.TrainerInfoDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class YpareoServiceClient {

	private final WebClient webClient;

	@Value("${ypareo.service.base-url:http://localhost:8082}")
	private String ypareoServiceBaseUrl;

	@Value("${ypareo.service.timeout:5}")
	private int timeoutSeconds;
	
	private static final String YPAREO_CB = "ypareo-service";

	@Retry(name = YPAREO_CB, fallbackMethod = "fallbackGetStudentInfo")
	@CircuitBreaker(name = YPAREO_CB, fallbackMethod = "fallbackGetStudentInfo")
	public StudentInfoDto getStudentInfo(String ypareoLogin) {
			log.info("Fetching student info from Ypareo API Service for login: {}", ypareoLogin);

			return webClient.get().uri(ypareoServiceBaseUrl + "/api/ypareo/students/{ypareoLogin}", ypareoLogin).retrieve()
					.bodyToMono(StudentInfoDto.class).timeout(Duration.ofSeconds(timeoutSeconds)).doOnSuccess(student -> {
						if (student != null) {
							log.info("Successfully retrieved student: {} - {}", student.getYpareoId(),
									student.getFirstName() + " " + student.getLastName());
						} else {
							log.warn("No student data returned for Login: {}", ypareoLogin);
						}
					}).onErrorResume(WebClientResponseException.class, ex -> {
						if (ex.getStatusCode().value() == 404) {
							log.warn("Student not found in Ypareo: {}", ypareoLogin);
							return Mono.empty();
						} else {
							log.error("Error calling Ypareo API Service: {} - {}", ex.getStatusCode(), ex.getMessage());
							return Mono.error(ex);
						}
					}).block();

	}

	public boolean validateStudent(String ypareoId) {
		try {
			StudentInfoDto studentInfo = getStudentInfo(ypareoId);
			return studentInfo != null && Boolean.TRUE.equals(studentInfo.getIsActive());
		} catch (Exception e) {
			log.error("Error validating student: {}", ypareoId, e);
			return false;
		}
	}

	public boolean isYpareoServiceAvailable() {
		try {
			return webClient.get().uri(ypareoServiceBaseUrl + "/actuator/health").retrieve().bodyToMono(String.class)
					.timeout(Duration.ofSeconds(3)).map(response -> response.contains("UP")).onErrorReturn(false)
					.block();
		} catch (Exception e) {
			log.warn("Ypareo service health check failed", e);
			return false;
		}
	}

	public StudentInfoDto fallbackGetStudentInfo(String ypareoId, Throwable ex) {
		log.warn("Fallback activated for student info. Could not fetch from Ypareo for ID: {}. Reason: {}", ypareoId,
				ex.getMessage());
		return null;
	}

	@Retry(name = YPAREO_CB, fallbackMethod = "fallbackGetTeacherInfo")
	@CircuitBreaker(name = YPAREO_CB, fallbackMethod = "fallbackGetTeacherInfo")
	public TrainerInfoDto getTrainerInfo(String ypareoLogin) {
	    log.info("Fetching teacher info from Ypareo API Service for login: {}", ypareoLogin);

	    return webClient.get()
	            .uri(ypareoServiceBaseUrl + "/api/ypareo/teachers/{ypareoLogin}", ypareoLogin)
	            .retrieve()
	            .bodyToMono(TrainerInfoDto.class)
	            .timeout(Duration.ofSeconds(timeoutSeconds))
	            .doOnSuccess(trainer -> {
	                if (trainer != null) {
	                    log.info("Successfully retrieved teacher: {} - {}", 
	                    		trainer.getYpareoId(), 
	                    		trainer.getFirstName() + " " + trainer.getLastName());
	                } else {
	                    log.warn("No teacher data returned for Login: {}", ypareoLogin);
	                }
	            })
	            .onErrorResume(WebClientResponseException.class, ex -> {
	                if (ex.getStatusCode().value() == 404) {
	                    log.warn("Teacher not found in Ypareo: {}", ypareoLogin);
	                    return Mono.empty();
	                } else {
	                    log.error("Error calling Ypareo API Service: {} - {}", 
	                        ex.getStatusCode(), ex.getMessage());
	                    return Mono.error(ex);
	                }
	            })
	            .block();
	}
	public TrainerInfoDto fallbackGetTeacherInfo(String ypareoId, Throwable ex) {
	    log.warn("Fallback activated for teacher info. Could not fetch from Ypareo for ID: {}. Reason: {}", 
	        ypareoId, ex.getMessage());
	    return null;
	}
}