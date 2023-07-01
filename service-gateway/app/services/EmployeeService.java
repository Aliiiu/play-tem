package services;

import entities.JpaEmployee;
import org.encentral.implementation.EmployeeImpl;
import org.encentral.model.Employee;
import org.encentral.model.EmployeeMapper;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class EmployeeService {
    private EmployeeImpl employeeRepository;
    private HttpExecutionContext executionContext;

    @Inject
    public EmployeeService(EmployeeImpl employeeRepository, HttpExecutionContext executionContext){
        this.employeeRepository = employeeRepository;
        this.executionContext = executionContext;
    }

    public CompletionStage<Employee> create(Employee employee){
        JpaEmployee jpaEmployee = EmployeeMapper.fromEmployee(employee);
        return employeeRepository.create(jpaEmployee).thenApplyAsync(savedData -> {
            return EmployeeMapper.toEmployee(savedData);
        }, executionContext.current());
    }

    public CompletionStage<Stream<Employee>> getAllEmployees(){
        return employeeRepository.getAllEmployees().thenApplyAsync(dataStream -> {
            return dataStream.map(data -> EmployeeMapper.toEmployee(data));
        }, executionContext.current());
    }

    public CompletionStage<Optional<Employee>> findEmployeeById(Long id){
        return employeeRepository.findEmployeeById(id).thenApplyAsync(optionalData -> {
            return optionalData.map(data -> EmployeeMapper.toEmployee(data));
        }, executionContext.current());
    }

    public CompletionStage<Optional<Employee>> findEmployeeByEmail(String email){
        return employeeRepository.findEmployeeByEmail(email).thenApplyAsync(optionalData -> {
            if (optionalData.isEmpty()) {
                return Optional.empty();
            } else {
                return optionalData.map(data -> EmployeeMapper.toEmployee(data));
            }
        }, executionContext.current());
    }

    public Employee findEmployeeByToken(String token){
        return EmployeeMapper.toEmployee(employeeRepository.findEmployeeByToken(token));
    }

    public CompletionStage<Optional<Employee>> update(Employee employeeDTO){
        JpaEmployee jpaEmployee = EmployeeMapper.fromEmployee(employeeDTO);
        return employeeRepository.update(jpaEmployee.getEmployeeId(), jpaEmployee).thenApplyAsync(optionalData ->{
            return optionalData.map(data -> EmployeeMapper.toEmployee(data));
        }, executionContext.current());
    }

    public void delete(Long id){
        employeeRepository.deleteEmployee(id);
    }
}
