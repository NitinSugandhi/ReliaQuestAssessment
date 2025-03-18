package com.reliaquest.api.model;

import java.util.UUID;

public record Employee(
        UUID id,
        String employee_name,
        Integer employee_salary,
        Integer employee_age,
        String employee_title,
        String employee_email) {}
