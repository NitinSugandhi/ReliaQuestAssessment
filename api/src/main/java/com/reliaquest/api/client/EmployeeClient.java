package com.reliaquest.api.client;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.Response;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@Slf4j
public class EmployeeClient {

    private final WebClient webClient;

    public EmployeeClient(WebClient.Builder webClientBuilder, @Value("${api.base-url}") String apiBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(apiBaseUrl).build();
    }

    private <R> Mono<? extends R> validateAndExtractEmployee(Response<R> employeeResponse) {
        if (employeeResponse.status().equals(Response.Status.ERROR)) {
            log.error("API returned error: {} ", employeeResponse);
            return Mono.error(new RuntimeException("API error: " + employeeResponse));
        }
        return Mono.just(employeeResponse.data());
    }

    private Mono<? extends Throwable> handleErrorResponse(ClientResponse response) {
        return response.bodyToMono(String.class).flatMap(errorBody -> {
            log.error("Error response from server: {}", errorBody);
            return Mono.error(new RuntimeException("Server error: " + errorBody));
        });
    }

    private Retry retryWhen() {
        return Retry.backoff(5, Duration.ofSeconds(2))
                .filter(throwable -> throwable instanceof WebClientResponseException
                        && ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.TOO_MANY_REQUESTS);
    }

    public Mono<Employee> getById(String employeeId) {
        return webClient
                .get()
                .uri("/employee/{id}", employeeId)
                .retrieve()
                .onStatus(code -> code.isError() && code != HttpStatus.NOT_FOUND, this::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<Response<Employee>>() {})
                .retryWhen(retryWhen())
                .onErrorResume(
                        e -> e instanceof WebClientResponseException
                                && ((WebClientResponseException) e).getStatusCode() == HttpStatus.NOT_FOUND,
                        exception -> Mono.empty())
                .flatMap(this::validateAndExtractEmployee);
    }

    public Mono<List<Employee>> getAll() {
        return webClient
                .get()
                .uri("/employee")
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<Response<List<Employee>>>() {})
                .retryWhen(retryWhen())
                .flatMap(this::validateAndExtractEmployee);
    }

    public Mono<Employee> create(EmployeeInput employeeInput) {
        return webClient
                .post()
                .uri("/employee")
                .bodyValue(employeeInput)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<Response<Employee>>() {})
                .retryWhen(retryWhen())
                .flatMap(this::validateAndExtractEmployee);
    }

    public Mono<Boolean> delete(EmployeeInput deleteEmployeeInput) {
        return webClient
                .method(HttpMethod.DELETE)
                .uri("/employee")
                .bodyValue(deleteEmployeeInput)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<Response<Boolean>>() {})
                .retryWhen(retryWhen())
                .map(Response::data);
    }
}
