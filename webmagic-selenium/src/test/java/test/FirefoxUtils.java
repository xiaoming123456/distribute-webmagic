package test;

import java.io.File;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class FirefoxUtils {
	public static FirefoxDriver getFirefoxDriver(String path) {
		File pathToBinary = new File(path);
		FirefoxBinary firefoxBinary = new FirefoxBinary(pathToBinary);
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		System.setProperty("webdriver.gecko.driver", "C:\\Program Files (x86)\\Mozilla Firefox\\geckodriver.exe");// firefox需要geckodriver驱动，这个，百度找咯
		return new FirefoxDriver(firefoxBinary, firefoxProfile);
	}

	/**
	 * 设置默认Firefox路径，返回FirefoxDriver
	 *
	 * @return FirefoxDriver
	 */
	public static FirefoxDriver getFirefoxDriver() {
		return FirefoxUtils.getFirefoxDriver("C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");// 这里就是自己装的火狐路径呢
	}

}
