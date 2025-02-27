package com.whoj.whojbackendmodel.model.dto.post;

import com.baomidou.mybatisplus.annotation.TableField;
import com.whoj.whojbackcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class PostQueryRequest extends PageRequest implements Serializable {

    private Long id;
    /**
     * 标题
     */
    private String title;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 创建时间区间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAfter;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createBefore;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
