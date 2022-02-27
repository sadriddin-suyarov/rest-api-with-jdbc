package service.resthandlers;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public interface RestApiHandler {
    Optional<String> handleRestRequest(String requestPath) throws SQLException, JsonProcessingException;

    long handleRestRequest(String requestPath, HttpServletRequest request) throws SQLException, IOException;

}
