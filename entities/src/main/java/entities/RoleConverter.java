package entities;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null){
            return null;
        }
        for (Role rolevalue : Role.values()){
            if (rolevalue.getRoleName().equalsIgnoreCase(role.getRoleName())){
                return role.name().toLowerCase();
            }
        }
        throw new IllegalArgumentException("Unsupported role: " + role);
    }

    @Override
    public Role convertToEntityAttribute(String roleName) {
        for (Role role : Role.values()) {
            if (role.getRoleName().toLowerCase().equals(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unsupported role: " + roleName);
    }
}


