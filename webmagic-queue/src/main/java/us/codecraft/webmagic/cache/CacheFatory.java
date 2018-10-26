package us.codecraft.webmagic.cache;

public class CacheFatory {
	private static ICache cache;

	public static ICache SingleCache() {
		if (cache == null) {
			synchronized (CacheFatory.class) {
				if (cache == null) {
					cache = new EhCache();
				}
			}
		}
		return cache;
	}

}
