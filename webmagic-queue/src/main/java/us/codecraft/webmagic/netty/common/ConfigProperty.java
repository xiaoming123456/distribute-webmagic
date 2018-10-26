package us.codecraft.webmagic.netty.common;

import java.io.FileReader;
import java.util.Properties;

import us.codecraft.webmagic.utils.FilePath;

public class ConfigProperty {
	private static final String CONFIG_FILENAME = "service.ini";
	public static Properties serviceConfig;
	static {
		serviceConfig = new Properties();
		String configFile = FilePath.getPath(CONFIG_FILENAME);
		try {
			serviceConfig.load(new FileReader(configFile));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
