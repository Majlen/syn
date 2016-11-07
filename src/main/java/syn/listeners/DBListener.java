package syn.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ServletContextListener opening and closing the connection to Database.
 * @author Milan Ševčík
 */
public final class DBListener implements ServletContextListener {
	private ServletContext context = null;
	private Connection db;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		this.context = null;
		try {
			db.close();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		this.context = event.getServletContext();
		String dburl = "jdbc:sqlite:" + System.getProperty("catalina.base") + "/syn.sqlite";

		try {
			Class.forName("org.sqlite.JDBC");
			db = DriverManager.getConnection(dburl);
			context.setAttribute("db", db);
		} catch (ClassNotFoundException e) {
			System.out.println("Cannot find JDBC driver: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("Cannot connect to DB: " + e.getMessage());
		}
	}
}
