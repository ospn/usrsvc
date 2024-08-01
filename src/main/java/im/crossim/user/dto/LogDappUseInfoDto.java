package im.crossim.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("记录用户的DAPP使用信息操作参数")
@Data
public class LogDappUseInfoDto {

    @ApiModelProperty("DAPP信息字符串")
    private String dappInfoStr;

}
