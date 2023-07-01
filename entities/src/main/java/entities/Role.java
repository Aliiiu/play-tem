package entities;

public enum Role {
    ADMIN,
    EMPLOYEE;

    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public static Role fromString(String role){
        switch(role.toLowerCase()){
            case "employee":
                return EMPLOYEE;
            case "admin":
                return ADMIN;
            default:
                return null;
        }
    }
}





