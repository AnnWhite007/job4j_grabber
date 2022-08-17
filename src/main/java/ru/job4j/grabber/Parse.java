package ru.job4j.grabber;

import java.io.IOException;
import java.util.List;

/**
 *  Интерфейс описывающий парсинг сайта (Извлечение данных с сайта)
 *  Этот компонент позволяет собрать короткое описание всех объявлений, а так же загрузить детали по каждому объявлению.
 * list(link) - этот метод загружает список объявлений по ссылке типа - "https://www.sql.ru/forum/job-offers/1"
 * Описание компонента через интерфейс позволяет расширить проект.
 * Например, осуществить сбор данных с других площадок: SqlRuParse, SuperJobParse.
 */
public interface Parse {
    List<Post> list(String link) throws IOException;
}
