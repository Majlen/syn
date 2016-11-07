package syn.entities;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

public class HPKPReport {
	private Instant date_time;
	private String hostname;
	private int port;
	private Instant effective_expiry;
	private boolean include_subdomains;
	private String noted_hostname;
	private String[] served_chain;
	private String[] validated_chain;
	private String[] known_pins;

	public HPKPReport(Instant date_time, String hostname, int port, Instant effective_expiry, boolean include_subdomains,
	                  String noted_hostname, String[] served_chain, String[] validated_chain, String[] known_pins) {
		this.date_time = date_time;
		this.hostname = hostname;
		this.port = port;
		this.effective_expiry = effective_expiry;
		this.include_subdomains = include_subdomains;
		this.noted_hostname = noted_hostname;
		this.served_chain = served_chain;
		this.validated_chain = validated_chain;
		this.known_pins = known_pins;
	}

	public HPKPReport(Reader json) throws CertificateException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		JsonReader reader = Json.createReader(json);
		JsonObject obj = reader.readObject();
		date_time = Instant.parse(obj.getString("date-time", null));
		hostname = obj.getString("hostname", null);
		port = obj.getInt("port", 0);
		effective_expiry = Instant.parse(obj.getString("effective-expiration-date", Instant.EPOCH.toString()));
		include_subdomains = obj.getBoolean("include-subdomains", false);
		noted_hostname = obj.getString("noted-hostname", null);

		JsonArray jsonArray = obj.getJsonArray("served-certificate-chain");
		served_chain = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			String c = jsonArray.getString(i);
			ByteArrayInputStream is = new ByteArrayInputStream(c.getBytes(Charset.defaultCharset()));
			X509Certificate cert = (X509Certificate)cf.generateCertificate(is);
			served_chain[i] = cert.getSubjectX500Principal().getName();
		}

		jsonArray = obj.getJsonArray("validated-certificate-chain");
		validated_chain = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			String c = jsonArray.getString(i);
			ByteArrayInputStream is = new ByteArrayInputStream(c.getBytes(Charset.defaultCharset()));
			X509Certificate cert = (X509Certificate)cf.generateCertificate(is);
			validated_chain[i] = cert.getSubjectX500Principal().getName();
		}

		jsonArray = obj.getJsonArray("known-pins");
		known_pins = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			known_pins[i] = jsonArray.getString(i);
		}
	}

	public Instant getDate_time() {
		return date_time;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public Instant getEffective_expiry() {
		return effective_expiry;
	}

	public boolean isInclude_subdomains() {
		return include_subdomains;
	}

	public String getNoted_hostname() {
		return noted_hostname;
	}

	public String[] getServed_chain() {
		return served_chain;
	}

	public String[] getValidated_chain() {
		return validated_chain;
	}

	public String[] getKnown_pins() {
		return known_pins;
	}

	public void store(Connection sql) throws SQLException {
		PreparedStatement s = sql.prepareStatement("INSERT INTO hpkp (date_time, hostname, port, effective_expiry," +
				"include_subdomains, noted_hostname, served_chain, validated_chain, known_pins)" +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
		s.setTimestamp(1, Timestamp.from(date_time));
		s.setString(2, hostname);
		s.setInt(3, port);
		s.setTimestamp(4, Timestamp.from(effective_expiry));
		s.setBoolean(5, include_subdomains);
		s.setString(6, noted_hostname);
		s.setString(7, array2JSON(served_chain));
		s.setString(8, array2JSON(validated_chain));
		s.setString(9, array2JSON(known_pins));
		s.executeUpdate();
		s.close();
	}

	public static void createTable(Connection sql) throws SQLException {
		Statement s = sql.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS hpkp (date_time INTEGER, hostname TEXT, port INTEGER," +
				" effective_expiry INTEGER, include_subdomains BOOLEAN, noted_hostname TEXT, served_chain TEXT," +
				" validated_chain TEXT, known_pins TEXT)");
	}

	public static HPKPReport[] findReports(Connection sql, int count) throws SQLException {
		PreparedStatement s = sql.prepareStatement("SELECT * FROM hpkp LIMIT ?;");
		s.setInt(1, count);
		ResultSet rs = s.executeQuery();
		HPKPReport[] out = objectsFromResultset(rs);
		s.close();
		return out;
	}

	public static HPKPReport[] findReports(Connection sql) throws SQLException {
		PreparedStatement s = sql.prepareStatement("SELECT * FROM hpkp;");
		ResultSet rs = s.executeQuery();
		HPKPReport[] out = objectsFromResultset(rs);
		s.close();
		return out;
	}

	public static HPKPReport[] findReportsByHostname(Connection sql, String hostname, int count) throws SQLException {
		PreparedStatement s = sql.prepareStatement("SELECT * FROM hpkp WHERE hostname LIKE ? LIMIT ?;");
		s.setString(1, '%' + hostname + '%');
		s.setInt(2, count);
		ResultSet rs = s.executeQuery();
		HPKPReport[] out = objectsFromResultset(rs);
		s.close();
		return out;
	}

	public static HPKPReport[] findReportsByHostname(Connection sql, String hostname) throws SQLException {
		PreparedStatement s = sql.prepareStatement("SELECT * FROM hpkp WHERE hostname LIKE ?;");
		s.setString(1, '%' + hostname + '%');
		ResultSet rs = s.executeQuery();
		HPKPReport[] out = objectsFromResultset(rs);
		s.close();
		return out;
	}

	private static HPKPReport[] objectsFromResultset(ResultSet rs) throws SQLException {
		ArrayList<HPKPReport> outList = new ArrayList<>();
		while (rs.next()) {
			String[] served_chain = JSON2array(rs.getString("served_chain"));
			String[] validated_chain = JSON2array(rs.getString("validated_chain"));
			String[] known_pins = JSON2array(rs.getString("known_pins"));

			outList.add(new HPKPReport(rs.getTimestamp("date_time").toInstant(), rs.getString("hostname"),
					rs.getInt("port"), rs.getTimestamp("effective_expiry").toInstant(),
					rs.getBoolean("include_subdomains"), rs.getString("noted_hostname"), served_chain, validated_chain,
					known_pins));
		}
		rs.close();
		return outList.toArray(new HPKPReport[0]);
	}

	private static String array2JSON(String[] arr) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (String str: arr) {
			builder.add(str);
		}
		JsonArray json = builder.build();

		StringWriter strWriter = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(strWriter);
		jsonWriter.writeArray(json);
		String out = jsonWriter.toString();

		jsonWriter.close();
		return out;
	}

	private static String[] JSON2array(String json) {
		StringReader strReader = new StringReader(json);
		JsonReader jsonReader = Json.createReader(strReader);

		JsonArray arr = jsonReader.readArray();
		String[] out = new String[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			out[i] = arr.getString(i);
		}

		jsonReader.close();
		strReader.close();
		return out;
	}
}
