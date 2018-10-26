package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UpdateMeta {
	static String JDBC_DRIVER = "com.cloudera.impala.jdbc41.Driver";
	static String CONNECTION_URL = "jdbc:impala://47.98.174.199:21050/default";

	public static void main(String[] args) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(CONNECTION_URL, "hadoop", "dingfeng123");
			ps = con.prepareStatement("select *from goods limit 10");
			rs = ps.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getString(1) + '\t' + rs.getLong(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭rs、ps和con
		}
	}
}