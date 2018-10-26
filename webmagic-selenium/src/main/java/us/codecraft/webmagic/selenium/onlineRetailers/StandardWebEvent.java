package us.codecraft.webmagic.selenium.onlineRetailers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selenium.core.SeleniumPage;
import us.codecraft.webmagic.selenium.data.DataManage;
import us.codecraft.webmagic.selenium.downloader.WebEvent;
import us.codecraft.webmagic.selenium.onlineRetailers.handle.ElementHandle;
import us.codecraft.webmagic.selenium.onlineRetailers.handle.ElementKind;
import us.codecraft.webmagic.selenium.onlineRetailers.handle.ProductListHandle;
import us.codecraft.webmagic.selenium.onlineRetailers.param.ExecuteParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.ResultParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.WEBParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.WEBParam.Strategy;
import us.codecraft.webmagic.selenium.utils.ScaleArrayList;
import us.codecraft.webmagic.selenium.utils.ScaleLinkedList;
import us.codecraft.webmagic.selenium.utils.StringTool;

/**
 * 标准事件
 * 
 * @author 丁烽
 *
 */
public class StandardWebEvent extends WebEvent {

	private static final ThreadLocal<String> scaleTabThread = new ThreadLocal<String>();
	private static final ThreadLocal<String> selectDocsThread = new ThreadLocal<String>();

	private static final ThreadLocal<ElementHandle> elementThread = new ThreadLocal<ElementHandle>();

	public static Log log = LogFactory.getLog(StandardWebEvent.class);

	// 特殊标识分割符v

	private WEBParam webParam;

	private ElementKind elementKind;

	public StandardWebEvent setWEBParam(WEBParam webParam, ElementKind elementKind) {
		if (webParam == null || elementKind == null) {
			throw new NullPointerException("没有初始化");
		}
		this.webParam = webParam;
		this.elementKind = elementKind;
		return this;
	}

