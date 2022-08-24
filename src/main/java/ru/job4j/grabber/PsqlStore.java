package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Хранилище
 * Метод save() - сохраняет объявление в базе.
 * Метод getAll() - позволяет извлечь объявления из базы.
 * Метод findById(int id) - позволяет извлечь объявление из базы по id.
 */

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        cnn = DriverManager.getConnection(cfg.getProperty("jdbc.url"),
                cfg.getProperty("jdbc.username"),
                cfg.getProperty("jdbc.password"));
    }

    public static void main(String[] args) throws Exception {
        Properties properties = config();
        try (PsqlStore psqlStore = new PsqlStore(properties)) {
            Post post = new Post("test", "/1234", "qwerty", LocalDateTime.of(2017, 11, 6, 6, 30, 40, 50000));
            Post post2 = new Post("test2", "/0000", "qwerty2", LocalDateTime.of(2022, 1, 7, 6, 40, 2, 1200));
            Post post3 = new Post("test3", "/1111", "qwerty3", LocalDateTime.of(2000, 5, 2, 2, 20, 0, 0));

            psqlStore.save(post);
            psqlStore.save(post2);
            psqlStore.save(post3);
            for (Post value : psqlStore.getAll()) {
                System.out.println(value);
            }

            Post post4 = psqlStore.findById(5);
            System.out.println(post4);
        }
    }

    public static Properties config() {
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("post.properties")) {
            Properties config = new Properties();
            config.load(in);
            return config;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Post parseResultSet(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId((resultSet.getInt(1)));
        post.setTitle(resultSet.getString(2));
        post.setDescription(resultSet.getString(3));
        post.setLink(resultSet.getString(4));
        post.setCreated(resultSet.getTimestamp(5).toLocalDateTime());
        return post;
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement("insert into post(name, text, link, created) "
                + "values (?, ?, ?, ?) on conflict (link) do nothing")) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        String query = "select * from post";
        List<Post> posts = new ArrayList<>();
        try (Statement statement = cnn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Post post = parseResultSet(resultSet);
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement ps = cnn.prepareStatement("select * from post where id = ?;")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    post = parseResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}