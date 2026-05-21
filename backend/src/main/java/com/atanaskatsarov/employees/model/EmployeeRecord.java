package com.atanaskatsarov.employees.model;

import java.time.LocalDate;

public record EmployeeRecord(Long id, Long projectId, LocalDate dateFrom, LocalDate dateTo) {
  
}
