package im.crossim.common.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 自定义分页对象。
 *
 * @param <T> 自定义分页对象存储的数据的类型。
 */
@ApiModel("分页对象")
@Data
public class MyPage<T> {

    @ApiModelProperty("数据列表")
    private List<T> records;

    @ApiModelProperty("数据总条数")
    private Long total;

    @ApiModelProperty("分页大小")
    private Long size;

    @ApiModelProperty("页码")
    private Long current;

    /**
     * 创建分页对象。
     *
     * @param page Mybatis Plus的分页对象。
     * @param convert 数据转换器。
     * @return 自定义分页对象。
     * @param <T> Mybatis Plus的分页对象存储的数据的类型。
     * @param <R> 自定义分页对象存储的数据的类型。
     */
    public static <T, R> MyPage<R> create(
            IPage<T> page,
            Function<T, R> convert
    ) {
        List<R> records = new ArrayList<>();
        for (T record : page.getRecords()) {
            records.add(
                    convert.apply(record)
            );
        }

        MyPage<R> myPage = new MyPage<>();
        myPage.setRecords(records);
        myPage.setTotal(page.getTotal());
        myPage.setSize(page.getSize());
        myPage.setCurrent(page.getCurrent());
        return myPage;
    }

}
