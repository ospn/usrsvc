package im.crossim.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("发送验证码操作参数")
@Data
public class SendCodeDto {

    @ApiModelProperty("目标")
    @NotNull(message = "param target cannot be null")
    private String target;

    @ApiModelProperty("验证码类型，1=注册；2=登录；3=找回密码")
    @NotNull(message = "param type cannot be null")
    private Integer type;

}
