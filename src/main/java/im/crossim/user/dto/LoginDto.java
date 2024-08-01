package im.crossim.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("登录信息")
@Data
public class LoginDto {

    @ApiModelProperty("登录类型，1=密码登录；2=手机登录；3=邮箱登录")
    @NotNull(message = "param type cannot be null")
    private Integer type;

    @ApiModelProperty("用户名，密码登录必填")
    private String username;

    @ApiModelProperty("手机号码，手机登录必填")
    private String mobile;

    @ApiModelProperty("电子邮箱，邮箱登录必填")
    private String email;

    @ApiModelProperty("密码，密码登录必填")
    private String password;

    @ApiModelProperty("验证码，手机登录与邮箱登录必填")
    private String code;

    @ApiModelProperty("项目")
    private String project;

}
