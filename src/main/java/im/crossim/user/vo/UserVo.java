package im.crossim.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("用户信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("电子邮箱")
    private String email;

    @ApiModelProperty("OSN ID")
    private String osnId;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("用户类型，1=管理员（未使用）；2=代理商（未使用）；3=普通用户")
    private Integer type;

    @ApiModelProperty("头像URL")
    private String portrait;

    @ApiModelProperty("VIP等级")
    private Integer vipLevel;

    @ApiModelProperty("私钥")
    private String privateKey;

}
