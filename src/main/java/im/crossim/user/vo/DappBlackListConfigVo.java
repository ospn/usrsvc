package im.crossim.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("DAPP黑名单配置")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DappBlackListConfigVo {

    @ApiModelProperty("DAPP黑名单配置文件下载URL")
    private String fileUrl;

    @ApiModelProperty("DAPP黑名单配置文件版本号")
    private Integer version;

}
