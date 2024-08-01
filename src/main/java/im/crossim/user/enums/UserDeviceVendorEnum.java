package im.crossim.user.enums;

public enum UserDeviceVendorEnum {
    XIAOMI(1, "小米"),
    VIVO(2, "Vivo"),
    HUAWEI(3, "华为"),
    ;

    private final int value;
    private final String name;

    UserDeviceVendorEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
