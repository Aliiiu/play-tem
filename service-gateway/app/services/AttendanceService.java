package services;

import entities.JpaAttendance;
import org.encentral.implementation.AttendanceImpl;
import org.encentral.model.Attendance;
import org.encentral.model.AttendanceMapper;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class AttendanceService {
    private AttendanceImpl attendanceRepository;
    private HttpExecutionContext executionContext;

    @Inject
    public AttendanceService (AttendanceImpl attendanceRepository, HttpExecutionContext executionContext){
        this.attendanceRepository = attendanceRepository;
        this.executionContext = executionContext;
    }

    public CompletionStage<Attendance> save(Attendance attendance){
        JpaAttendance jpaAttendance = AttendanceMapper.fromAttendance(attendance);
        return attendanceRepository.save(jpaAttendance).thenApplyAsync(data -> {
            return AttendanceMapper.toAttendance(data);
        }, executionContext.current());
    }

    public CompletionStage<Optional<List<Attendance>>> findByEmployeeAndDate(LocalDate date){

        return attendanceRepository.findByEmployeeAndDate(date).thenApplyAsync(optionalData -> {
            if (optionalData.isEmpty()){
                return Optional.empty();
            } else {
                return optionalData.map(dataStream -> AttendanceMapper.toAttendanceList(dataStream));
            }
        }, executionContext.current());
    }
}
