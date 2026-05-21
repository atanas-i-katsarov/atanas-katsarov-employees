package com.atanaskatsarov.employees.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atanaskatsarov.employees.exception.InvalidCsvException;
import com.atanaskatsarov.employees.model.EmployeePairKey;
import com.atanaskatsarov.employees.model.EmployeePairResult;
import com.atanaskatsarov.employees.model.EmployeeRecord;
import com.atanaskatsarov.employees.model.ProjectOverlap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeeService {

  public EmployeePairResult getEmployeePairsFromCsv(MultipartFile file) throws InvalidCsvException {
    List<EmployeeRecord> employeeRecords = parseEmployeeCsv(file);
    return findEmployeePairWithMostDaysWorked(employeeRecords);
  }

  private EmployeePairResult findEmployeePairWithMostDaysWorked(List<EmployeeRecord> employeeRecords) {
    // Group by projectID
    Map<Long, List<EmployeeRecord>> projectToEmployeesMap = employeeRecords.stream()
        .collect(Collectors.groupingBy(EmployeeRecord::projectId));

    // For each project find employee pairs
    final Map<EmployeePairKey, EmployeePairResult> pairToProjectOverlapsMap = new HashMap<>();
    projectToEmployeesMap.entrySet().forEach(entry -> {
      Long projectId = entry.getKey();
      List<EmployeeRecord> employees = entry.getValue();

      Map<EmployeePairKey, ProjectOverlap> pairToOverlapMap = new HashMap<>();
      for (int i = 0; i < employees.size(); i++) {
        for (int j = i + 1; j < employees.size(); j++) {
          EmployeeRecord emp1 = employees.get(i);
          EmployeeRecord emp2 = employees.get(j);

          EmployeePairKey employeePairKey = new EmployeePairKey(Math.min(emp1.id(), emp2.id()),
              Math.max(emp1.id(), emp2.id()));

          int daysWorked = calculateOverlapDays(emp1, emp2);
          if (daysWorked > 0) {
            if (pairToOverlapMap.containsKey(employeePairKey)) {
              ProjectOverlap existingOverlap = pairToOverlapMap.get(employeePairKey);
              pairToOverlapMap.put(employeePairKey,
                  new ProjectOverlap(projectId, existingOverlap.daysWorked() + daysWorked));
            } else {
              pairToOverlapMap.put(employeePairKey, new ProjectOverlap(projectId, daysWorked));
            }
          }
        }
      }

      pairToOverlapMap.entrySet().forEach(pairEntry -> {
        EmployeePairKey pairKey = pairEntry.getKey();
        ProjectOverlap overlap = pairEntry.getValue();
        if (pairToProjectOverlapsMap.containsKey(pairKey)) {
          EmployeePairResult existingResult = pairToProjectOverlapsMap.get(pairKey);
          List<ProjectOverlap> updatedOverlaps = new ArrayList<>(existingResult.projectOverlaps());
          updatedOverlaps.add(overlap);
          pairToProjectOverlapsMap.put(pairKey, new EmployeePairResult(pairKey.employee1Id(), pairKey.employee2Id(),
              existingResult.totalDaysWorked() + overlap.daysWorked(), updatedOverlaps));
        } else {
          pairToProjectOverlapsMap.put(pairKey, new EmployeePairResult(pairKey.employee1Id(), pairKey.employee2Id(),
              overlap.daysWorked(), List.of(overlap)));
        }
      });
    });

    return pairToProjectOverlapsMap.values().stream()
        .max((p1, p2) -> Integer.compare(p1.totalDaysWorked(), p2.totalDaysWorked()))
        .orElse(new EmployeePairResult(null, null, 0, List.of()));
  }

  private int calculateOverlapDays(EmployeeRecord emp1, EmployeeRecord emp2) {
    LocalDate overlapStart = emp1.dateFrom().isAfter(emp2.dateFrom()) ? emp1.dateFrom() : emp2.dateFrom();
    LocalDate overlapEnd = emp1.dateTo().isBefore(emp2.dateTo()) ? emp1.dateTo() : emp2.dateTo();
    if (overlapStart.isBefore(overlapEnd) || overlapStart.isEqual(overlapEnd)) {
      return (int) ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1; // TODO: Check if we should include both start
                                                                          // and end date as worked days, for now we
                                                                          // assume that if they overlap at least one
                                                                          // day, it counts as 1 day worked together.
    }
    return 0;
  }

  private List<EmployeeRecord> parseEmployeeCsv(MultipartFile file) throws InvalidCsvException {
    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
        CSVParser csvParser = new CSVParser(fileReader,
            CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build())) {

      List<EmployeeRecord> employees = new ArrayList<>();
      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {
        try {
          EmployeeRecord employee = new EmployeeRecord(
              Long.valueOf(csvRecord.get(0)),
              Long.valueOf(csvRecord.get(1)),
              LocalDate.parse(csvRecord.get(2)), // TODO: All date formats should be supported, but for now we can
                                                 // assume ISO format
              LocalDate.parse(csvRecord.get(3)) // TODO: null should be now date
          );
          employees.add(employee);
        } catch (Exception e) {
          log.warn("Skipping invalid CSV record at line {}: {}", csvRecord.getRecordNumber(), e.getMessage());
        }
      }
      return employees;
    } catch (Exception e) {
      throw new InvalidCsvException("Failed to parse CSV file: " + e.getMessage());
    }
  }
}
