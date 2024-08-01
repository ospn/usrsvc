package im.crossim.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("登录信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {

    @ApiModelProperty("登录Token")
    private String token;

    @ApiModelProperty("刷新Token")
    private String refreshToken;

    @ApiModelProperty("用户信息")
    private UserVo userInfo;

    @ApiModelProperty("登录配置")
    private LoginConfigVo loginConfig;

    @ApiModelProperty("敏感关键字配置")
    private SensitiveKeywordConfigVo sensitiveKeywordConfig;

    @ApiModelProperty("DAPP黑名单配置")
    private DappBlackListConfigVo dappBlackListConfig;

}
