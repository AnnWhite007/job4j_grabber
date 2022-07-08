package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
 * <p>
 * 1.1. Job c параметрами
 * В проекте агрегатор будет использоваться база данных. Открыть и закрывать соединение с базой накладно.
 * Чтобы этого избежать коннект к базе будет создаваться при старте. Объект коннект будет передаваться в Job.
 * Quartz создает объект Job, каждый раз при выполнении работы.
 * Каждый запуск работы вызывает конструктор. Чтобы в объект Job иметь общий ресурс нужно использовать JobExecutionContext.
 * При создании Job мы указываем параметры data. В них мы передаем ссылку на store (connection)
 * Чтобы получить объекты из context используется  context.getJobDetail().getJobDataMap().get("store");
 * Объект store является общим для каждой работы.
 * <p>
 * Задача:
 * Добавить в файл rabbit.properties настройки для базы данных. Создать sql schema с таблицей rabbit и полем created_date.
 * При старте приложения создать connect к базе и передать его в Job. В Job сделать запись в таблицу, когда выполнена Job.
 * Весь main должен работать 10 секунд. Закрыть коннект нужно в блоке try-with-resources.
 */

public class AlertRabbit {

    public static void main(String[] args) {
        try {
            Properties properties = config();
            int interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
            Connection connection = init(properties);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static Properties config() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            return config;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Connection init(Properties properties) throws ClassNotFoundException {
        Class.forName(properties.getProperty("rabbit.driver-class-name"));
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("rabbit.url"),
                properties.getProperty("rabbit.username"),
                properties.getProperty("rabbit.password"))
        ) {
            return connection;

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
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement =
                         connection.prepareStatement("insert into rabbit(created_date) values (?)")) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)));
                statement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}