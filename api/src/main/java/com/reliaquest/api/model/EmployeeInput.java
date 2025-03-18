package com.reliaquest.api.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EmployeeInput {

    @NotBlank
    private String name;

    @Positive @NotNull private Integer salary;

    @Min(16)
    @Max(75)
    @NotNull private Integer age;

    @NotBlank
    private String title;

    public EmployeeInput(String employee_name, int salary, int age, String title) {
        this.name = employee_name;
        this.salary = salary;
        this.age = age;
        this.title = title;
    }

    public EmployeeInput(String employee_name) {
        this.name = employee_name;
    }
}
