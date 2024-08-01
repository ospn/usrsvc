package im.crossim.user.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginWithDoubleKeyDto {

    @NotEmpty
    private String username;

    @NotEmpty
    private String userOsnId;

    @NotEmpty
    private String shadowId;

    @NotEmpty
    private String timestamp;

    @NotEmpty
    private String signOsnId;

    @NotEmpty
    private String signShadowId;

}
