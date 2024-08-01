package im.crossim.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("注册信息")
@Data
public class RegisterDto {

    @ApiModelProperty("注册类型，1=用户名注册；2=手机号码注册；3=电子邮箱注册")
    @NotNull(message = "param type cannot be null")
    private Integer type;

    @ApiModelProperty("用户名，用户名注册时必填")
    private String username;

    @ApiModelProperty("手机号码，手机号码注册时必填")
    private String mobile;

    @ApiModelProperty("电子邮箱，电子邮箱注册时必填")
    private String email;

    @ApiModelProperty("密码")
    @NotBlank(message = "param password cannot be blank")
    private String password;

    @ApiModelProperty("昵称")
    @NotBlank(message = "param nickname cannot be blank")
    private String nickname;

    @ApiModelProperty("项目，暂未使用，可填写空字符串")
    private String project;

    @ApiModelProperty("头像URL")
    private String portrait;

    @ApiModelProperty("验证码，手机号码注册时和电子邮箱注册时必填")
    private String code;

}
