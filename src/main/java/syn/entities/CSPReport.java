package syn.entities;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.CharArrayReader;
import java.io.Reader;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

public class CSPReport {
	private String blocked_uri;
	private String document_uri;
	private String effective_directive;
	private String original_policy;
	private String referrer;
	private int status_code;
	private String violated_directive;
	private String source_file;
	private int line_number;
	private int column_number;
	private Instant date;

	public CSPReport(String blocked_uri, String document_uri, String effective_directive, String original_policy,
	                 String referrer, int status_code, String violated_directive, String source_file,
	                 int line_number, int column_number, Instant date) {
		this.blocked_uri = blocked_uri;
		this.document_uri = document_uri;
		this.effective_directive = effective_directive;
		this.original_policy = original_policy;
		this.referrer = referrer;
		this.status_code = status_code;
		this.violated_directive = violated_directive;
		this.source_file = source_file;
		this.line_number = line_number;
		this.column_number = column_number;
		this.date = date;
	}

	public CSPReport(Reader json) {
		JsonReader reader = Json.createReader(json);
		JsonObject obj = reader.readObject();
		JsonObject csp = obj.getJsonObject("csp-report");
		blocked_uri = csp.getString("blocked-uri", null);
		document_uri = csp.getString("document-uri", null);
		effective_directive = csp.getString("effective-directive", null);
		original_policy = csp.getString("original-policy", null);
		referrer = csp.getString("referrer", null);
		status_code = csp.getInt("status-code", 0);
		violated_directive = csp.getString("violated-directive", null);
		source_file = csp.getString("source-file", null);
		line_number = csp.getInt("line-number", 0);
		column_number = csp.getInt("column-number", 0);
		date = Instant.now();
	}

	public String getBlocked_uri() {
		return blocked_uri;
	}

	public String getDocument_uri() {
		return document_uri;
	}

	public String getEffective_directive() {
		return effective_directive;
	}

	public String getOriginal_policy() {
		return original_policy;
	}

	public String getReferrer() {
		return referrer;
	}

	public int getStatus_code() {
		return status_code;
	}

	public String getViolated_directive() {
		return violated_directive;
	}

	public String getSource_file() {
		return source_file;
	}

	public int getLine_number() {
		return line_number;
	}

	public int getColumn_number() {
		return column_number;
	}

	public Instant getDate() {
		return date;
	}

	public void store(Connection sql) throws SQLException {
		PreparedStatement s = sql.prepareStatement("INSERT INTO csp (blocked_uri, document_uri, effective_directive," +
				"original_policy, referrer, status_code, violated_directive, source_file, line_number," +
				"column_number, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		s.setString(1, blocked_uri);
		s.setString(2, document_uri);
		s.setString(3, effective_directive);
		s.setString(4, original_policy);
		s.setString(5, referrer);
		s.setInt(6, status_code);
		s.setString(7, violated_directive);
		s.setString(8, source_file);
		s.setInt(9, line_number);
		s.setInt(10, column_number);
		s.setTimestamp(11, Timestamp.from(date));
		s.executeUpdate();
		s.close();
	}

	public static void createTable(Connection sql) throws SQLException {
		Statement s = sql.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS csp (blocked_uri TEXT, date INTEGER, document_uri TEXT," +
				" effective_directive TEXT, original_policy TEXT, referrer TEXT, status_code INTEGER," +
				" violated_directive TEXT, source_file TEXT, line_number INTEGER, column_number INTEGER)");
	}

	public static CSPReport[] findReports(Connection sql, int count) throws SQLException {
		PreparedStatement s = sql.prepareStatement("SELECT * FROM csp LIMIT ?;");
		s.setInt(1, count);
		ResultSet rs = s.executeQuery();
		CSPReport[] out = objectsFromResultset(rs);
		s.close();
		return out;
	}

	public static CSPReport[] findReports(Connection sql) throws SQLException {
		PreparedStatement s = sql.prepareStatement("SELECT * FROM csp;");
		ResultSet rs = s.executeQuery();
		CSPReport[] out = objectsFromResultset(rs);
		s.close();
		return out;
	}

	public static CSPReport[] findReportsByHostname(Connection sql, String hostname, int count) throws SQLException {
		PreparedStatement s = sql.prepareStatement("SELECT * FROM csp WHERE hostname LIKE ? LIMIT ?;");
		s.setString(1, '%' + hostname + '%');
		s.setInt(2, count);
		ResultSet rs = s.executeQuery();
		CSPReport[] out = objectsFromResultset(rs);
		s.close();
		return out;
	}

	public static CSPReport[] findReportsByHostname(Connection sql, String hostname) throws SQLException {
		PreparedStatement s = sql.prepareStatement("SELECT * FROM csp WHERE hostname LIKE ?;");
		s.setString(1, '%' + hostname + '%');
		ResultSet rs = s.executeQuery();
		CSPReport[] out = objectsFromResultset(rs);
		s.close();
		return out;
	}

	private static CSPReport[] objectsFromResultset(ResultSet rs) throws SQLException {
		ArrayList<CSPReport> outList = new ArrayList<>();
		while (rs.next()) {
			outList.add(new CSPReport(rs.getString("blocked_uri"), rs.getString("document_uri"),
					rs.getString("effective_directive"), rs.getString("original_policy"), rs.getString("referrer"),
					rs.getInt("status_code"), rs.getString("violated_directive"), rs.getString("source_file"),
					rs.getInt("line_number"), rs.getInt("column_number"), rs.getTimestamp("date").toInstant()));
		}
		rs.close();
		return outList.toArray(new CSPReport[0]);
	}
}
