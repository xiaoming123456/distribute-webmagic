package us.codecraft.webmagic.selenium.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author 丁烽
 * @version 创建时间：2018年10月24日 下午3:39:40
 */

public class Test {
	public static void main(String[] args) {
		new Test().getPath();
		PropertyConfigurator.configure(" C:\\Users\\Administrator\\Desktop\\爬虫\\log4j.xml ");
		Log log = LogFactory.getLog(Test.class);
		log.error("1");
	}

	public void getPath() {
		System.out.println(this.getClass().getResource(""));
		System.out.println(Test.class.getClassLoader().getResource(""));
	}
}
