package com.whoj.whojbackendmodel.model.dto.post;

import com.baomidou.mybatisplus.annotation.TableField;
import com.whoj.whojbackcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class SolutionPageQueryRequest extends PageRequest implements Serializable {

    private Long questionId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
