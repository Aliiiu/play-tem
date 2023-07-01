package org.encentral.implementation;

import entities.JpaAttendance;
import org.encentral.api.IAttendance;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class AttendanceImpl implements IAttendance {
    private JPAApi jpaApi;
    private DatabaseExecutionContext executionContext;
//    private final CircuitBreaker<Optional<JpaAttendance>> circuitBreaker = new CircuitBreaker<Optional<JpaAttendance>>().withFailureThreshold(1).withSuccessThreshold(3);

    @Inject
    public AttendanceImpl(JPAApi jpaApi, DatabaseExecutionContext executionContext){
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<JpaAttendance> save (JpaAttendance attendance){
        return supplyAsync(() -> wrap(em -> insert(em, attendance)), executionContext);
    }

    @Override
    public CompletionStage<Optional<List<JpaAttendance>>> findByEmployeeAndDate (LocalDate date){
        return CompletableFuture.supplyAsync(() -> {
            try {
                return wrap(em -> search(em, date));
            } catch (NoResultException e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Optional<JpaAttendance> lookup(EntityManager em, Long id) throws SQLException {
        return Optional.ofNullable(em.find(JpaAttendance.class, id));
    }

    private Optional<List<JpaAttendance>> search(EntityManager em, LocalDate date) {
        TypedQuery<JpaAttendance> query = em
                .createQuery("SELECT a  FROM Attendance a WHERE a.date = :date", JpaAttendance.class)
                .setParameter("date", date);
        return Optional.ofNullable(query.getResultList());
    }

    private Stream<JpaAttendance> select(EntityManager em) {
        TypedQuery<JpaAttendance> query = em.createQuery("SELECT e FROM Employee e", JpaAttendance.class);
        return query.getResultList().stream();
    }

    private JpaAttendance insert(EntityManager em, JpaAttendance attendance) {
        return em.merge(attendance);
    }

    private void delete(EntityManager em, JpaAttendance attendance){
        em.remove(attendance);
    }
}
