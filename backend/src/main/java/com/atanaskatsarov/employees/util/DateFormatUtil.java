package com.atanaskatsarov.employees.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateFormatUtil {
    
    private static final List<DateTimeFormatter> SUPPORTED_FORMATS = Arrays.asList(
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("MM-dd-yyyy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("ddMMyyyy"),
        DateTimeFormatter.ofPattern("MMddyyyy")
    );

    public static LocalDate parseDate(String dateString) throws DateTimeParseException {
        if (dateString == null || dateString.isBlank()) {
            return LocalDate.now();
        }
        
        for (DateTimeFormatter formatter : SUPPORTED_FORMATS) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // Try next format
                log.trace("Format {} did not match", formatter);
            }
        }
        
        throw new DateTimeParseException("Unable to parse date: " + dateString, dateString, 0);
    }
}
