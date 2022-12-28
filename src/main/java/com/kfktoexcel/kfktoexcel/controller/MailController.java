package com.kfktoexcel.kfktoexcel.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

/**
 * @author chengkui
 */
@RestController
@Api(tags = "邮件控制器")
@Slf4j
public class MailController {

    @Autowired
    private JavaMailSender javaMailSender;

    //发件人邮箱
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendMail(String subject, String text, String email){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //收件人
        mailMessage.setTo(email);  //收件人邮箱也可以为其他，如qq邮箱
        //发件人
        mailMessage.setFrom(fromEmail);
        //设置标题
        mailMessage.setSubject(subject);
        //设置正文
        mailMessage.setText(text);
        //发送邮件
        javaMailSender.send(mailMessage);
    }

    @RequestMapping("/send")
    public String send(@RequestParam("email") String email){
        String code = "";
        for (int i = 0; i < 6; i++) {
            code += new Random().nextInt(10);  //生成6位数随机验证码
        }
        sendMail("邮件标题", "邮件内容", email);  //收件人邮箱作为参数是为了从前端获取
        return code;  //返回验证码
    }

    @RequestMapping("/cpu")
    public void cpuTest() {
        int a = 0;
        while (true) {
            a++;
        }
    }

    private static Object lock1 = new Object();

    private static Object lock2 = new Object();


    @GetMapping("/lock")
    public void lockTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock1) {
                   log.info("thread1拿到锁lock1");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (lock2) {
                        log.info("thread1拿到锁lock2");
                    }
                }
            }
        }, "thread1").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock2) {
                    log.info("thread2拿到锁lock2");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (lock1) {
                        log.info("thread2拿到锁lock1");
                    }
                }
            }
        }, "thread2").start();

    }
}
