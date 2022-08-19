package ru.job4j.grabber;

import java.util.List;

/**
 * Хранилище
 * Связь с базой через интерфейс
 * Интерфейсы позволяют избавиться от прямой зависимости.
 * На первом этапе можно использовать MemStore - хранение данных в памяти.
 * Метод save() - сохраняет объявление в базе.
 * Метод getAll() - позволяет извлечь объявления из базы.
 * Метод findById(int id) - позволяет извлечь объявление из базы по id.
 */

public interface Store {
    void save(Post post) throws Exception;

    List<Post> getAll();

    Post findById(int id);
}