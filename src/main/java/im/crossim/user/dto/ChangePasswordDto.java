package im.crossim.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("修改密码参数")
@Data
public class ChangePasswordDto {

    @ApiModelProperty("当前密码")
    @NotBlank(message = "param currentPassword cannot be blank")
    private String currentPassword;

    @ApiModelProperty("新密码")
    @NotBlank(message = "param newPassword cannot be blank")
    private String newPassword;

}
