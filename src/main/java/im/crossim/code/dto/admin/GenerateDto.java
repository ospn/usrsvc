package im.crossim.code.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel("批量生成激活码操作参数")
@Data
public class GenerateDto {

    /* ==================================================
        通用参数
     ================================================== */

    @ApiModelProperty("生成模式")
    @NotNull(message = "参数mode不能为空")
    private Integer mode;

    @ApiModelProperty("批次号")
    @NotEmpty(message = "参数batchCode不能为空")
    private String batchCode;

    @ApiModelProperty("激活码类型")
    @NotNull(message = "参数type不能为空")
    private Integer type;

    @ApiModelProperty("激活码数量")
    @NotNull(message = "参数count不能为空")
    private Integer count;

    @ApiModelProperty("有效时间开始")
    @NotNull(message = "参数effectiveTimeStart不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveTimeStart;

    @ApiModelProperty("有效时间结束")
    @NotNull(message = "参数effectiveTimeEnd不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveTimeEnd;

    @ApiModelProperty("是否启用")
    @NotNull(message = "参数enabled不能为空")
    private Boolean enabled;

    /* ==================================================
        mode=0：基于字母表生成
     ================================================== */

    @ApiModelProperty("前缀")
    private String prefix;

    @ApiModelProperty("激活码长度")
    private Integer size;

    @ApiModelProperty("字母表")
    private String alphabet;

    /* ==================================================
        mode=1：基于UUID生成
     ================================================== */
    // 无参数

}
