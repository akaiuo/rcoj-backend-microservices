package com.whoj.whojbackendvalidationservice.service;

import javax.mail.MessagingException;

public interface MailService {
    /**
     * 发送纯文本邮件
     * @param to
     * @param subject
     * @param text
     */
    boolean sendTextMailMessage(String to, String subject, String text) throws MessagingException;

    /**
     * 发送html邮件
     * @param to
     * @param subject
     * @param content
     */
    void sendHtmlMailMessage(String to, String subject, String content);

    /**
     * 发送带附件的邮件
     * @param to      邮件收信人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param filePath 附件路径
     */
    void sendAttachmentMailMessage(String to, String subject, String content, String filePath);
}
