package com.edtech.app.course;

import com.edtech.app.auth.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class CourseController {

    private final AuthService authService;
    private final CourseService courseService;

    public CourseController(AuthService authService, CourseService courseService) {
        this.authService = authService;
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public List<CourseService.CourseListItem> listCourses(@RequestHeader("X-Session-Token") String token) {
        return courseService.listCourses(authService.requireUser(token));
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseService.CourseDetailResponse> createCourse(
            @RequestHeader("X-Session-Token") String token,
            @Valid @RequestBody CreateCourseRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(authService.requireUser(token), request.name()));
    }

    @GetMapping("/courses/{courseId}")
    public CourseService.CourseDetailResponse getCourse(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable String courseId
    ) {
        return courseService.getCourse(authService.requireUser(token), courseId);
    }

    @GetMapping("/students/{studentId}")
    public CourseService.StudentSummary findStudent(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable String studentId
    ) {
        authService.requireUser(token);
        return courseService.findStudentByIdentifier(studentId);
    }

    @PostMapping("/courses/{courseId}/students")
    public CourseService.CourseDetailResponse addStudent(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable String courseId,
            @Valid @RequestBody AddStudentBody request
    ) {
        return courseService.addStudentToCourse(
                authService.requireUser(token),
                courseId,
                new CourseService.AddStudentRequest(request.studentId(), request.name(), request.email())
        );
    }

    @PutMapping("/courses/{courseId}/activities")
    public CourseService.CourseDetailResponse updateActivities(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable String courseId,
            @RequestBody List<ActivityBody> activities
    ) {
        List<CourseService.ActivityInput> payload = activities.stream()
                .map(activity -> new CourseService.ActivityInput(activity.id(), activity.name(), activity.percentage()))
                .toList();
        return courseService.updateActivities(authService.requireUser(token), courseId, payload);
    }

    @PutMapping("/courses/{courseId}/grades")
    public CourseService.CourseDetailResponse updateGrade(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable String courseId,
            @Valid @RequestBody GradeBody request
    ) {
        return courseService.updateGrade(
                authService.requireUser(token),
                courseId,
                new CourseService.GradeInput(request.studentId(), request.activityId(), request.grade())
        );
    }

    @DeleteMapping("/courses/{courseId}/activities/{activityId}")
    public CourseService.CourseDetailResponse deleteActivity(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable String courseId,
            @PathVariable String activityId
    ) {
        return courseService.deleteActivity(authService.requireUser(token), courseId, activityId);
    }

    public record CreateCourseRequest(@NotBlank String name) {
    }

    public record AddStudentBody(@NotBlank String studentId, @NotBlank String name, @NotBlank String email) {
    }

    public record ActivityBody(String id, @NotBlank String name, Double percentage) {
    }

    public record GradeBody(@NotBlank String studentId, @NotBlank String activityId, Double grade) {
    }
}
