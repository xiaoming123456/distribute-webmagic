package us.codecraft.webmagic.utils;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FilePath {

	public static Log log = LogFactory.getLog(FilePath.class);
	private static FilePath filePath = new FilePath();

	public static String getPath(String FileName) {
		String loaderPath = null;
		try {
			URL url = FilePath.class.getClassLoader().getResource(FileName);
			if (url != null) {
				loaderPath = FilePath.class.getClassLoader().getResource(FileName).getPath();
			} else {
				String jarPath = filePath.getClass().getResource("").getPath();
				int jarIndex = jarPath.indexOf(".jar");
				if (jarIndex != -1) {
					jarPath = jarPath.substring(0, jarPath.indexOf(".jar"));
					jarPath = jarPath.substring(0, jarPath.lastIndexOf("/"));
					jarPath = jarPath.replace("file:", "");

				}

				loaderPath = jarPath + "/" + FileName;
			}
			// loaderPath = System.getProperty("user.dir");
			loaderPath = java.net.URLDecoder.decode(loaderPath, "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogException.appendExceptionLog(log, e);
		}
		return loaderPath;
	}

}
