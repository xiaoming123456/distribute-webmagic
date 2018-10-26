package us.codecraft.webmagic.selenium.onlineRetailers.param;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.selenium.onlineRetailers.handle.ProductListHandle;

public class WEBParam {
	// 价格
	private String priceStr;
	// 规格元素
	private String scaleStr;
	// 比较字符
	private String compareDoc;
	// 等待时间
	private long waitTime;
	// 策略
	private Strategy strategy;
	// 点击字符串
	private String clickStr;

	// 标题
	private String titleDoc;
	// 选中节点
	private String chooseStr;
	// 选中select节点
	private String selectChooseStr;

	// 分类类型
	private String typeName;

	private ProductListHandle productListHandle;

	public WEBParam(String typeName, String priceStr, String scaleStr, String chooseStr, String clickStr,
			String titleDoc, String compareDoc, long waitTime, Strategy strategy) {
		if (strategy == null) {
			throw new NullPointerException("没有默认的策略");
		}
		if (StringUtils.isEmpty(priceStr) || StringUtils.isEmpty(scaleStr) || StringUtils.isEmpty(chooseStr)
				|| StringUtils.isEmpty(clickStr) || StringUtils.isEmpty(titleDoc) || waitTime == 0) {
			throw new RuntimeException("初始参数异常");
		}

		if (strategy == Strategy.compare && StringUtils.isEmpty(compareDoc)) {
			throw new RuntimeException("比较策略没有比较字符串 ");
		}

		this.priceStr = priceStr;
		this.scaleStr = scaleStr;
		this.clickStr = clickStr;
		this.titleDoc = titleDoc;
		this.chooseStr = chooseStr;
		this.compareDoc = compareDoc;
		this.waitTime = waitTime;
		this.strategy = strategy;

	}

	public String getPriceStr() {
		return priceStr;
	}

	public String getScaleStr() {
		return scaleStr;
	}

	public String getCompareDoc() {
		return compareDoc;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public String getTitleDoc() {
		return titleDoc;
	}

	public String getChooseStr() {
		return chooseStr;
	}

	public String getClickStr() {
		return clickStr;
	}

	public String getSelectChooseStr() {
		return selectChooseStr;
	}

	public void setSelectChooseStr(String selectChooseStr) {
		this.selectChooseStr = selectChooseStr;
	}

	public String getTypeName() {
		return typeName;
	}

	public ProductListHandle getProductListHandle() {
		return productListHandle;
	}

	public void setProductListHandle(ProductListHandle productListHandle) {
		this.productListHandle = productListHandle;
	}

	public enum Strategy {
		wait, compare;
	}
}
