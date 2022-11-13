package com.atguigu.yygh.scheduled;

import com.atguigu.yygh.constant.MqConst;
import com.atguigu.yygh.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    /**
     * 每天8点执行 提醒就诊
     *
     * cron表达式设置执行的间隔
     */
    //@Scheduled(cron = "0 0 8 * * ?") 这里象征每天八点执行
    @Scheduled(cron = "0/30 * * * * ?")
    public void task1() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }
}
