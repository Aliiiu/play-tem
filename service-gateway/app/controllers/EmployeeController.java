package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import entities.Role;
import org.encentral.model.Employee;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import services.AttendanceService;
import services.EmployeeService;
import utils.Helper;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.mvc.Results.*;

public class EmployeeController {
    private HttpExecutionContext executionContext;
    private EmployeeService employeeService;

    @Inject
    public EmployeeController(HttpExecutionContext executionContext, EmployeeService employeeService, AttendanceService attendanceService){
        this.executionContext = executionContext;
        this.employeeService = employeeService;
    }

    public CompletionStage<Result> removeEmployee(Http.Request request , int id){
        Employee employee = getAuthenticatedEmployee(request);

        if (employee != null){
            if (employee.getRole() != Role.ADMIN){
                return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Only an admin can make this request", false)));
            }
            CompletionStage<Optional<Employee>> optionalCompletionStage = employeeService.findEmployeeById(Long.parseLong(String.valueOf(id)));
            return optionalCompletionStage.thenComposeAsync(optionalEmployeeDTO -> {
                if (optionalEmployeeDTO.isPresent()){
                    if (optionalEmployeeDTO.get().getRole() == Role.EMPLOYEE){
                        return CompletableFuture.completedFuture(notFound(Helper.createResponse("Only an admin can remove employee", false)));
                    }
                    employeeService.delete(Long.parseLong(String.valueOf(id)));
                    return CompletableFuture.completedFuture(ok(Helper.createResponse("Employee deleted successfully", true)));
                } else {
                    return CompletableFuture.completedFuture(notFound(Helper.createResponse("Employee not found", false)));
                }
            }, executionContext.current());
        } else {
            return CompletableFuture.completedFuture(unauthorized(Helper.createResponse("Invalid token", false)));
        }
    }

    public CompletionStage<Result> addEmployee(Http.Request request){
        Employee authenticatedEmployee = getAuthenticatedEmployee(request);

        if (authenticatedEmployee != null){

            if (authenticatedEmployee.getRole() != Role.ADMIN){
                return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Only an admin can make this request", false)));
            }

            JsonNode jsonData = request.body().asJson();
            if (!(jsonData.has("firstName") && jsonData.has("lastName") && jsonData.has("role") && jsonData.has("email"))){
                return CompletableFuture.completedFuture(badRequest(Helper.createResponse("One or more of this field is missing: firstname, lastname, role, email", false)));
            }

            Employee employee = new Employee();
            employee.setFirstName(jsonData.get("firstName").asText());
            employee.setLastName(jsonData.get("lastName").asText());
            employee.setEmail(jsonData.get("email").asText());
            employee.setRole(Role.fromString(jsonData.get("role").asText().toLowerCase()));
            employee.setCreatedAt(LocalDateTime.now());
            if(employee.getRole() == null){
                return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Role does not exist", false)));
            }
            CompletionStage<Optional<Employee>> employeeOptional = employeeService.findEmployeeByEmail(employee.getEmail());
            return employeeOptional.thenComposeAsync(optionalEmployeeDTO -> {
                if (optionalEmployeeDTO.isPresent()) {
                    return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Email already exists", false)));
                } else {
                    employee.setPassword(Helper.generatePin());
                    return employeeService.create(employee)
                            .thenApplyAsync(savedData -> created(Helper.createResponse(Json.toJson(savedData), true)), executionContext.current());
                }
            }, executionContext.current());
        } else {
            return CompletableFuture.completedFuture(unauthorized(Helper.createResponse("Invalid token", false)));
        }

    }

