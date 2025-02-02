package com.whoj.whojbackendmodel.model.dto.comment;

import com.whoj.whojbackcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentQueryRequest extends PageRequest implements Serializable {
    private Long postId;
}
