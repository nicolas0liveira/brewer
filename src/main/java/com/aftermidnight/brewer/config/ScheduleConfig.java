package com.aftermidnight.brewer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.aftermidnight.brewer.schedule.VendaSchedule;

@Configuration
@ComponentScan(basePackageClasses = VendaSchedule.class)
@EnableScheduling
@EnableAsync
public class ScheduleConfig {

}
