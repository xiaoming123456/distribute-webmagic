package us.codecraft.webmagic.samples.yunyin;

import us.codecraft.webmagic.selenium.core.SeleniumProcessor;
import us.codecraft.webmagic.selenium.core.SeleniumSpider;
import us.codecraft.webmagic.selenium.data.DataManage;
import us.codecraft.webmagic.selenium.downloader.SeleniumDownloader;
import us.codecraft.webmagic.selenium.downloader.WebEvent;
import us.codecraft.webmagic.selenium.onlineRetailers.StandardWebEvent;
import us.codecraft.webmagic.selenium.onlineRetailers.handle.ElementKind;
import us.codecraft.webmagic.selenium.onlineRetailers.handle.ProductListHandle;
import us.codecraft.webmagic.selenium.onlineRetailers.param.WEBParam;
import us.codecraft.webmagic.selenium.onlineRetailers.param.WEBParam.Strategy;

/**
 *
 * 云印网
 * 
 *
 * 
 */
public class YunyinProcessorGroup extends SeleniumProcessor {
	public String replacePath(String path) {
		if (path.contains("html#keyword")) {
			path = path.replace("html#keyword", "html?keyword");
		}

		return path;
	}

	public static void main(String[] args) {
		regexPathList.add("http://www\\.ininin\\.com/search\\.html#keyword.*");
		regexPathList.add("http://www\\.ininin\\.com/product/\\d+\\.html");
		DataManage.sql = "insert into goodsInfoTest1(url,title,scale,descPrice,typeName,time) values";

		String scaleStr = "var attrMap = {};" + "var attrValArray = null;"
				+ "var  productParam = $('div[id^=\"product_params-\"]');" + "" + "if(productParam.length>0){"
				+ "	productParam.find('div.vals.clearfix').each( function() {"
				+ "			var attr = $(this).attr('data-name').replace(/\\n/g, '').replace(/ /g, '').replace(/\\//g, '丨').replace(/：/g, '');"
				+ "		" + "			attrValArray = new Array();"
				+ "			eval(\"attrMap.\"+attr+\"=attrValArray\");"
				+ "			$(this).find('a.val').each( function() {" + "			"
				+ "			var attrVal = $(this).text();" + "			if(attrVal.search(\"自定义\") == -1)"
				+ "				attrValArray.push(attrVal+\";\"+$(this).attr(\"data-value\"));" + "			});"
				+ "	}); " + "}else{" + "" + "$('div[id^=\"template-product_params\"] span.vals').each( function() {"
				+ "			var attr = $(this).attr('data-name').replace(/\\n/g, '').replace(/ /g, '').replace(/\\//g, '丨').replace(/：/g, '');"
				+ "		" + "			attrValArray = new Array();"
				+ "			eval(\"attrMap.\"+attr+\"=attrValArray\");"
				+ "			$(this).find('a.val').each( function() {" + "			"
				+ "			var attrVal = $(this).text();" + "			if(attrVal.search(\"自定义\") == -1)"
				+ "				attrValArray.push(attrVal+\";\"+$(this).attr(\"data-value\"));" + "			});"
				+ "	}); " + "} " + "return  JSON.stringify(attrMap);";

		String priceStr = "var price = $(\"#data-price\").text();" + "if(!price)"
				+ " price = $('[id^=\"data-discountAmount\"]').text();" + "return price;";
		String chooseStr = "var attrVal = \"\";" + "$(\"a.sel\").each(function (){" + "attrVal+=$(this).text()+\";\";"
				+ "}); return attrVal";
		WEBParam webParam = new WEBParam("云印", priceStr, scaleStr, chooseStr,
				" return $('div.attr.clearfix a.val[data-value=\"%1$s\"]').click().length;", "h1.headline a", "", 200,
				Strategy.wait);
		ProductListHandle productListHandle = new ProductListHandle();
		productListHandle
				.setGetProductStr("var paths= \"\";\n" + "$(\"#template_product_list_view a\").each(function(){\n"
						+ "\n" + "paths +=$(this).attr(\"href\")+\";\";\n" + "}); return paths;");
		productListHandle.setPauseStr(
				"if($(\"#data_curPage\").text()!=\"\"&&$(\"#data_curPage\").text()!=$(\"#data_totalPage\").text()){\n"
						+ "$(\".pagin .after\").click();return  \"true\";\n" + "}else {return\"false\" }");

		webParam.setProductListHandle(productListHandle);
		WebEvent webEvent = new StandardWebEvent().setWEBParam(webParam, ElementKind.lable);
		seleniumSpider = (SeleniumSpider) SeleniumSpider.create(new YunyinProcessorGroup()).thread(0)
				.setDownloader(new SeleniumDownloader(webEvent)).addUrl("http://www.ininin.com/");

		seleniumSpider.runAsync();

		// http://www.ininin.com/
		// http://www.ininin.com/product/200553.html
	}

}
