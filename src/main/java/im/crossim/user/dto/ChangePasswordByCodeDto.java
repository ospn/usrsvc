package im.crossim.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("找回密码参数")
@Data
public class ChangePasswordByCodeDto {

    @ApiModelProperty("目标")
    @NotBlank(message = "param target cannot be blank")
    private String target;

    @ApiModelProperty("新密码")
    @NotBlank(message = "param newPassword cannot be blank")
    private String newPassword;

    @ApiModelProperty("验证码")
    @NotBlank(message = "param code cannot be blank")
    private String code;

}
