package im.crossim.app.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("获取APP配置操作结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAppConfigVo {

    @ApiModelProperty("下载页面URL")
    private String downloadPageUrl;

    @ApiModelProperty("最新APP版本")
    private Integer version;

    @ApiModelProperty("是否强制更新")
    private Boolean force;

}
