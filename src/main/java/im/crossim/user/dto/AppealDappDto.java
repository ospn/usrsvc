package im.crossim.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("申诉DAPP操作参数")
@Data
public class AppealDappDto {

    @ApiModelProperty("DAPP信息字符串")
    private String dappInfoStr;

}
