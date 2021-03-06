package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 2.1. Преобразование даты
 * В данном задании вам нужно преобразовать исходные данные, заданные  в виде строк,
 * в объекты Java для возможности дальнейшей работы с ними как с временным типом.
 */

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime localDateTime = LocalDateTime.from(ZonedDateTime.parse(parse));
        return localDateTime;
    }

}