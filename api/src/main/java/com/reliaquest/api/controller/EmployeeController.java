package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("Received request to fetch all employee");
        List<Employee> employees = employeeService.getAll();
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable("searchString") String searchString) {
        log.info("Received request to fetch employee by name: {}", searchString);
        List<Employee> employees = employeeService.getByNameSearch(searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") String id) {
        log.info("Received request to fetch employee by id: {}", id);
        Employee employee = employeeService.getById(id);
        return employee != null
                ? ResponseEntity.ok(employee)
                : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Received request to fetch highest salary of employee");
        Optional<Integer> highestSalary = employeeService.getHighestSalary();
        return highestSalary.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound()
                .build());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Received request to fetch top ten highest earning employee names");
        List<String> top10HighestEarningEmployee = employeeService.highestEarningEmployeeName(10);
        return ResponseEntity.ok(top10HighestEarningEmployee);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeInput employeeInput) {
        log.info("Received request to create employee");
        Employee employee = employeeService.create(employeeInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        log.info("Received request to delete employee by id: {}", id);
        Optional<String> deleted = employeeService.deleteById(id);
        return deleted.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