    public CompletionStage<Result> updatePassword(Http.Request request){
        Employee authenticatedEmployee = getAuthenticatedEmployee(request);

        if (authenticatedEmployee != null){
            if (authenticatedEmployee.getRole() != Role.ADMIN){
                return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Cant change admin password", false)));
            }

            JsonNode jsonData = request.body().asJson();

            String email = jsonData.get("email").asText();
            String password = jsonData.get("password").asText();
            CompletionStage<Optional<Employee>> employeeOptional = employeeService.findEmployeeByEmail(email);
            return employeeOptional.thenComposeAsync(employeeOptionalDTO -> {
                if (employeeOptionalDTO.isPresent()){
                    Employee employee = employeeOptionalDTO.get();
                    if (employee.getPassword().equals(password)){
                        return CompletableFuture.completedFuture(badRequest(Helper.createResponse("enter a new password", false)));
                    } else {
                        employee.setPassword(password);
                        return employeeService.update(employee).thenComposeAsync(optionalData -> {
                            if (optionalData.isPresent()){
                                return CompletableFuture.completedFuture(ok(Helper.createResponse("Password updated successfully", true)));
                            } else {
                                return CompletableFuture.completedFuture(internalServerError(Helper.createResponse("Error changing password", false)));
                            }
                        }, executionContext.current());
                    }
                } else {
                    return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Invalid Credentials", false)));
                }
            }, executionContext.current());
        } else {
            return CompletableFuture.completedFuture(unauthorized(Helper.createResponse("Invalid token", false)));
        }
    }

    Employee getAuthenticatedEmployee(Http.Request request) {
        String tokenHeader = request.header("Authorization").orElse("");
        String token = tokenHeader.replace("Bearer ", "").trim();

        if (token.isEmpty()) {
            return null;
        }

        return employeeService.findEmployeeByToken(token);
    }

    public CompletionStage<Result> signIn(Http.Request request) {
        JsonNode jsonData = request.body().asJson();
        if (!jsonData.has("password")){
            return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Provide a valid password", false)));
        } else {
            String email = jsonData.get("email").asText();
            String password = jsonData.get("password").asText();
            CompletionStage<Optional<Employee>> employeeOptional = employeeService.findEmployeeByEmail(email);
            return employeeOptional.thenComposeAsync(employeeOptionalDTO -> {
                if (employeeOptionalDTO.isPresent()) {
                    Employee employee = employeeOptionalDTO.get();
                    if (employee.getPassword().equals(password)) {
                        String token = UUID.randomUUID().toString();
                        employee.setToken(token);
                        return employeeService.update(employee).thenApplyAsync(optionData -> {
                            if (optionData.isPresent()) {
                                return created(Helper.createResponse(Json.toJson(optionData.get()), true));
                            } else {
                                return internalServerError(Helper.createResponse("Error updating employee", false));
                            }
                        }, executionContext.current());
                    } else {
                        return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Wrong password", false)));
                    }
                } else {
                    return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Invalid Credentials", false)));
                }
            }, executionContext.current());
        }
    }

    public CompletionStage<Result> getAllEmployees(Http.Request request){
        Employee employee = getAuthenticatedEmployee(request);

        if (employee != null){
            if (employee.getRole() != Role.ADMIN){
                return CompletableFuture.completedFuture(badRequest(Helper.createResponse("Only an admin can make this request", false)));
            }
            return employeeService.getAllEmployees().thenApplyAsync(employeeDTOStream -> {
                List<Employee> employeeDTOList = employeeDTOStream.collect(Collectors.toList());
                return ok(Helper.createResponse(Json.toJson(employeeDTOList), true));
            }, executionContext.current());
        } else {
            return CompletableFuture.completedFuture(unauthorized(Helper.createResponse("Invalid token", false)));
        }
    }

    public CompletionStage<Result> getEmployee(Http.Request request , int id){
        Employee authenticatedEmployee = getAuthenticatedEmployee(request);

        if (authenticatedEmployee != null){
            CompletionStage<Optional<Employee>> optionalCompletionStage = employeeService.findEmployeeById(Long.parseLong(String.valueOf(id)));
            return optionalCompletionStage.thenComposeAsync(optionalEmployeeDTO -> {
                if (optionalEmployeeDTO.isPresent()){
                    Employee employee = optionalEmployeeDTO.get();
                    return CompletableFuture.completedFuture(ok(Helper.createResponse(Json.toJson(employee), true)));
                } else {
                    return CompletableFuture.completedFuture(notFound(Helper.createResponse("Employee not found", false)));
                }
            }, executionContext.current());
        } else {
            return CompletableFuture.completedFuture(unauthorized(Helper.createResponse("Invalid token", false)));
        }
    }
}
