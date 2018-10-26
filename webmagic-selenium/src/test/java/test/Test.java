package test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * @author 丁烽
 * @version 创建时间：2018年10月25日 下午4:23:49
 */
public class Test {
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("webdriver.gecko.driver", "/data/reptile/geckodriver");
		WebDriver webDriver = new FirefoxDriver();
		webDriver.get("www.baidu.com");
		Thread.sleep(10000);
	}
}
