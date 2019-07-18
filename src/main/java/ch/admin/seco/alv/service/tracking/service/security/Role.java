package ch.admin.seco.alv.service.tracking.service.security;

public enum Role {

    ADMIN("ROLE_ADMIN"),
    SYSADMIN("ROLE_SYSADMIN");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
