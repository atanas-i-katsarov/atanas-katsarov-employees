package com.atanaskatsarov.employees.controller;

import com.atanaskatsarov.employees.exception.InvalidCsvException;
import com.atanaskatsarov.employees.model.EmployeePairResult;
import com.atanaskatsarov.employees.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/upload")
    public ResponseEntity<EmployeePairResult> uploadEmployeeCsvFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidCsvException("CSV file is required");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("text/csv") && !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new InvalidCsvException("Uploaded file must be a CSV");
        }

        try {
            // For now only validate presence and basic type; processing can be delegated to employeeService
            byte[] data = file.getBytes();
            if (data.length == 0) {
                throw new InvalidCsvException("CSV file is empty");
            }
        } catch (IOException e) {
            throw new InvalidCsvException("Failed to read uploaded file");
        }

        EmployeePairResult result = employeeService.getEmployeePairsFromCsv(file);
        return ResponseEntity.ok().body(result);
    }

}
