package syn.servlets;

import syn.entities.CSPReport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class CSPServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Connection sql = (Connection) getServletContext().getAttribute("db");
		String hostname = req.getParameter("hostname");
		CSPReport[] violations = null;

		try {
			if (hostname != null) {
				violations = CSPReport.findReportsByHostname(sql, hostname);
			} else {
				violations = CSPReport.findReports(sql);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		req.setAttribute("violations", violations);
		getServletContext().getNamedDispatcher("csp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CSPReport violation = new CSPReport(req.getReader());
		Connection sql = (Connection) getServletContext().getAttribute("db");
		try {
			violation.store(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
