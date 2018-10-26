package us.codecraft.webmagic.selenium.utils;

import java.util.ArrayList;

public class ScaleArrayList<T extends ScaleLinkedList<?>> extends ArrayList<ScaleLinkedList<?>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1562117852053961032L;

	/**
	 * 启动循环
	 */
	public void start() {
		if (this.size() > 0) {
			this.get(0).iterate(0);
		}

	}

	/**
	 * 执行下一层逻辑
	 */
	public void deepLayer(int n) {
		if (n != this.size() - 1) {
			this.get(n + 1).iterate(n + 1);
		}
	}

}
