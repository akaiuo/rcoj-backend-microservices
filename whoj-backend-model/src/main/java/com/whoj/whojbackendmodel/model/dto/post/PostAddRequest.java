package com.whoj.whojbackendmodel.model.dto.post;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
public class PostAddRequest {
    private String title;
    private String content;
    private String preview;
    private List<String> tags;
    private Integer editorType; // 1: 富文本， 2:md

    @Nullable
    private Long questionId = null; // 题解对应题目id 供添加题解使用
}
