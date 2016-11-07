package syn.servlets;

import syn.entities.HPKPReport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.SQLException;

public class HPKPServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Connection sql = (Connection) getServletContext().getAttribute("db");
		String hostname = req.getParameter("hostname");
		HPKPReport[] violations = null;

		try {
			if (hostname != null) {
				violations = HPKPReport.findReportsByHostname(sql, hostname);
			} else {
				violations = HPKPReport.findReports(sql);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		req.setAttribute("violations", violations);
		getServletContext().getNamedDispatcher("hpkp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			HPKPReport violation = new HPKPReport(req.getReader());
			Connection sql = (Connection) getServletContext().getAttribute("db");
			violation.store(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (CertificateException e) {
			System.out.println(e.getMessage());
		}
	}
}
