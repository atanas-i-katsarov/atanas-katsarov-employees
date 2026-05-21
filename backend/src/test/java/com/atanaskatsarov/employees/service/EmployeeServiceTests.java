package com.atanaskatsarov.employees.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.atanaskatsarov.employees.exception.InvalidCsvException;
import com.atanaskatsarov.employees.model.EmployeePairResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Tests")
class EmployeeServiceTests {

  @InjectMocks
  private EmployeeService employeeService;

  private MultipartFile mockFile;

  @BeforeEach
  void setUp() {
    mockFile = mock(MultipartFile.class);
  }

  @Test
  @DisplayName("Should parse valid CSV with employees on same project")
  void testParseValidCsvWithOverlap() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,2020-01-10\n" +
        "2,1,2020-01-05,2020-01-15\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    assertEquals(1L, result.employee1Id());
    assertEquals(2L, result.employee2Id());
    assertTrue(result.totalDaysWorked() > 0);
  }

  @Test
  @DisplayName("Should handle employees with no overlap")
  void testParseEmployeesWithNoOverlap() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,2020-01-05\n" +
        "2,1,2020-01-10,2020-01-15\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    assertEquals(0, result.totalDaysWorked());
  }

  @Test
  @DisplayName("Should handle single employee")
  void testParseSingleEmployee() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,2020-01-10\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    assertEquals(0, result.totalDaysWorked());
  }

  @Test
  @DisplayName("Should handle empty CSV file")
  void testParseEmptyCSV() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    assertEquals(0, result.totalDaysWorked());
  }

  @Test
  @DisplayName("Should skip invalid CSV records")
  void testSkipInvalidRecords() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,2020-01-10\n" +
        "invalid,data,here\n" +
        "2,1,2020-01-05,2020-01-15\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    // Should still find the valid pair
    assertEquals(1L, result.employee1Id());
    assertEquals(2L, result.employee2Id());
  }

  @Test
  @DisplayName("Should handle multiple projects and find pair with most overlap")
  void testMultipleProjectsWithDifferentOverlaps() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,2020-01-05\n" +
        "2,1,2020-01-02,2020-01-04\n" +
        "1,2,2020-02-01,2020-02-10\n" +
        "2,2,2020-02-05,2020-02-20\n" +
        "3,2,2020-02-06,2020-02-08\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    // Employees 1 and 2 have more total overlap days across both projects
    assertEquals(1L, result.employee1Id());
    assertEquals(2L, result.employee2Id());
    assertTrue(result.totalDaysWorked() > 0);
    assertEquals(2, result.projectOverlaps().size());
  }

  @Test
  @DisplayName("Should handle null DateTo as current date")
  void testNullDateToAsCurrentDate() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,\n" +
        "2,1,2020-01-05,\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    assertEquals(1L, result.employee1Id());
    assertEquals(2L, result.employee2Id());
    assertTrue(result.totalDaysWorked() > 0);
  }

  @Test
  @DisplayName("Should handle different date formats")
  void testDifferentDateFormats() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,2020-01-10\n" +
        "2,1,01/01/2020,10/01/2020\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    assertEquals(1L, result.employee1Id());
    assertEquals(2L, result.employee2Id());
  }

  @Test
  @DisplayName("Should calculate overlap days correctly for same start and end dates")
  void testCalculateOverlapDaysSameDateRange() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,2020-01-05\n" +
        "2,1,2020-01-01,2020-01-05\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    // 5 days (Jan 1-5 inclusive)
    assertEquals(5, result.totalDaysWorked());
  }

  @Test
  @DisplayName("Should calculate overlap days correctly for partial overlap")
  void testCalculateOverlapDaysPartialOverlap() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,2020-01-10\n" +
        "2,1,2020-01-05,2020-01-15\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    // Overlap from Jan 5 to Jan 10 = 6 days (inclusive)
    assertEquals(6, result.totalDaysWorked());
  }

  @Test
  @DisplayName("Should throw InvalidCsvException for invalid file")
  void testInvalidFileThrowsException() throws Exception {
    when(mockFile.getInputStream()).thenThrow(new RuntimeException("File read error"));

    assertThrows(InvalidCsvException.class, () -> {
      employeeService.getEmployeePairsFromCsv(mockFile);
    });
  }

  @Test
  @DisplayName("Should handle CSV with extra whitespace")
  void testCSVWithExtraWhitespace() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "  1  ,  1  ,  2020-01-01  ,  2020-01-10  \n" +
        "  2  ,  1  ,  2020-01-05  ,  2020-01-15  \n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    assertEquals(1L, result.employee1Id());
    assertEquals(2L, result.employee2Id());
  }

  @Test
  @DisplayName("Should find correct pair among multiple candidates")
  void testFindCorrectPairAmongMultipleCandidates() throws Exception {
    String csvContent = "EmpID,ProjectID,DateFrom,DateTo\n" +
        "1,1,2020-01-01,2020-01-10\n" +
        "2,1,2020-01-02,2020-01-05\n" +
        "3,1,2020-01-03,2020-01-20\n";

    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    EmployeePairResult result = employeeService.getEmployeePairsFromCsv(mockFile);

    assertNotNull(result);
    // Employees 1 and 3 have the most overlap (8 days: Jan 3-10)
    assertEquals(1L, result.employee1Id());
    assertEquals(3L, result.employee2Id());
    assertEquals(8, result.totalDaysWorked());
  }
}
