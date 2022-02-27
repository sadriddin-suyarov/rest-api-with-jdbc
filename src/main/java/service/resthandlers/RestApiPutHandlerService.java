package service.resthandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.BookDao;
import entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class RestApiPutHandlerService implements RestApiHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private BookDao bookDao;

    @Override
    public Optional<String> handleRestRequest(String requestPath) throws SQLException, JsonProcessingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long handleRestRequest(String requestPath, HttpServletRequest req) throws SQLException, IOException {
        long updatedRows = 0;
        if (requestPath.matches("^/books/\\d+$")) {
            String[] parts = requestPath.split("/");
            String bookIdParam = parts[2];
            int bookId = Integer.parseInt(bookIdParam);
            String bodyParams = req.getReader().lines().collect(Collectors.joining());

            Book book = objectMapper.readValue(bodyParams, Book.class);
            book.setId(bookId);

            updatedRows = bookDao.update(book);
        }

        return updatedRows;
    }
}
