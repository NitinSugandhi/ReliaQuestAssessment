package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reliaquest.api.client.EmployeeClient;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

public class EmployeeServiceTest {

    @Mock
    EmployeeClient employeeClient;

    @InjectMocks
    EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetById_Success() {
        UUID uuid = UUID.randomUUID();
        Employee mockEmployee = new Employee(uuid, "employee_name", 100000, 25, "test", "test_email");
        when(employeeClient.getById(uuid.toString())).thenReturn(Mono.just(mockEmployee));
        Employee result = employeeService.getById(uuid.toString());
        assertEquals(mockEmployee, result);
    }

    @Test
    void testGetById_NotFound() {
        when(employeeClient.getById(anyString())).thenReturn(Mono.empty());
        Employee result = employeeService.getById("");
        assertNull(result);
    }

    @Test
    void testGetAll_Success() {
        UUID uuid = UUID.randomUUID();
        Employee mockEmployee = new Employee(uuid, "employee_name", 100000, 25, "test", "test_email");
        when(employeeClient.getAll()).thenReturn(Mono.just(List.of(mockEmployee)));
        List<Employee> result = employeeService.getAll();
        assertEquals(List.of(mockEmployee), result);
    }

    @Test
    void testGetAll_EmptyList() {
        when(employeeClient.getAll()).thenReturn(Mono.just(List.of()));
        List<Employee> result = employeeService.getAll();
        assertEquals(List.of(), result);
    }

    @Test
    void testGetByNameSearch_Success() {
        Employee mockEmployee = new Employee(UUID.randomUUID(), "test1", 100000, 25, "test", "test_email");
        Employee mockEmployee1 = new Employee(UUID.randomUUID(), "test2", 100000, 25, "test", "test_email");
        Employee mockEmployee2 = new Employee(UUID.randomUUID(), "name", 100000, 25, "test", "test_email");
        when(employeeClient.getAll()).thenReturn(Mono.just(List.of(mockEmployee, mockEmployee1, mockEmployee2)));
        List<Employee> result = employeeService.getByNameSearch("test");
        assertEquals(List.of(mockEmployee, mockEmployee1), result);
    }

    @Test
    void testGetByNameSearch_EmptyList() {
        Employee mockEmployee = new Employee(UUID.randomUUID(), "test1", 100000, 25, "test", "test_email");
        Employee mockEmployee1 = new Employee(UUID.randomUUID(), "test2", 100000, 25, "test", "test_email");
        Employee mockEmployee2 = new Employee(UUID.randomUUID(), "name", 100000, 25, "test", "test_email");
        when(employeeClient.getAll()).thenReturn(Mono.just(List.of(mockEmployee, mockEmployee1, mockEmployee2)));
        List<Employee> result = employeeService.getByNameSearch("test123");
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void testGetHighestSalary() {
        Employee mockEmployee = new Employee(UUID.randomUUID(), "test1", 100000, 25, "test", "test_email");
        Employee mockEmployee1 = new Employee(UUID.randomUUID(), "test2", 200000, 25, "test", "test_email");
        Employee mockEmployee2 = new Employee(UUID.randomUUID(), "name", 300000, 25, "test", "test_email");
        when(employeeClient.getAll()).thenReturn(Mono.just(List.of(mockEmployee, mockEmployee1, mockEmployee2)));
        Optional<Integer> result = employeeService.getHighestSalary();
        assertEquals(Optional.of(300000), result);
    }

    @Test
    void testGetHighestSalary_NotFound() {
        when(employeeClient.getAll()).thenReturn(Mono.just(List.of()));
        Optional<Integer> result = employeeService.getHighestSalary();
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testHighestEarningEmployeeName() {
        Employee mockEmployee = new Employee(UUID.randomUUID(), "test1", 200000, 25, "test", "test_email");
        Employee mockEmployee1 = new Employee(UUID.randomUUID(), "test2", 300000, 25, "test", "test_email");
        Employee mockEmployee2 = new Employee(UUID.randomUUID(), "name", 100000, 25, "test", "test_email");
        when(employeeClient.getAll()).thenReturn(Mono.just(List.of(mockEmployee, mockEmployee1, mockEmployee2)));
        List<String> result = employeeService.highestEarningEmployeeName(2);
        assertEquals(List.of("test2", "test1"), result);
    }

    @Test
    void testHighestEarningEmployeeName_Empty() {
        when(employeeClient.getAll()).thenReturn(Mono.just(List.of()));
        List<String> result = employeeService.highestEarningEmployeeName(2);
        assertEquals(List.of(), result);
    }

    @Test
    void testCreate() {
        Employee mockEmployee = new Employee(UUID.randomUUID(), "name", 100000, 25, "test", "test_email");
        EmployeeInput employeeInput = new EmployeeInput("name", 100000, 25, "test");
        when(employeeClient.create(employeeInput)).thenReturn(Mono.just(mockEmployee));
        Employee result = employeeService.create(employeeInput);
        assertEquals(mockEmployee, result);
    }

    @Test
    void testDelete() {
        Employee mockEmployee = new Employee(UUID.randomUUID(), "name", 100000, 25, "test", "test_email");
        when(employeeClient.getById(any())).thenReturn(Mono.just(mockEmployee));
        when(employeeClient.delete(any())).thenReturn(Mono.just(true));
        Optional<String> result = employeeService.deleteById("2");
        assertEquals(Optional.of("name"), result);
    }

    @Test
    void testDelete_notFound() {
        when(employeeClient.getById(any())).thenReturn(Mono.empty());
        when(employeeClient.delete(any())).thenReturn(Mono.just(false));
        Optional<String> result = employeeService.deleteById("2");
        assertEquals(Optional.empty(), result);
    }
}
