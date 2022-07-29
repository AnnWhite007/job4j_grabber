package ru.job4j.grabber;

import java.io.IOException;
import java.util.List;

/**
 *  Интерфейс описывающий парсинг сайта
 */
public interface Parse {
    List<Post> list(String link) throws IOException;
}
