package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 2. Парсинг HTML страницы.
 * Когда мы открываем в браузере сайт, то браузер отправляет запрос на сервер. Сервер отправляет текст обратно браузеру.
 * Текст от сервера - это HTML. Браузер должен его сначала разобрать. Во время анализа и разбора HTML-кода браузер строит на основе него DOM-дерево.
 * После выполнения этого браузер приступает к отрисовке страницы. При этом уже используется созданное DOM-дерево, а не исходный HTML-код.
 * DOM (Document Object Model) – это объектная модель документа, которую браузер создает в памяти компьютера на основании HTML-кода, полученного им от сервера.
 * Если говорить простым языком, то HTML-код – это текст страницы, а DOM – это набор связанных объектов, которые созданы браузером при парсинге ее текста.
 * Модели в этой модели образуются практически из всего, что имеется в HTML (теги, текстовый контент, комментарии и т.д.) включая при этом сам документ.
 * Связи между этими объектами в модели формируются на основании того, как HTML-элементы расположены в коде относительно друг друга.
 * При этом DOM документа после формирования можно изменять. При изменении DOM браузер мгновенно перерисовывает изображение страницы. В результате у нас отрисовка страницы всегда соответствует DOM.
 * Исходный код веб-страницы состоит из тегов, атрибутов, комментариев и текста. Теги – это базовая синтаксическая конструкция HTML.
 * Большинство из них являются парными. В этом случае один является открывающим, а второй – закрывающим.
 * Пара таких тегов образует HTML-элемент. HTML-элементы могут иметь дополнительные параметры – атрибуты.
 * В документе для создания разметки одни элементы находятся внутри других. В результате HTML-документ можно представить, как множество вложенных друг в друга HTML-элементов.
 * Браузер в итоге строит дерево на основе HTML-элементов и других сущностей исходного кода страницы. При этом учитывается вложенность элементов друг в друга.
 * В итоге браузер полученное DOM-дерево использует как в своей работе, так и предоставляет нам API для работы с ним через JavaScript.
 * При строительстве DOM браузер создает из HTML-элементов, текста, комментариев и других сущностей языка HTML объекты (узлы DOM-дерева).
 * В HTML любой элемент всегда имеет одного родителя (HTML-элемент, в котором он непосредственно расположен). В HTML у элемента не может быть несколько родителей, исключение составляет элемент html – у него нет вообще родителей.
 * DOM-дерево строится сверху вниз. Корнем DOM-дерева всегда является сам документ (узел document). Далее дерево строится в зависимости от структуры HTML кода.
 * Библиотека jsoup - позволяет сделать запрос на сервер и извлечь нужный текст из полученного HTML.
 * Разберем код:
 * 1. У нас есть две константы. Первая это ссылка на сайт в целом. Вторая указывает на страницу с вакансиями непосредственно
 * 2. Сначала мы получаем страницу, чтобы с ней можно было работать:
 * 3. Далее анализируя структуру страницы мы получаем, что признаком вакансии является CSS класс .vacancy-card__inner, а признаком названия класс .vacancy-card__title.
 * Ссылка на вакансию вложена в элемент названия, сама же ссылка содержит абсолютный путь к вакансии (относительно домена. Это наша константа SOURCE_LINK)
 * 4. На основе анализа прописываем парсинг
 * 1) Сначала мы получаем все вакансии страницы.
 * Перед CSS классом ставится точка. Это правила CSS селекторов, с которыми работает метод JSOUP select()
 * 2) Проходимся по каждой вакансии и извлекаем нужные для нас данные. Сначала получаем элементы содержащие название и ссылку.
 * Дочерние элементы можно получать через индекс - метод child(0) или же через селектор - select(".vacancy-card__title").
 * 3) Наконец получаем данные непосредственно. text() возвращает все содержимое элемента в виде текста, т.е. весь текст что находится вне тегов HTML.
 * Ссылку находится в виде атрибута, поэтому ее значение надо получить как значение атрибута. Для этого служит метод attr()
 * <p>
 * 2.1.1. Парсинг. Парсить нужно первые 5 страниц.
 * 2.3. Загрузка деталей поста. Создайте метод для загрузки деталей объявления.
 * 2.4. HabrCareerParse
 * В list передается  ссылка "https://career.habr.com/vacancies/java_developer?page=" и в цикле Вы прибавляете к ней номера страниц от 1 до 5.
 * Имейте в виду, что ссылка будет поступать извне. У нас запуск программы в другом классе.
 * Парсинг Post вынесите в отдельный метод c Element в параметрах (возврат Post), а его уже используйте в list
 */

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws IOException {
        final int LIMIT = 5;
        for (int p = 1; p <= LIMIT; p++) {
            Connection connection = Jsoup.connect(PAGE_LINK + p);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                String dateTime = dateElement.child(0).attr("datetime");
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s%n", vacancyName, link, dateTime);
            });
        }
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element description = document.selectFirst(".style-ugc");
        return description.text();
    }

    public Post getPost(Element row) {
        Post post = new Post();
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        Element dateElement = row.select(".vacancy-card__date").first();
        String dateTime = dateElement.child(0).attr("datetime");
        DateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(habrCareerDateTimeParser);
        LocalDateTime localDateTime = habrCareerParse.dateTimeParser.parse(dateTime);
        post.setCreated(localDateTime);
        post.setTitle(titleElement.text());
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        post.setLink(link);
        String description;
        try {
            description = retrieveDescription(link);
            post.setDescription(description);
        } catch (IllegalArgumentException | IOException i) {
            i.printStackTrace();
        }
        return post;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> vacancyList = new ArrayList<>();
        final int LIMIT = 5;
        for (int p = 1; p <= LIMIT; p++) {
            Connection connection = Jsoup.connect(link + p);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> vacancyList.add(getPost(row)));
        }
        return vacancyList;
    }
}