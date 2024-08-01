package im.crossim.user.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SetUserDeviceDto {

    @NotNull
    private Integer vendor;

    @NotEmpty
    private String deviceId;

}
