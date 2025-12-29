package com.whoj.whojbackendjudgeservice.judge.strategy;


import com.whoj.whojbackendmodel.model.codesandbox.JudgeInfo;
import com.whoj.whojbackendmodel.model.dto.question.JudgeCase;
import com.whoj.whojbackendmodel.model.dto.question.JudgeConf;
import com.whoj.whojbackendmodel.model.enums.JudgeInfoMessageEnum;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 其他语言判题处理（给予两倍时间空间）
 */
public class OtherLangJudgeStrategy implements JudgeStrategy {

    @Override
    public JudgeContext doJudge(JudgeContext judgeContext) {
        List<String> runOutputList = judgeContext.getOutputList(); // 运行输出
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        JudgeConf judgeConf = judgeContext.getJudgeConf(); //判题限制时间、内存
        List<String> outputList = judgeCaseList.stream().map(JudgeCase::getOutput).toList(); // 正确输出
        List<Long> timeList = judgeContext.getTimeList();
        List<Long> memoList = judgeContext.getMemoList();
        List<String> errorOutputList = judgeContext.getErrorOutputlist();
        List<String> messageList = new LinkedList<>();
        String message = JudgeInfoMessageEnum.ACCEPTED.getText();
        Long maxTime = 0L;
        Long maxMemo = 0L;
        Set<JudgeInfoMessageEnum> messageEnumSet = new HashSet<>(); // 用于判断 “多种错误” 情况

        for (int i = 0; i < judgeCaseList.size(); i++) {
            String output = outputList.get(i); // 正确输出
            maxTime = Math.max(maxTime, timeList.get(i));
            maxMemo = Math.max(maxMemo, memoList.get(i));
            // 判断有无错误输出
            if (errorOutputList.get(i) != null && !errorOutputList.get(i).isBlank()) {
                message = JudgeInfoMessageEnum.RUNTIME_ERROR.getText();
                messageEnumSet.add(JudgeInfoMessageEnum.RUNTIME_ERROR);
                messageList.add(JudgeInfoMessageEnum.RUNTIME_ERROR.getText());
                continue;
            }
            // 判断时间
            if (timeList.get(i) >= judgeConf.getTimeLimit() * 2) {
                message = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getText();
                messageEnumSet.add(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED);
                messageList.add(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getText());
                continue;
            }
            // 判断内存
            if (memoList.get(i) / 1000 >= judgeConf.getMemoLimit() * 2) { // 远程代码沙箱的内存单位为Byte，判题配置为kByte
                message = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getText();
                messageEnumSet.add(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED);
                messageList.add(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getText());
                continue;
            }
            //判断答案
            if (!equals(runOutputList.get(i), output)) {
                message = JudgeInfoMessageEnum.WRONG_ANSWER.getText();
                messageEnumSet.add(JudgeInfoMessageEnum.WRONG_ANSWER);
                messageList.add(JudgeInfoMessageEnum.WRONG_ANSWER.getText());
                continue;
            }
            messageList.add(JudgeInfoMessageEnum.ACCEPTED.getText());
        }
        JudgeInfo judgeInfo = JudgeInfo.builder()
                .maxTime(maxTime)
                .maxMemo(maxMemo)
                .message(messageEnumSet.size() >= 2 ? JudgeInfoMessageEnum.MANY_ERROR.getText() : message)
                .build();

        return JudgeContext.builder()
                .judgeInfo(judgeInfo)
                .messageList(messageList)
                .build();
    }

    /**
     * 不考虑最后的换行符，比较两个字符串
     * @param s1 str1
     * @param s2 str2
     * @return 是否相同
     */
    private boolean equals(String s1, String s2) {
        if (s1.charAt(s1.length() - 1) == s2.charAt(s2.length() - 1)) {
            return s1.equals(s2);
        }else {
            if (s1.charAt(s1.length() - 1) == '\n') {
                if (s1.length() > s2.length()) return s1.substring(0, s1.length() - 1).equals(s2);
                else return false;
            }else if (s2.charAt(s2.length() - 1) == '\n') {
                if (s2.length() > s1.length()) return s2.substring(0, s2.length() - 1).equals(s1);
                else return false;
            }
        }
        return false;
    }
}
