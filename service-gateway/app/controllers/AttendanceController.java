package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import entities.Role;
import org.encentral.model.Attendance;
import org.encentral.model.Employee;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import services.AttendanceService;
import services.EmployeeService;
import utils.Helper;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.*;

public class AttendanceController {
    private HttpExecutionContext executionContext;
    private EmployeeService employeeService;
    private AttendanceService attendanceService;
    private EmployeeController employeeController;

    @Inject
    public AttendanceController(HttpExecutionContext executionContext, EmployeeService employeeService, AttendanceService attendanceService){
        this.executionContext = executionContext;
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
    }

    public CompletionStage<Result> getEmployeeDailyAttendance(Http.Request request){

        Employee authenticatedEmployeeemployee = employeeController.getAuthenticatedEmployee(request);

        if (authenticatedEmployeeemployee != null){
            if (authenticatedEmployeeemployee.getRole() != Role.ADMIN){
                return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Only an admin can make this request", false)));
            }

            LocalDate localDate = LocalDate.now();
            CompletionStage<Optional<List<Attendance>>> optionalCompletionStage = attendanceService.findByEmployeeAndDate(localDate);
            return optionalCompletionStage.thenApplyAsync(optionalAttendanceStream -> {
                if (optionalAttendanceStream.isPresent()) {
                    List<Attendance> attendanceList = optionalAttendanceStream.get();
                    return ok(Json.toJson(attendanceList));
                } else {
                    return notFound(Helper.createResponse("Attendance records not found", false));
                }
            });
        } else {
            return CompletableFuture.completedFuture(unauthorized(Helper.createResponse("Invalid token", false)));
        }
    }

    public CompletionStage<Result> markAttendance(Http.Request request){

        Employee authenticatedEmployeeemployee = employeeController.getAuthenticatedEmployee(request);

        if (authenticatedEmployeeemployee != null){
            JsonNode jsonData = request.body().asJson();
            if (!jsonData.has("employeeId")){
                return CompletableFuture.completedFuture(badRequest("EmployeeId is not specified"));
            } else {
                Long employeeId = jsonData.get("employeeId").asLong();
                Attendance attendanceDTO = new Attendance();
                LocalDateTime currentTime = LocalDateTime.now();

                if (!Helper.isWorkingDay(currentTime)) {
                    return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Attendance cannot be marked on non-working days", true)));
                }

                if (!Helper.isWorkingHours(currentTime)) {
                    return CompletableFuture.completedFuture((badRequest(Helper.createResponse("Attendance cannot be marked outside working hours", false))));
                }

                attendanceDTO.setDate(LocalDate.now());
                attendanceDTO.setTimeIn(LocalDateTime.now());
                CompletionStage<Optional<Employee>> employeeOptional = employeeService.findEmployeeById(employeeId);
                return employeeOptional.thenComposeAsync(optionalEmployeeDTO -> {
                    if (optionalEmployeeDTO.isPresent()){
                        attendanceDTO.setEmployeeDTO(optionalEmployeeDTO.get());
//                        return CompletableFuture.completedFuture(created(Helper.createResponse(Json.toJson(attendanceDTO), true)));
                        return attendanceService.save(attendanceDTO).thenApplyAsync(savedData -> created(Helper.createResponse(Json.toJson(savedData), true)));
                    } else {
                        return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Employee does not exit", false)));
                    }
                }, executionContext.current());
            }

        } else {
            return  CompletableFuture.completedFuture(unauthorized(Helper.createResponse("Invalid token", false)));
        }
    }
}
