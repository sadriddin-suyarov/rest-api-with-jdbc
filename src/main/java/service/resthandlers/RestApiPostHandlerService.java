package service.resthandlers;

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
public class RestApiPostHandlerService implements RestApiHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private BookDao bookDao;

    @Override
    public Optional<String> handleRestRequest(String requestPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long handleRestRequest(String requestPath, HttpServletRequest req) throws SQLException, IOException {
        long generated_id = 0;
        if (requestPath.matches("/books/") || requestPath.matches("/books")) {
            String bodyParams = req.getReader().lines().collect(Collectors.joining());
            Book book = objectMapper.readValue(bodyParams, Book.class);
            generated_id = bookDao.insert(book);

        }

        return generated_id;
    }
}
