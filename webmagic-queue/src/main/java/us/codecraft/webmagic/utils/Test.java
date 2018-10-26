package us.codecraft.webmagic.utils;

import java.io.FileNotFoundException;

/**
 * @author 丁烽
 * @version 创建时间：2018年10月24日 下午3:39:40
 */

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		new Test().getPath();
		// PropertyConfigurator.configure("C:/Users/Administrator/Desktop/爬虫/log4j.properties");
		System.out.println();
		// new FileReader("C:\\Users\\Administrator\\Desktop\\爬虫/config.ini ");

		// Log log = LogFactory.getLog(Test.class);
		// log.error("123");
		// System.getProperty("user.dir");
		// System.out.println(System.getProperty("java.class.path"));
	}

	public void getPath() {
		String jarPath = this.getClass().getResource("").getPath();
		int jarIndex = jarPath.indexOf(".jar");
		if (jarIndex != -1) {
			jarPath = jarPath.substring(0, jarPath.indexOf(".jar"));
			jarPath = jarPath.substring(0, jarPath.lastIndexOf("/"));
			jarPath.replace("file:", "");
		}
		System.out.println(jarPath);
		System.out.println(Test.class.getClassLoader().getResource(""));
	}
}
