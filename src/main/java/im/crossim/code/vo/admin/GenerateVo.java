package im.crossim.code.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("批量生成激活码操作结果")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateVo {

    @ApiModelProperty("批次ID")
    private String batchId;

}
