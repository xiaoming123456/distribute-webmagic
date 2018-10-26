package us.codecraft.webmagic.cache;

public interface ICache {
	void put(String key, Object value);

	Object get(String key);
}
