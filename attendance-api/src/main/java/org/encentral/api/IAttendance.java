package org.encentral.api;

import entities.JpaAttendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IAttendance {
    CompletionStage<JpaAttendance> save (JpaAttendance attendance);

    CompletionStage<Optional<List<JpaAttendance>>> findByEmployeeAndDate (LocalDate date);
}
