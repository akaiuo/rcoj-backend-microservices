package com.whoj.whojbackendmodel.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JudgeSubmit {
    private Long questionId;
    private Boolean isAccepted;
}
