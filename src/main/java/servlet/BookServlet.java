package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import service.resthandlers.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/api/*", name = "BookServlet")
public class BookServlet extends HttpServlet {
    private static final String BOOK_ADD_ERROR = "The book was not added !\n";
    private static final String BOOK_UPDATE_ERROR = "The book was not updated\n";
    private static final String BOOK_DELETE_ERROR = "The book was not deleted\n";
    private static final String BOOK_CREATED_SUCCESS_JSON = "{ \"id\" : \"%d\" }";
    private static final String BOOK_NOT_FOUND = "The book with given ID not found\n";

    private RestApiHandler restApiGetHandler;
    private RestApiHandler restApiPostHandler;
    private RestApiHandler restApiPutHandler;
    private RestApiHandler restApiDeleteHandler;

    @Override
    public void init() {
        final Object restApiGetHandlerService = getServletContext().getAttribute("restApiGetHandlerService");
        final Object restApiPostHandlerService = getServletContext().getAttribute("restApiPostHandlerService");
        final Object restApiPutHandlerService = getServletContext().getAttribute("restApiPutHandlerService");
        final Object restApiDeleteHandlerService = getServletContext().getAttribute("restApiDeleteHandlerService");

        this.restApiGetHandler = (RestApiGetHandlerService) restApiGetHandlerService;
        this.restApiPostHandler = (RestApiPostHandlerService) restApiPostHandlerService;
        this.restApiPutHandler = (RestApiPutHandlerService) restApiPutHandlerService;
        this.restApiDeleteHandler = (RestApiDeleteHandlerService) restApiDeleteHandlerService;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/HTML; charset=UTF-8");

        try {
            String user_response = restApiGetHandler.handleRestRequest(pathInfo).orElseThrow(SQLException::new);
            resp.setContentType("application/json; charset=UTF-8");
            resp.setStatus(200);
            PrintWriter out = resp.getWriter();
            out.write(user_response);

        } catch (SQLException e) {
            e.printStackTrace();
            PrintWriter out = resp.getWriter();
            if (pathInfo.contains("books")) {
                out.write("Book with given ID not found");
            }
            resp.setStatus(404);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");


        try {
            long generated_id = restApiPostHandler.handleRestRequest(pathInfo, req);
            resp.setContentType("application/json; charset=UTF-8");
            if (pathInfo.contains("books")) {
                resp.getWriter().write(String.format(BOOK_CREATED_SUCCESS_JSON, generated_id));
            }
            resp.setStatus(201);
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("\"id\" нарушает ограничение NOT NULL")) {
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write(BOOK_ADD_ERROR + BOOK_NOT_FOUND);
                resp.setStatus(404);
                return;
            }

            resp.setContentType("text/HTML; charset=UTF-8");
            resp.getWriter().write("BOOK_ADD_ERROR" + e.getMessage());
            resp.setStatus(400);
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/HTML; charset=UTF-8");

        try {
            long deleted_rows = restApiDeleteHandler.handleRestRequest(pathInfo, req);
            if (deleted_rows != 0) {
                resp.setStatus(200);
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(400);
            if (pathInfo.contains("books")) {
                resp.getWriter().write(BOOK_DELETE_ERROR);
            }

        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/HTML; charset=UTF-8");

        try {
            restApiPutHandler.handleRestRequest(pathInfo, req);
            resp.setStatus(200);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(400);
            resp.getWriter().write(BOOK_UPDATE_ERROR);
        }
    }

}
