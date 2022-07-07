package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
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

    private Connection cn;

    public static void main(String[] args) {
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(param())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
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

    public void init() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("rabbit.driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("rabbit.url"),
                    config.getProperty("rabbit.username"),
                    config.getProperty("rabbit.password")
            );

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(System.currentTimeMillis());
        }
    }
}