package im.crossim.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("敏感关键字配置")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensitiveKeywordConfigVo {

    @ApiModelProperty("敏感关键字文件下载URL")
    private String fileUrl;

    @ApiModelProperty("敏感关键字文件版本号")
    private Integer version;

}
