package im.crossim.group.vo;

import im.crossim.common.vo.MyPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@ApiModel("列出群组操作结果")
@Data
@Builder
public class ListGroupVo {

    @ApiModelProperty("分页对象")
    private MyPage<GroupVo> page;

}
