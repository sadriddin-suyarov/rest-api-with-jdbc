package servlet;

import dao.*;
import dao.datasource.DataSourceHikariPostgreSQL;
import dao.jdbc.sessionmanager.SessionManager;
import dao.jdbc.sessionmanager.SessionManagerJdbc;
import service.resthandlers.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        final ServletContext servletContext =
                servletContextEvent.getServletContext();

        DataSource dataSource = DataSourceHikariPostgreSQL.getHikariDataSource();
        SessionManager sessionManager = new SessionManagerJdbc(dataSource);
        BookDao bookDao = new BookDaoImpl(sessionManager);

        RestApiHandler restApiGetHandlerService = new RestApiGetHandlerService(bookDao);
        RestApiHandler restApiPostHandlerService = new RestApiPostHandlerService(bookDao);
        RestApiHandler restApiPutHandlerService = new RestApiPutHandlerService(bookDao);
        RestApiHandler restApiDeleteHandlerService = new RestApiDeleteHandlerService(bookDao);

        servletContext.setAttribute("bookDao", bookDao);
        servletContext.setAttribute("restApiGetHandlerService", restApiGetHandlerService);
        servletContext.setAttribute("restApiPostHandlerService", restApiPostHandlerService);
        servletContext.setAttribute("restApiPutHandlerService", restApiPutHandlerService);
        servletContext.setAttribute("restApiDeleteHandlerService", restApiDeleteHandlerService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
