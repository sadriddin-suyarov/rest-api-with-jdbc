package service.resthandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.BookDao;
import entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class RestApiGetHandlerService implements RestApiHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private BookDao bookDao;

    @Override
    public Optional<String> handleRestRequest(String requestPath) throws SQLException, JsonProcessingException {
        if (requestPath.matches("^/books/\\d+$")) {
            String bookIdParam = parseID(requestPath);
            int bookId = Integer.parseInt(bookIdParam);
            Book book = bookDao.findById(bookId).orElseThrow(SQLException::new);
            final String jsonBook = objectMapper.writeValueAsString(book);
            return Optional.ofNullable(jsonBook);

        } else if (requestPath.matches("/books") || requestPath.matches("/books/")) {
            final List<Book> books = bookDao.findAll();
            return Optional.ofNullable(objectMapper.writeValueAsString(books));
        }

        return Optional.empty();
    }

    @Override
    public long handleRestRequest(String requestPath, HttpServletRequest request) {
        throw new UnsupportedOperationException();
    }

    private String parseID(String requestPath) {
        String[] parts = requestPath.split("/");
        return parts[2];
    }
}
