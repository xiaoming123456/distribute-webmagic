package us.codecraft.webmagic.cache;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

/**
 * @author 丁烽
 * @version 创建时间：2018年10月26日 上午9:19:00
 */
public class Test {
	public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
		// File f = new File("");
		// InputStream InputStream = new FileInputStream(f);
		ICache cache = CacheFatory.SingleCache();
		cache.put("a", 123);
		System.out.println(cache.get("a"));
	}
}