	@Override
	public Page onEvent(WebDriver webDriver) {
		SeleniumPage seleniumPage = new SeleniumPage();
		ProductListHandle productListHandle = webParam.getProductListHandle();
		JavascriptExecutor js = (JavascriptExecutor) webDriver;

		String title = getDocStr(webParam.getTitleDoc(), js);
		String url = webDriver.getCurrentUrl();
		String defaultPrice = "";

		try {
			webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
			defaultPrice = getJSStr(webParam.getPriceStr(), js);
			if (StringUtils.isEmpty(defaultPrice)) {
				if (productListHandle != null) {
					ExecuteParam executeParam = new ExecuteParam();
					executeParam.setJs(js);
					return productListHandle.execute(executeParam);
				} else {
					System.out.println("error~" + url);
					log.error("error~" + url);
					return seleniumPage;
				}
			}

		} catch (Exception e) {
			return seleniumPage;
		}

		HashMap<Integer, String> valAttrMap = new HashMap<>();

		// js脚本代码禁止//来注释
		String scaleList = "";
		try {
			scaleList = js.executeScript(webParam.getScaleStr()).toString();
		} catch (Exception e2) {
			// TODO: handle exception
			e2.printStackTrace();
			System.out.println("error~" + url);
			log.error("error~" + url);
			return seleniumPage;
		}

		String attrTable = null;
		if (webParam.getStrategy() == Strategy.compare) {
			attrTable = getDocStr(webParam.getCompareDoc(), js);
			scaleTabThread.set(attrTable);
		}
		ScaleArrayList<ScaleLinkedList<String>> layerList = new ScaleArrayList<>();

		ScaleLinkedList.Execute<String> scaleExecute = (element, layerLevel, index) -> {
			// TODO Auto-generated method stub
			String val = element.item;
			String scale = "";
			boolean storage = false;// 满足入库条件
			boolean deepLayer = false;// 是否进入下一层

			ElementHandle elementHandle = elementThread.get();
			if (elementHandle == null) {
				try {
					elementHandle = elementKind.buildInstance();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				elementThread.set(elementHandle);
			}
			// 获得属性所在的行，用来获得属性名

			// element.executeElement(val, index);
			ExecuteParam executeParam = new ExecuteParam();
			executeParam.setIndex(index);
			executeParam.setJs(js);
			executeParam.setLayerList(layerList);
			executeParam.setScaleTabThread(scaleTabThread);
			executeParam.setSelectDocsThread(selectDocsThread);
			executeParam.setTitle(title);
			executeParam.setUrl(url);
			executeParam.setVal(val);
			executeParam.setValAttrMap(valAttrMap);
			executeParam.setWebParam(webParam);

			ResultParam result = elementHandle.execute(executeParam);
			storage = result.isStorage();
			deepLayer = result.isDeepLayer();
			scale = result.getScale();

			if (storage) {
				scale += ElementHandle.getChooseScale(selectDocsThread.get(), valAttrMap, webParam, js);
				// System.out.println(scale);
				String price = getJSStr(webParam.getPriceStr(), js);
				DataManage.insertVertor.add("('" + url + "','" + title + "','" + scale + "','" + price + "','"
						+ webParam.getTypeName() + "',from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss')),");

			}

			if (deepLayer) {
				layerList.deepLayer(layerLevel);
			}
		};

		// 拼装规格
		JSONObject scaleJson = JSONObject.parseObject(scaleList, Feature.OrderedField);
		Iterator<String> attrItr = scaleJson.keySet().iterator();
		int rowNum = -1;
		String num = "";
		while (attrItr.hasNext()) {
			String attr = attrItr.next();
			// 有效的select
			if (attr.equals(ElementHandle.SELECT_DOCS)) {
				selectDocsThread.set(scaleJson.getString(attr));
				continue;
			}
			rowNum++;
			valAttrMap.put(rowNum, attr);
			JSONArray attrValArray = scaleJson.getJSONArray(attr);
			ScaleLinkedList<String> attrValList = new ScaleLinkedList<String>(scaleExecute);
			boolean click = false;
			for (int i = 0; i < attrValArray.size(); i++) {
				String attrData = attrValArray.get(i).toString();
				attrData = StringTool.extract(attrData);
				String[] attrVal = attrData.split(";");
				if (attrVal.length > 1) {
					attrValList.add(attrData + ElementHandle.ROW + rowNum);

					if (!attrVal[1].contains(ElementHandle.RADIO) && !attrVal[1].contains(ElementHandle.SELECT)
							&& !click) {
						num = getClick(attrVal[1], String.valueOf(rowNum), webParam, js);
						if (num.equals("1")) {
							click = true;
						}
					}
				}

			}

			if (attrValList.getSize() > 0)
				layerList.add(attrValList);
		}

		String scale = ElementHandle.getChooseScale(selectDocsThread.get(), valAttrMap, webParam, js);
		DataManage.insertVertor.add("('" + url + "','" + title + "','" + scale + "','" + defaultPrice + "','"
				+ webParam.getTypeName() + "',from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss')),");

		// 开始启动
		layerList.start();
		// String price = js.executeScript("return $('#hbPrice').text();").toString();
		// String price = firefoxDriver.findElement(By.id("hbPrice")).getText();
		// System.out.println(price);
		return seleniumPage;
	}

	/**
	 * 点击并获得可点击的元素个数
	 * 
	 * @param val
	 * @param webParam
	 * @param js
	 * @return
	 */
	protected static String getClick(String val, String row, WEBParam webParam, JavascriptExecutor js) {
		String num = "";
		try {
			String clickStr = String.format(webParam.getClickStr(), val, row);
			num = js.executeScript(clickStr).toString();
		} catch (Exception e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}
		return num;

	}

	public void getOptionArray(String scaleVal, String attr, JavascriptExecutor js, String url, String title,
			String scale, ScaleArrayList<ScaleLinkedList<String>> previousList, int previousN) {
		String index = scaleVal.substring(ElementHandle.SELECT.length(), scaleVal.length());
		ScaleArrayList<ScaleLinkedList<String>> selectList = new ScaleArrayList<>();
		// option点击事件
		ScaleLinkedList.Execute<String> optionExecute = (e, n, i) -> {
			if (i != 0) {

				String optionEvent = "var selSelect = $($(\"select\")[" + index + "]).parent();\r\n"
						+ "var selOption;\r\n" + "selSelect.find(\"option\").each(function (){\r\n"
						+ "if($(this).text()=='" + e.item + "'){\r\n" + "selOption = $(this);\r\n" + "}\r\n" + "});\r\n"
						+ "selSelect.find(\"option\").prop(\"selected\",false);\r\n selOption.prop(\"selected\",true);\r\n"
						+ "selOption.focus();\r\n selOption.parent().trigger(\"change\");\r\n ";

				// + "var optionVal = \"\";\r\n"
				// + "selSelect.find(\"select option:selected\").each(function (){\r\n"
				// + " optionVal+=\"、\"+$(this).text();\r\n" + "});\r\n" + "return optionVal;";
				// String optionVal =
				js.executeScript(optionEvent);

				// String newscale = scale + attr + ":" + optionVal.substring(1,
				// optionVal.length());
				String price = getJSStr(webParam.getPriceStr(), js);
				DataManage.insertVertor.add("('" + url + "','" + title + "','" + scale + "','" + price + "','"
						+ webParam.getTypeName() + "',from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss')),");
			}

			if (n == selectList.size() - 1) {
				previousList.deepLayer(previousN);

			}

			selectList.deepLayer(n);
		};

		String selectjs = "var selectArray = [];\r\n" + "var optionArray;\r\n" + "$($(\"select\")[" + index
				+ "]).parent().find(\"select\").each(function (){\r\n" + "optionArray = [];\r\n"
				+ "selectArray.push(optionArray);\r\n" + "$(this).find(\"option\").each(function(){\r\n"
				+ "optionArray.push($(this).text());\r\n" + "});\r\n" + "}); return JSON.stringify(selectArray);";
		String selectJson = js.executeScript(selectjs).toString();
		JSONArray selectArray = JSONArray.parseArray(selectJson);

		for (int i = 0; i < selectArray.size(); i++) {
			JSONArray opitonArray = selectArray.getJSONArray(i);
			ScaleLinkedList<String> opitonList = new ScaleLinkedList<String>(optionExecute);
			for (int j = 0; j < opitonArray.size(); j++) {
				opitonList.add(opitonArray.getString(j));
			}
			if (opitonList.getSize() > 0) {
				selectList.add(opitonList);
			}

		}

		selectList.start();
	}

	public void getOptionArray(String index, JavascriptExecutor js, String url, String title, String scale,
			String sumScale, ScaleArrayList<ScaleLinkedList<String>> previousList, int previousN) {
		ScaleArrayList<ScaleLinkedList<String>> selectList = new ScaleArrayList<>();
		// option点击事件
		ScaleLinkedList.Execute<String> optionExecute = (e, n, i) -> {
			if (i != 0) {
				String optionEvent = "var selSelect = $($(\"input[type='checkbox']\")[" + index + "]).parent();\r\n"
						+ "var selOption;\r\n" + "selSelect.find(\"option\").each(function (){\r\n"
						+ "if($(this).text()=='" + e.item + "'){\r\n" + "selOption = $(this);\r\n" + "}\r\n" + "});\r\n"
						+ "\r\n" + "selSelect.find(\"option\").prop(\"selected\",true);"
						+ "selOption.prop(\"selected\",\"selected\");\r\n"
						+ "selOption.parent().trigger(\"change\");\r\n" + "var optionVal = \"\";\r\n"
						+ "selSelect.find(\"select option:selected\").each(function (){\r\n"
						+ "	optionVal+=\"、\"+$(this).text();\r\n" + "});\r\n" + "return optionVal;";
				String optionVal = js.executeScript(optionEvent).toString();
				String newScale = scale + optionVal + ";" + sumScale;
				String price = getJSStr(webParam.getPriceStr(), js);
				DataManage.insertVertor.add("('" + url + "','" + title + "','" + newScale + "','" + price + "','"
						+ webParam.getTypeName() + "',from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss')),");
			}
			if (n == selectList.size() - 1) {
				previousList.deepLayer(previousN);

			}
			selectList.deepLayer(n);
		};

		String selectjs = "var selectArray = [];\r\n" + "var optionArray;\r\n" + "$($(\"input[type='checkbox']\")["
				+ index + "]).parent().find(\"select\").each(function (){\r\n" + "optionArray = [];\r\n"
				+ "selectArray.push(optionArray);\r\n" + "$(this).find(\"option\").each(function(){\r\n"
				+ "optionArray.push($(this).text());\r\n" + "});\r\n" + "}); return JSON.stringify(selectArray);";
		String selectJson = js.executeScript(selectjs).toString();
		JSONArray selectArray = JSONArray.parseArray(selectJson);

		for (int i = 0; i < selectArray.size(); i++) {
			JSONArray opitonArray = selectArray.getJSONArray(i);
			ScaleLinkedList<String> opitonList = new ScaleLinkedList<String>(optionExecute);
			for (int j = 0; j < opitonArray.size(); j++) {
				opitonList.add(opitonArray.getString(j));
			}
			if (opitonList.getSize() > 0) {
				selectList.add(opitonList);
			}

		}

		selectList.start();
	}

	/**
	 * @param attrTable
	 *            比较的文本
	 * @param compareStr
	 *            html比较元素
	 * @param js
	 * @return
	 */
	protected static String waitRequest(String attrTable, WEBParam webParam, JavascriptExecutor js) {
		if (webParam.getStrategy() == Strategy.wait) {
			sleep(webParam.getWaitTime());

		} else if (webParam.getStrategy() == Strategy.compare) {
			for (int i = 0; i < 200; i++) {
				sleep(webParam.getWaitTime());

				String newAttrTable = getDocStr(webParam.getCompareDoc(), js);
				newAttrTable = StringTool.extract(newAttrTable);
				if (!attrTable.equals(newAttrTable)) {
					// System.out.println("sleep:" + (i + 1) * 5);
					attrTable = newAttrTable;
					break;
				}

			}
		}
		return attrTable;
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获得文档文本
	 * 
	 * @param doc
	 * @param js
	 * @return
	 */
	public static String getDocStr(String doc, JavascriptExecutor js) {
		return StringTool.extract(js.executeScript("return $('" + doc + "').text();").toString());
	}

	public static String getJSStr(String str, JavascriptExecutor js) {
		return StringTool.extract(js.executeScript(str).toString());
	}
}
