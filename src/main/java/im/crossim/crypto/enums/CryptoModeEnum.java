package im.crossim.crypto.enums;

public enum CryptoModeEnum {

    NONE(0, "无加密"),
    BASE64(1, "Base64"),
    AES(2, "AES"),
    ;

    private final int value;
    private final String name;

    CryptoModeEnum(int value, String name) {
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
