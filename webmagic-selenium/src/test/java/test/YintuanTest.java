package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.selenium.utils.ScaleLinkedList;

public class YintuanTest {
	private static final ThreadLocal<String> attrTread = new ThreadLocal<String>();

	public static void main(String[] args) throws InterruptedException {
		FirefoxDriver firefoxDriver = FirefoxUtils.getFirefoxDriver();
		firefoxDriver.get("https://www.intuan.com/product/makeup/47");
		//
		// WebElement webelement =
		// firefoxDriver.findElements(By.className("order_ulist")).get(2);
		// System.out.println(webelement.getText());
		JavascriptExecutor js = (JavascriptExecutor) firefoxDriver;

		System.out.println(js.executeScript("return $('h3.orderHb_s1_tit').text();").toString());

		String nameList = js.executeScript(
				"var attrMap = {};" + "var attrValArray = null;" + "$('li.order_ulist').each( function() {"
						+ " var attr = $(this).attr('attrtype');" + "var data = $(this).attr('data');"
						+ "if(!eval(\"attrMap.\"+attr)&&attr){" + "attrValArray = new Array();"
						+ "eval(\"attrMap.\"+attr+\"=attrValArray\");" + "}" + "var attrVal = $(this).text();"
						+ "if($(this).text().search(\"自定义\") == -1&&$(this).text().search(\"\\n\") == -1)"
						+ "attrValArray.push($(this).text()+\";\"+data);" + "}); " + ""// alert(attrMap.size);
						+ "" + " return  JSON.stringify(attrMap);")
				.toString();

		String compareEle = "return $('#attrTable').html();";
		String attrTable = js.executeScript(compareEle).toString();
		System.out.println(attrTable);
		System.out.println(nameList);
		attrTread.set(attrTable);
		List<ScaleLinkedList<String>> layerList = new ArrayList<>();

		ScaleLinkedList.Execute<String> execute = (e, n, index) -> {
			// TODO Auto-generated method stub
			if (index != 0) {

				if (e.item.indexOf(";") != -1) {

					String[] attrVal = e.item.split(";");
					js.executeScript("$('li.order_ulist[data=\"" + attrVal[1] + "\"]').click();");
					attrTread.set(waitRequest(attrTread.get(), compareEle, js));
					System.out.println(attrVal[0]);
					System.out.println(js.executeScript("return $('#hbPrice').text();").toString());
				}
			}
			if (n != layerList.size() - 1) {
				layerList.get(n + 1).iterate(n + 1);
			}
		};

		JSONObject scaleJson = JSONObject.parseObject(nameList);
		Iterator<String> attrItr = scaleJson.keySet().iterator();
		while (attrItr.hasNext()) {
			String attr = attrItr.next();
			JSONArray attrValArray = scaleJson.getJSONArray(attr);
			ScaleLinkedList<String> attrValList = new ScaleLinkedList<String>(execute);
			for (int i = 0; i < attrValArray.size(); i++) {
				String attrData = attrValArray.get(i).toString();
				attrValList.add(attrData);
			}
			layerList.add(attrValList);
		}
		// 开始启动
		layerList.get(0).iterate(0);
		// String price = js.executeScript("return $('#hbPrice').text();").toString();
		// String price = firefoxDriver.findElement(By.id("hbPrice")).getText();
		// System.out.println(price);

		System.out.println(attrTable);
		firefoxDriver.close();
	}

	/**
	 * @param attrTable
	 *            比较的文本
	 * @param compareStr
	 *            html比较元素
	 * @param js
	 * @return
	 */
	private static String waitRequest(String attrTable, String compareEle, JavascriptExecutor js) {
		for (int i = 0; i < 200; i++) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String newAttrTable = js.executeScript(compareEle).toString();
			if (!attrTable.equals(newAttrTable)) {
				System.out.println("sleep:" + (i + 1) * 5);
				attrTable = newAttrTable;
				break;
			}

		}
		return attrTable;
	}
}
