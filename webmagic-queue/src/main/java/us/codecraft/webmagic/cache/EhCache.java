package us.codecraft.webmagic.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import us.codecraft.webmagic.utils.FilePath;

public class EhCache implements ICache {
	private static final String CONFIG_FILENAME = "ehcache.xml";
	private static final String CACHE_NAME = "taskQueue";
	private CacheManager manager;
	private static final Log log = LogFactory.getLog(EhCache.class);

	public EhCache() {

		File filePath = new File(FilePath.getPath(CONFIG_FILENAME));
		InputStream InputStream = null;
		try {
			InputStream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		manager = CacheManager.create(InputStream);
	}

	public void put(String key, Object value) {
		Cache cache = manager.getCache(CACHE_NAME);
		Element element = new Element(key, value);
		synchronized (this) {
			cache.put(element);
		}
	}

	public Object get(String key) {
		Cache cache = manager.getCache(CACHE_NAME);
		Element element = null;
		synchronized (this) {
			element = cache.get(key);
		}
		return element == null ? null : element.getObjectValue();
	}

}
