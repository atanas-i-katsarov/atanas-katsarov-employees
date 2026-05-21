package com.atanaskatsarov.employees.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DateFormatUtil Tests")
class DateFormatUtilTests {

  @Test
  @DisplayName("Should parse ISO format (yyyy-MM-dd)")
  void testParseISOFormat() {
    LocalDate result = DateFormatUtil.parseDate("2020-01-15");
    assertEquals(LocalDate.of(2020, 1, 15), result);
  }

  @Test
  @DisplayName("Should parse dd/MM/yyyy format")
  void testParseDdMmYyyySlashFormat() {
    LocalDate result = DateFormatUtil.parseDate("15/01/2020");
    assertEquals(LocalDate.of(2020, 1, 15), result);
  }

  @Test
  @DisplayName("Should parse MM/dd/yyyy format")
  void testParseMmDdYyyySlashFormat() {
    LocalDate result = DateFormatUtil.parseDate("01/15/2020");
    assertEquals(LocalDate.of(2020, 1, 15), result);
  }

  @Test
  @DisplayName("Should parse dd-MM-yyyy format")
  void testParseDdMmYyyyDashFormat() {
    LocalDate result = DateFormatUtil.parseDate("15-01-2020");
    assertEquals(LocalDate.of(2020, 1, 15), result);
  }

  @Test
  @DisplayName("Should parse MM-dd-yyyy format")
  void testParseMmDdYyyyDashFormat() {
    LocalDate result = DateFormatUtil.parseDate("01-15-2020");
    assertEquals(LocalDate.of(2020, 1, 15), result);
  }

  @Test
  @DisplayName("Should parse yyyy/MM/dd format")
  void testParseYyyyMmDdSlashFormat() {
    LocalDate result = DateFormatUtil.parseDate("2020/01/15");
    assertEquals(LocalDate.of(2020, 1, 15), result);
  }

  @Test
  @DisplayName("Should parse ddMMyyyy format")
  void testParseDdMmYyyyConcatenatedFormat() {
    LocalDate result = DateFormatUtil.parseDate("15012020");
    assertEquals(LocalDate.of(2020, 1, 15), result);
  }

  @Test
  @DisplayName("Should parse MMddyyyy format")
  void testParseMmDdYyyyConcatenatedFormat() {
    LocalDate result = DateFormatUtil.parseDate("01152020");
    assertEquals(LocalDate.of(2020, 1, 15), result);
  }

  @Test
  @DisplayName("Should return current date for null input")
  void testParseNullReturnsCurrentDate() {
    LocalDate result = DateFormatUtil.parseDate(null);
    assertEquals(LocalDate.now(), result);
  }

  @Test
  @DisplayName("Should return current date for blank input")
  void testParseBlankReturnsCurrentDate() {
    LocalDate result = DateFormatUtil.parseDate("   ");
    assertEquals(LocalDate.now(), result);
  }

  @Test
  @DisplayName("Should throw exception for invalid date format")
  void testParseInvalidFormatThrowsException() {
    assertThrows(DateTimeParseException.class, () -> {
      DateFormatUtil.parseDate("invalid-date");
    });
  }

  @Test
  @DisplayName("Should throw exception for invalid date values")
  void testParseInvalidDateValuesThrowsException() {
    assertThrows(DateTimeParseException.class, () -> {
      DateFormatUtil.parseDate("2020-13-01"); // Invalid month
    });
  }

  @Test
  @DisplayName("Should throw exception for invalid day of month")
  void testParseInvalidDayThrowsException() {
    assertThrows(DateTimeParseException.class, () -> {
      DateFormatUtil.parseDate("2020-02-30"); // Feb 30 doesn't exist
    });
  }

  @Test
  @DisplayName("Should handle leap year date")
  void testParseLeapYearDate() {
    LocalDate result = DateFormatUtil.parseDate("2020-02-29");
    assertEquals(LocalDate.of(2020, 2, 29), result);
  }

  @Test
  @DisplayName("Should handle empty string")
  void testParseEmptyString() {
    LocalDate result = DateFormatUtil.parseDate("");
    assertEquals(LocalDate.now(), result);
  }
}
