package org.encentral.api;

import entities.JpaEmployee;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface IEmployee {
    CompletionStage<Stream<JpaEmployee>> getAllEmployees();
    CompletionStage<JpaEmployee> create(JpaEmployee employee);
    CompletionStage<Optional<JpaEmployee>> findEmployeeById(Long id);
    CompletionStage<Optional<JpaEmployee>> findEmployeeByEmail(String email);
    CompletionStage<Optional<JpaEmployee>> update(Long id, JpaEmployee employeeData);
    JpaEmployee findEmployeeByToken(String token);
    CompletionStage<Void> deleteEmployee(Long id);
}
