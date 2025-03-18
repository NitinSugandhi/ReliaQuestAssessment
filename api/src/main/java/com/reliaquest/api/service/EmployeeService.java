package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeClient;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeClient employeeClient;

    public List<Employee> getAll() {
        log.debug("fetching all employee");
        return employeeClient.getAll().block();
    }

    public List<Employee> getByNameSearch(String name) {
        log.debug("Searching employee by name {}", name);
        return getAll().stream()
                .filter(employee -> employee.employee_name().toLowerCase().contains(name.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    public Employee getById(String id) {
        log.debug("Getting employee by id {}", id);
        return employeeClient.getById(id).block();
    }

    public Optional<Integer> getHighestSalary() {
        log.debug("Getting Highest Salary");
        return getAll().stream().map(Employee::employee_salary).max(Integer::compareTo);
    }

    public List<String> highestEarningEmployeeName(int limit) {
        log.debug("Getting top {} earning employee name", limit);
        return getAll().stream()
                .sorted(Comparator.comparing(Employee::employee_salary).reversed())
                .limit(limit)
                .map(Employee::employee_name)
                .collect(Collectors.toList());
    }

    public Employee create(EmployeeInput employeeInput) {
        log.debug("creating employee {}", employeeInput);
        return Objects.requireNonNull(employeeClient.create(employeeInput).block());
    }

    public Optional<String> deleteById(String id) {
        log.debug("deleting employee {}", id);
        Employee employee = getById(id);
        if (employee != null) {
            Boolean status = employeeClient
                    .delete(new EmployeeInput(employee.employee_name()))
                    .block();
            if (Boolean.TRUE.equals(status)) {
                log.info("Successfully deleted employee: {}", employee.employee_name());
                return Optional.of(employee.employee_name());
            } else {
                log.warn("Failed to delete employee: {}", employee.employee_name());
            }
        } else {
            log.warn("No employee with id: {}", id);
        }
        return Optional.empty();
    }
}
