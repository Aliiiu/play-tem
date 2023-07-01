package org.encentral.implementation;

import entities.JpaEmployee;
import org.encentral.api.IEmployee;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;
import play.db.jpa.JPAApi;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Singleton
public class EmployeeImpl implements IEmployee {
    private JPAApi jpaApi;
    private DatabaseExecutionContext executionContext;
//    private final CircuitBreaker<Optional<JpaEmployee>> circuitBreaker = new CircuitBreaker<Optional<JpaEmployee>>().withFailureThreshold(1).withSuccessThreshold(3);

    @Inject
    public EmployeeImpl(JPAApi jpaApi, DatabaseExecutionContext executionContext){
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Stream<JpaEmployee>> getAllEmployees(){
        return supplyAsync(() -> wrap(this::select), executionContext);
    }

    @Override
    public CompletionStage<JpaEmployee> create (JpaEmployee employeeData){
        return supplyAsync(() -> wrap(entityManager -> insert(entityManager, employeeData)), executionContext);
    }

    @Override
    public CompletionStage<Optional<JpaEmployee>> update(Long id, JpaEmployee employeeData){
        return supplyAsync(() -> wrap(entityManager -> modify(entityManager, id, employeeData)), executionContext);
    }

    @Override
    public CompletionStage<Optional<JpaEmployee>> findEmployeeById(Long id) {
        return supplyAsync(() -> wrap(em -> lookup(em, id)), executionContext);
    }

    @Override
    public CompletionStage<Optional<JpaEmployee>> findEmployeeByEmail(String email) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return wrap(em -> search(em, email));
            } catch (NoResultException e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    @Override
    public JpaEmployee findEmployeeByToken(String token){
        return wrap(entityManager -> searchToken(entityManager, token));
    }

    @Override
    public CompletionStage<Void> deleteEmployee(Long id){
        CompletionStage<Void> completionStage = supplyAsync(() -> wrap(entityManager -> {
            JpaEmployee employee = entityManager.find(JpaEmployee.class, id);
            if (employee != null){
                delete(entityManager, employee);
            }
            return null;
        }), executionContext);

        return completionStage;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Optional<JpaEmployee> lookup(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(JpaEmployee.class, id));
    }

    private Optional<JpaEmployee> search(EntityManager em, String email) {
        TypedQuery<JpaEmployee> query = em
                .createQuery("SELECT e FROM Employee e WHERE e.email = :email", JpaEmployee.class)
                .setParameter("email", email);
        return Optional.ofNullable(query.getSingleResult());
    }

    private JpaEmployee searchToken(EntityManager em, String token) {
        TypedQuery<JpaEmployee> query = em
                .createQuery("SELECT e FROM Employee e WHERE e.token = :token", JpaEmployee.class)
                .setParameter("token", token);
        return query.getSingleResult();
    }

    private Stream<JpaEmployee> select(EntityManager em) {
        TypedQuery<JpaEmployee> query = em.createQuery("SELECT e FROM Employee e", JpaEmployee.class);
        return query.getResultList().stream();
    }

    private Optional<JpaEmployee> modify(EntityManager em, Long id, JpaEmployee employee) {
        JpaEmployee data = em.find(JpaEmployee.class, id);
        if (data != null) {
            data.setPassword(employee.getPassword());
            data.setToken(employee.getToken());
            em.merge(data);
        }
        return Optional.ofNullable(data);
    }

    private JpaEmployee insert(EntityManager em, JpaEmployee postData) {
        return em.merge(postData);
    }

    private void delete(EntityManager em, JpaEmployee employeeData){
        em.remove(employeeData);
    }
}
