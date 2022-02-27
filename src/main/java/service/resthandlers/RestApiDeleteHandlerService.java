package service.resthandlers;

import dao.BookDao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class RestApiDeleteHandlerService implements RestApiHandler {
    private BookDao bookDao;
    @Override
    public Optional<String> handleRestRequest(String requestPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long handleRestRequest(String requestPath, HttpServletRequest req) throws SQLException {
        long updated_rows = 0;
        if (requestPath.matches("^/books/\\d+$")) {
            String[] parts = requestPath.split("/");
            String bookIdParam = parts[2];

            int bookId = Integer.parseInt(bookIdParam);
            updated_rows = bookDao.delete(bookId);
        }

        return updated_rows;
    }
}
