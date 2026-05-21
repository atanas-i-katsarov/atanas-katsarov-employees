package com.atanaskatsarov.employees.model;

import java.util.List;

public record EmployeePairResult(Long employee1Id, Long employee2Id, int totalDaysWorked, List<ProjectOverlap> projectOverlaps) {
  
}
