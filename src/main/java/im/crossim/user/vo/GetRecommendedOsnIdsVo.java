package im.crossim.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("获取推荐的用户OSN ID列表操作结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRecommendedOsnIdsVo {

    @ApiModelProperty("推荐的用户OSN ID列表")
    private List<String> recommendedOsnIds;

}
