package dao;

import entity.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookDao {
    long insert(Book book) throws SQLException;

    Optional<Book> findById(int id) throws SQLException;

    List<Book> findAll() throws SQLException;

    int delete(int id) throws SQLException;

    int update(Book book) throws SQLException;

}
