package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.DriverManager;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

/**
 * 0. Техническое задание. Агрегатор Java Вакансий.
 * 1. Quartz
 * В Java есть библиотека позволяющая делать действия с периодичностью.
 * "http://www.quartz-scheduler.org/"
 * Библиотека Quartz при запуске ищет доступный логгер, поэтому для корректной работы необходимо его подключить.
 * Периодический запуск:
 * 1. Конфигурирование.
 * Начало работы происходит с создания класса управляющего всеми работами.
 * В объект Scheduler мы будем добавлять задачи, которые хотим выполнять периодически.
 * 2. Создание задачи. JobDetail
 * quartz каждый раз создает объект с типом org.quartz.Job. Нужно создать класс реализующий этот интерфейс.
 * Внутри класса Rabbit нужно описать требуемые действия. В нашем случае - это вывод на консоль текста.
 * 3. Создание расписания.SimpleScheduleBuilder
 * Настраивается периодичность запуска. В нашем случае, мы будем запускать задачу через 10 секунд и делать это бесконечно.
 * 4. Задача выполняется через триггер - Trigger
 * Здесь можно указать, когда начинать запуск. Мы хотим сделать это сразу.
 * 5. Загрузка задачи и триггера в планировщик. scheduleJob
 * <p>
 * Задание.
 * 1. Доработайте программу AlertRabbit. Нужно создать файл rabbit.properties.
 * 2. При запуске программы нужно читать файл rabbit.properties.
 */

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(param())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static int param() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            return Integer.parseInt(config.getProperty("rabbit.interval"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}