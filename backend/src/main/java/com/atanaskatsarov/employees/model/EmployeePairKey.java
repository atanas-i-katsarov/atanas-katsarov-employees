package com.atanaskatsarov.employees.model;

public record EmployeePairKey(Long employee1Id, Long employee2Id) {
  public EmployeePairKey {
        Long minId = Math.min(employee1Id, employee2Id);
        Long maxId = Math.max(employee1Id, employee2Id);
        employee1Id = minId;
        employee2Id = maxId;
    }
}
