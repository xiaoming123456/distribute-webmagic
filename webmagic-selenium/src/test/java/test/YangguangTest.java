package test;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.alibaba.fastjson.JSONArray;

import us.codecraft.webmagic.selenium.utils.ScaleLinkedList;
import us.codecraft.webmagic.selenium.utils.ScaleLinkedList.Node;
import us.codecraft.webmagic.selenium.utils.ScaleLinkedList.Position;

public class YangguangTest {
	private static final ThreadLocal<String> attrTread = new ThreadLocal<String>();
	private static final ThreadLocal<Integer> craftNumTread = new ThreadLocal<Integer>();

	public static void main(String[] args) throws InterruptedException {
		FirefoxDriver firefoxDriver = FirefoxUtils.getFirefoxDriver();
		firefoxDriver.get("http://mall.easypnp.com/ProClass/CalcPrice.aspx?id=3");
		//
		// WebElement webelement =
		// firefoxDriver.findElements(By.className("order_ulist")).get(2);
		// System.out.println(webelement.getText());
		JavascriptExecutor js = (JavascriptExecutor) firefoxDriver;
		int craftNum = 0;
		System.out.println(js.executeScript("return $('h3.orderHb_s1_tit').text();").toString());

		String nameList = js.executeScript("var attrVal =[];" + "var parName ='';" + "var attr = [];" + "var num = 0;"
				+ "var craftNum = 0;attr.push(attrVal);" + "$('.o_site_rlist_option').find('a').each( function() { "
				+ "if($(this).attr('pn').search('自定义') != -1)" + "	return;"
				+ "var curParName =  $(this).attr('psname');  " + "if(parName!=''&&parName !=curParName){" + "num++;"
				+ "if(curParName=='工艺')" + "craftNum = num;" + "attrVal = [];" + "attr.push(attrVal);" + "}"
				+ "parName = curParName;" + "attrVal.push( $(this).attr('parval'));" + "});" + "" + ""
				+ "attr.push(craftNum);" + " return  JSON.stringify(attr);").toString();

		String compareEle = "return $('#norms_id').html();";
		String attrTable = js.executeScript(compareEle).toString();
		System.out.println(attrTable);
		System.out.println(nameList);
		attrTread.set(attrTable);
		List<ScaleLinkedList<String>> layerList = new ArrayList<>();

		ScaleLinkedList.Execute<String> execute = (e, n, index) -> {
			// TODO Auto-generated method stub
			int size = layerList.size() - 1;
			boolean needClick = false;
			// 工艺需要进行特殊处理
			if (craftNumTread.get() == n) {

				Position position = layerList.get(n).position;
				Node<String> preNode = null;
				if (position == Position.head) {
					preNode = e.prev;
				} else if (position == Position.last) {
					preNode = e.next;
				}
				// 清空工艺的上个节点
				if (preNode != null)
					js.executeScript("$('[parval=\"" + preNode.item + "\"]').attr('class','');");
				needClick = true;
			} else if (index != 0) {

				needClick = true;
				js.executeScript("$('a[psname=\"工艺\"]').attr('class','');");
			}

			// 左右2边的初始位置不进行点击
			if (needClick) {
				click(e.item, js);
				try {
					firefoxDriver.switchTo().alert().accept();
				} catch (Exception e2) {
					// TODO: handle exception
				}

				attrTread.set(waitRequest(attrTread.get(), compareEle, js));

				System.out.println(js.executeScript("return $('#countRes').text();").toString());

			}
			if (n != size) {
				layerList.get(n + 1).iterate(n + 1);
			}
		};

		JSONArray scaleJson = JSONArray.parseArray(nameList);
		craftNum = scaleJson.getIntValue(scaleJson.size() - 1);
		craftNumTread.set(craftNum);
		for (int j = 0; j < scaleJson.size() - 1; j++) {

			JSONArray attrValArray = scaleJson.getJSONArray(j);
			ScaleLinkedList<String> attrValList = new ScaleLinkedList<String>(execute);
			for (int i = 0; i < attrValArray.size(); i++) {
				String attrData = attrValArray.get(i).toString();

				attrValList.add(attrData);
				// 最后工艺不涉及第一默认规则
				if (i == 0 && j != craftNum) {
					click(attrData, js);
				}
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

	public static void click(String attrData, JavascriptExecutor js) {
		js.executeScript("$('[parval=\"" + attrData + "\"]').click();");
		// 确定按钮事件
		js.executeScript("$('div[class^=\"tooltip_info\"]').each(function(){" + "if($(this).css('display')=='block')"
				+ "$(this).find('input[class^=\"zy_but\"]').click();" + "});");
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
