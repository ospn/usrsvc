package im.crossim.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@ApiModel("拉取配置操作数据")
@Data
@Builder
public class FetchConfigVo {

    @ApiModelProperty("登录配置")
    private LoginConfigVo loginConfig;

    @ApiModelProperty("敏感词配置")
    private SensitiveKeywordConfigVo sensitiveKeywordConfig;

    @ApiModelProperty("小程序黑名单配置")
    private DappBlackListConfigVo dappBlackListConfig;

}
