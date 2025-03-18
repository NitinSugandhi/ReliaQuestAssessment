package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    EmployeeService employeeService;

    @Test
    void testGetById_Success() {
        UUID uuid = UUID.randomUUID();
        Employee mockEmployee = new Employee(uuid, "employee_name", 100000, 25, "test", "test_email");
        when(employeeService.getById(uuid.toString())).thenReturn(mockEmployee);
        webTestClient
                .get()
                .uri("/api/v1/employee/" + uuid)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Employee.class)
                .isEqualTo(mockEmployee);
    }

    @Test
    void testGetById_NotFound() {
        when(employeeService.getById(anyString())).thenReturn(null);
        webTestClient.get().uri("/api/v1/employee/1").exchange().expectStatus().isNotFound();
    }

    @Test
    void testGetAll() {
        UUID uuid = UUID.randomUUID();
        Employee mockEmployee = new Employee(uuid, "employee_name", 100000, 25, "test", "test_email");
        when(employeeService.getAll()).thenReturn(List.of(mockEmployee));
        webTestClient
                .get()
                .uri("/api/v1/employee")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Employee.class)
                .hasSize(1)
                .isEqualTo(List.of(mockEmployee));
    }

    @Test
    void testGetAllEmptyResponse() {
        when(employeeService.getAll()).thenReturn(new ArrayList<>());
        webTestClient
                .get()
                .uri("/api/v1/employee")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Employee.class)
                .hasSize(0);
    }

    @Test
    void testGetByNameSearch() {
        UUID uuid = UUID.randomUUID();
        Employee mockEmployee = new Employee(uuid, "employee_name", 100000, 25, "test", "test_email");
        when(employeeService.getByNameSearch(anyString())).thenReturn(List.of(mockEmployee));
        webTestClient
                .get()
                .uri("/api/v1/employee/search/test")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Employee.class)
                .hasSize(1)
                .isEqualTo(List.of(mockEmployee));
    }

    @Test
    void testGetByNameSearchEmptyResponse() {
        when(employeeService.getByNameSearch(anyString())).thenReturn(new ArrayList<>());
        webTestClient
                .get()
                .uri("/api/v1/employee/search/name")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Employee.class)
                .hasSize(0);
    }

    @Test
    void testGetHighestEarningEmployeeName() {
        List<String> employeeNames = List.of("name1", "name2", "name3");
        when(employeeService.highestEarningEmployeeName(10)).thenReturn(employeeNames);
        webTestClient
                .get()
                .uri("/api/v1/employee/topTenHighestEarningEmployeeNames")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .consumeWith(response -> {
                    List<String> body = response.getResponseBody();
                    assertNotNull(body);
                    assertEquals(3, body.size(), "Expected list size does not match");
                    assertEquals(List.of("name1", "name2", "name3"), body, "Expected employee names do not match");
                });
    }

    @Test
    void testGetHighestSalary() {
        when(employeeService.getHighestSalary()).thenReturn(java.util.Optional.of(1000));
        webTestClient
                .get()
                .uri("/api/v1/employee/highestSalary")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Integer.class)
                .isEqualTo(1000);
    }

    @Test
    void testGetHighestSalary_Not_Found() {
        UUID uuid = UUID.randomUUID();
        when(employeeService.getHighestSalary()).thenReturn(Optional.empty());
        webTestClient
                .get()
                .uri("/api/v1/employee/highestSalary")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testCreate() {
        UUID uuid = UUID.randomUUID();
        Employee mockEmployee = new Employee(uuid, "employee_name", 100000, 25, "test", "test_email");
        EmployeeInput employeeInput = new EmployeeInput("employee_name", 100000, 25, "test");
        when(employeeService.create(any())).thenReturn(mockEmployee);
        webTestClient
                .post()
                .uri("/api/v1/employee")
                .bodyValue(employeeInput)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Employee.class);
    }

    @Test
    void testCreate_validation() {
        EmployeeInput employeeInput = new EmployeeInput(null, 100000, 25, "test");
        webTestClient
                .post()
                .uri("/api/v1/employee")
                .bodyValue(employeeInput)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .value(ex -> assertTrue(ex.contains("Validation failed for argument at index 0 in method")));
    }

    @Test
    void testDelete() {
        when(employeeService.deleteById(any())).thenReturn(Optional.of("testEmployee"));
        webTestClient
                .delete()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("testEmployee");
    }

    @Test
    void testDelete_notFound() {
        when(employeeService.deleteById(any())).thenReturn(Optional.empty());
        webTestClient
                .delete()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
