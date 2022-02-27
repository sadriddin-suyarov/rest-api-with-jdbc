package dao;

import dao.jdbc.sessionmanager.SessionManager;
import entity.Book;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class BookDaoImpl implements BookDao {
    private final SessionManager sessionManager;

    @Override
    public int update(Book book) throws SQLException {
        int rowsUpdated = 0;
        sessionManager.beginSession();

        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement pst = connection.prepareStatement(SQLTask.UPDATE.QUERY)) {
            pst.setString(1, book.getAuthor());
            pst.setString(2, book.getBookName());
            pst.setString(3, book.getDescription());
            pst.setLong(4, book.getPrice());
            pst.setInt(5, book.getId());

            rowsUpdated = pst.executeUpdate();
            sessionManager.commitSession();

        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
            sessionManager.rollbackSession();
            throw ex;
        }
        return rowsUpdated;
    }

    @Override
    public long insert(Book book) throws SQLException {
        long generated_bookId;
        sessionManager.beginSession();

        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement preparedStatement = connection.prepareStatement(SQLTask.INSERT.QUERY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, book.getAuthor());
            preparedStatement.setString(2, book.getBookName());
            preparedStatement.setString(3, book.getDescription());
            preparedStatement.setLong(4, book.getPrice());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                generated_bookId = resultSet.getLong(1);
                sessionManager.commitSession();
            }
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
            sessionManager.rollbackSession();
            throw ex;
        }
        return generated_bookId;
    }

    @Override
    public Optional<Book> findById(int id) throws SQLException {
        Book book = null;
        sessionManager.beginSession();

        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement preparedStatement = connection.prepareStatement(SQLTask.FIND_BY_ID.QUERY)) {
            preparedStatement.setLong(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    book = parseBookFromResultSet(rs);
                }
            }
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
            sessionManager.rollbackSession();
            throw ex;
        }

        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        sessionManager.beginSession();

        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement pst = connection.prepareStatement(SQLTask.FIND_ALL.QUERY)) {

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    books.add(parseBookFromResultSet(rs));
                }
            }
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
            sessionManager.rollbackSession();
            throw ex;
        }

        return books;
    }

    @Override
    public int delete(int id) throws SQLException {
        int updated_rows;
        sessionManager.beginSession();

        try (Connection connection = sessionManager.getCurrentSession();
             PreparedStatement pst = connection.prepareStatement(SQLTask.DELETE.QUERY)) {
            pst.setLong(1, id);
            updated_rows = pst.executeUpdate();
            sessionManager.commitSession();

        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
            sessionManager.rollbackSession();
            throw ex;
        }
        return updated_rows;
    }


    @AllArgsConstructor
    enum SQLTask {
        INSERT("INSERT INTO book(author, book_name, description, price) " +
                "VALUES ((?), (?), (?), (?))"),
        FIND_ALL("SELECT id, author, book_name, description, price FROM book"),
        FIND_BY_ID("SELECT id, author, book_name, description, price FROM book " +
                "WHERE id = (?)"),
        UPDATE("UPDATE book SET author = (?), book_name = (?), description = (?), price = (?) WHERE id = (?)"),
        DELETE("DELETE FROM book WHERE id = (?)");

        final String QUERY;
    }

    private Book parseBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setAuthor(rs.getString("author"));
        book.setBookName(rs.getString("book_name"));
        book.setDescription(rs.getString("description"));
        book.setPrice(rs.getLong("price"));

        return book;
    }
}
