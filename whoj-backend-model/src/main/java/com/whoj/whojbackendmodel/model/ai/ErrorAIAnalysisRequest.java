package com.whoj.whojbackendmodel.model.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ErrorAIAnalysisRequest {
    String errMsg;
    String code;
}
