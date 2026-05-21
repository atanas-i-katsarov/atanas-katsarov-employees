package com.atanaskatsarov.employees.model;

import java.util.ArrayList;
import java.util.List;

public class EmployeePairAggregate {

  private final Long employee1Id;
  private final Long employee2Id;

  private int totalDaysWorked;

  private final List<ProjectOverlap> projectOverlaps = new ArrayList<>();

  public EmployeePairAggregate(
      Long employee1Id,
      Long employee2Id) {
    this.employee1Id = employee1Id;
    this.employee2Id = employee2Id;
  }

  public void addProjectOverlap(
      ProjectOverlap overlap) {
    projectOverlaps.add(overlap);
    totalDaysWorked += overlap.daysWorked();
  }

  public int totalDaysWorked() {
    return totalDaysWorked;
  }

  public EmployeePairResult toResult() {

    return new EmployeePairResult(
        employee1Id,
        employee2Id,
        totalDaysWorked,
        List.copyOf(projectOverlaps));
  }
}