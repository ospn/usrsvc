package im.crossim.user.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetLoginInfoDto {

    @NotEmpty
    private String userOsnId;

    @NotEmpty
    private String random;

}
