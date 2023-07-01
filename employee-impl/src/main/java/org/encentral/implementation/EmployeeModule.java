package org.encentral.implementation;

import com.google.inject.AbstractModule;
import org.encentral.api.IEmployee;

public class EmployeeModule extends AbstractModule {
    @Override
    protected void configure(){
        bind(IEmployee.class).to(EmployeeImpl.class);
    }
}
