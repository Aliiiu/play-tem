package org.encentral.implementation;

import com.google.inject.AbstractModule;
import org.encentral.api.IAttendance;
import org.encentral.api.IEmployee;

public class AttendanceModule extends AbstractModule {
    @Override
    protected void configure(){
        bind(IAttendance.class).to(AttendanceImpl.class);
    }
}
