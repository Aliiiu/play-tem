package controllers;

import org.encentral.model.Employee;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.EmployeeService;
import utils.Helper;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AdminAuthenticator extends Security.Authenticator {
    private EmployeeService employeeService;

    @Inject
    public AdminAuthenticator(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    public String getAdminName(Http.RequestHeader request){
        String tokenHeader = request.header("Authorization").orElse("");
        String token = tokenHeader.replace("Bearer ", "").trim();

        Employee employee = employeeService.findEmployeeByToken(token);
        return employee.getFirstName();
    }

    public Result onUnauthorized(Http.RequestHeader request) {
        return forbidden("You are not authorized to access this resource");
    }

}
