package us.codecraft.webmagic.selenium.onlineRetailers.param;

/**
 * @author 丁烽
 * @version 创建时间：2018年10月15日 上午8:59:22
 */
public class ResultParam {
	private String scale; // 规格

	private boolean storage;// 是否存储

	private boolean deepLayer;// 是否进入下层循环

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public boolean isStorage() {
		return storage;
	}

	public void setStorage(boolean storage) {
		this.storage = storage;
	}

	public boolean isDeepLayer() {
		return deepLayer;
	}

	public void setDeepLayer(boolean deepLayer) {
		this.deepLayer = deepLayer;
	}

}
