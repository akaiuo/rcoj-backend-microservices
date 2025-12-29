package com.whoj.whojbackendvalidationservice.service.impl;

import com.alibaba.nacos.common.packagescan.resource.FileSystemResource;
import com.whoj.whojbackendvalidationservice.service.MailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.File;
import java.util.Date;

@Service
public class MailServiceImpl implements MailService {
    /**
     * 注入邮件工具类
     */
    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Value("${spring.mail.username}")
    private String sendMailer;

    /**
     * 检测邮件信息类
     * @param to
     * @param subject
     * @param text
     */
    private void checkMail(String to,String subject,String text){
        if(StringUtils.isEmpty(to)){
            throw new RuntimeException("邮件收信人不能为空");
        }
        if(StringUtils.isEmpty(subject)){
            throw new RuntimeException("邮件主题不能为空");
        }
        if(StringUtils.isEmpty(text)){
            throw new RuntimeException("邮件内容不能为空");
        }
    }

    /**
     * 发送纯文本邮件
     * @param to
     * @param subject
     * @param text
     */
    @Override
    public boolean sendTextMailMessage(String to, String subject, String text) throws MessagingException {
        checkMail(to,subject,text);
        //true 代表支持复杂的类型
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(),true);
        //邮件发信人
        mimeMessageHelper.setFrom(sendMailer);
        //邮件收信人  1或多个
        mimeMessageHelper.setTo(to.split(","));
        //邮件主题
        mimeMessageHelper.setSubject(subject);
        //邮件内容
        mimeMessageHelper.setText(text);
        //邮件发送时间
        mimeMessageHelper.setSentDate(new Date());

        //发送邮件
        javaMailSender.send(mimeMessageHelper.getMimeMessage());
        return true;
    }


    /**
     * 发送html邮件
     * @param to
     * @param subject
     * @param content
     */
    @Override
    public void sendHtmlMailMessage(String to, String subject, String content){
        try {
            //true 代表支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(),true);
            //邮件发信人
            mimeMessageHelper.setFrom(sendMailer);
            //邮件收信人  1或多个
            mimeMessageHelper.setTo(to.split(","));
            //邮件主题
            mimeMessageHelper.setSubject(subject);
            //邮件内容   true 代表支持html
            mimeMessageHelper.setText(content,true);
            //邮件发送时间
            mimeMessageHelper.setSentDate(new Date());

            //发送邮件
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            System.out.println("发送邮件成功："+sendMailer+"->"+to);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发送邮件失败："+e.getMessage());
        }
    }

    /**
     * 发送带附件的邮件
     * @param to      邮件收信人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param filePath 附件路径
     */
    @Override
    public void sendAttachmentMailMessage(String to, String subject, String content, String filePath){
        try {
            //true 代表支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(),true);
            //邮件发信人
            mimeMessageHelper.setFrom(sendMailer);
            //邮件收信人  1或多个
            mimeMessageHelper.setTo(to.split(","));
            //邮件主题
            mimeMessageHelper.setSubject(subject);
            //邮件内容   true 代表支持html
            mimeMessageHelper.setText(content,true);
            //邮件发送时间
            mimeMessageHelper.setSentDate(new Date());
            //添加邮件附件
            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = file.getFilename();
            mimeMessageHelper.addAttachment(fileName, file.getFile());

            //发送邮件
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            System.out.println("发送邮件成功："+sendMailer+"->"+to);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发送邮件失败："+e.getMessage());
        }
    }

    public String validCodeHtml(String code, String title, int validCodeExpireMinutes) {
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>" + title + "</title>\n" +
                "    <style type=\"text/css\">\n" +
                "        /* 内联样式确保邮件客户端兼容性 */\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;\n" +
                "            line-height: 1.6;\n" +
                "            color: #444;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 30px 20px;\n" +
                "            background: #ffffff;\n" +
                "        }\n" +
                "        .code-box {\n" +
                "            font-size: 32px;\n" +
                "            letter-spacing: 10px;\n" +
                "            padding: 20px;\n" +
                "            background: #f8f9fa;\n" +
                "            text-align: center;\n" +
                "            margin: 25px 0;\n" +
                "            border-radius: 8px;\n" +
                "            color: #2d3436;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            padding: 5px;\n" +
                "            text-align: center;\n" +
                "            color: #6c757d;\n" +
                "            font-size: 12px;\n" +
                "            background: #f8f9fa;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"container\">\n" +
                "\n" +
                "    <div class=\"content\">\n" +
                "\n" +
                "        <p>您正在尝试登录/注册，请输入以下验证码：</p>\n" +
                "        <p>You are trying to log in or register, so please enter the following verification code to continue：</p>\n" +
                "\n" +
                "        <div class=\"code-box\">\n" +
                "            " + code + "\n" +
                "        </div>\n" +
                "\n" +
                "        <p style=\"color: #6c757d;\">该验证码 <strong>" + validCodeExpireMinutes + "分钟</strong> 内有效，请勿泄露给他人</p>\n" +
                "        <p style=\"color: #6c757d;\">This verification code is valid for <strong>" + validCodeExpireMinutes + " minutes</strong>, do not share it with anyone</p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"footer\">\n" +
                "        <p>©2025 RCOJ保留所有权利 All rights reserved</p>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n";



        return text;
    }
}
