package us.codecraft.webmagic.selenium.onlineRetailers.param;

import java.util.HashMap;

import org.openqa.selenium.JavascriptExecutor;

import us.codecraft.webmagic.selenium.utils.ScaleArrayList;
import us.codecraft.webmagic.selenium.utils.ScaleLinkedList;

/**
 * @author 丁烽
 * @version 创建时间：2018年10月15日 上午9:18:43
 */
public class ExecuteParam {
	private String val;// 规格值

	private int index;// 规格索引

	private ThreadLocal<String> selectDocsThread;// 选择的元素

	private ThreadLocal<String> scaleTabThread; // 规格列表

	private JavascriptExecutor js;

	private WEBParam webParam;

	private HashMap<Integer, String> valAttrMap;

	private ScaleArrayList<ScaleLinkedList<String>> layerList;

	private String url;

	private String title;

	private Integer layerLevel;

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ThreadLocal<String> getSelectDocsThread() {
		return selectDocsThread;
	}

	public void setSelectDocsThread(ThreadLocal<String> selectDocsThread) {
		this.selectDocsThread = selectDocsThread;
	}

	public ThreadLocal<String> getScaleTabThread() {
		return scaleTabThread;
	}

	public void setScaleTabThread(ThreadLocal<String> scaleTabThread) {
		this.scaleTabThread = scaleTabThread;
	}

	public JavascriptExecutor getJs() {
		return js;
	}

	public void setJs(JavascriptExecutor js) {
		this.js = js;
	}

	public WEBParam getWebParam() {
		return webParam;
	}

	public void setWebParam(WEBParam webParam) {
		this.webParam = webParam;
	}

	public HashMap<Integer, String> getValAttrMap() {
		return valAttrMap;
	}

	public void setValAttrMap(HashMap<Integer, String> valAttrMap) {
		this.valAttrMap = valAttrMap;
	}

	public ScaleArrayList<ScaleLinkedList<String>> getLayerList() {
		return layerList;
	}

	public void setLayerList(ScaleArrayList<ScaleLinkedList<String>> layerList) {
		this.layerList = layerList;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getLayerLevel() {
		return layerLevel;
	}

	public void setLayerLevel(Integer layerLevel) {
		this.layerLevel = layerLevel;
	}

}
