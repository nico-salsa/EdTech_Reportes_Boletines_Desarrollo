package com.edtech.app.report;

import com.edtech.app.auth.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses/{courseId}/students/{studentId}/report")
public class ReportController {

    private final AuthService authService;
    private final ReportService reportService;

    public ReportController(AuthService authService, ReportService reportService) {
        this.authService = authService;
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<byte[]> export(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable String courseId,
            @PathVariable String studentId,
            @RequestParam String format
    ) {
        ReportService.ExportedReport report = reportService.exportStudentReport(
                authService.requireUser(token),
                courseId,
                studentId,
                format
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, report.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.filename() + "\"")
                .body(report.content());
    }
}
