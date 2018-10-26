package us.codecraft.webmagic.selenium.data;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import us.codecraft.webmagic.utils.FilePath;
import us.codecraft.webmagic.utils.LogException;

//因为impala 只有查 新增 所以只写了2个方法
public class JDBCModel {
	private static final String CONFIG_FILENAME = "data.ini";
	public static Log log = LogFactory.getLog(JDBCModel.class);

	private static Properties properties = null;
	private static Connection con = null;
	static {
		properties = new Properties();
		String configFile = FilePath.getPath(CONFIG_FILENAME);
		try {
			properties.load(new FileReader(configFile));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogException.appendExceptionLog(log, e);
		}

		try {
			Class.forName(properties.getProperty("JDBC_DRIVER"));
			con = DriverManager.getConnection(properties.getProperty("CONNECTION_URL"), properties.getProperty("NAME"),
					properties.getProperty("PASSWORD"));
		} catch (Exception e) {
			LogException.appendExceptionLog(log, e);
		}
	}

	public static List<ArrayList<String>> query(String sql) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		List<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		try {

			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			int columnNum = rs.getMetaData().getColumnCount();
			while (rs.next()) {

				ArrayList<String> rowList = new ArrayList<String>();
				for (int i = 1; i <= columnNum; i++) {
					rowList.add(rs.getString(i));
				}
				dataList.add(rowList);
				// System.out.println(rs.getString(1) + '\t' + rs.getLong(2));
			}
		} catch (Exception e) {
			LogException.appendExceptionLog(log, e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LogException.appendExceptionLog(log, e);
			}

		}
		return dataList;
	}

	public static void insert(String sql) {

		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) con.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LogException.appendExceptionLog(log, e);
			}
		}
	}

	/**
	 * impala 不支持批处理
	 * 
	 * @param sql
	 * @param dataList
	 */
	public static void insertBatch(String sql, List<ArrayList<String>> dataList) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < dataList.size(); i++) { // i=1000 2000
				ArrayList<String> rowList = dataList.get(i);
				for (int j = 1; j <= rowList.size(); j++) {
					ps.setString(j, rowList.get(j - 1));
				}

				ps.addBatch();
				if (i % 500 == 0) {
					ps.executeBatch();
					ps.clearBatch();
				}
			}
			ps.executeBatch();

		} catch (SQLException e) {
			LogException.appendExceptionLog(log, e);
		} finally {
			try {

				ps.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LogException.appendExceptionLog(log, e);
			}

		}

	}

	public static void main(String[] args) {
		// System.out.println(query("select *from goods limit 10").size());
		long beginTime = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer();
		sb.append("insert into deed(ip) values('123')");
		for (int j = 0; j < 10000; j++) {
			sb.append(",('123')");
		}
		insert(sb.toString());
		System.out.println(System.currentTimeMillis() - beginTime);

	}
}
