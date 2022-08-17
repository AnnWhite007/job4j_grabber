package ru.job4j.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Периодический запуск.
 * В этом проекте используется quartz для запуска парсера.
 */

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}