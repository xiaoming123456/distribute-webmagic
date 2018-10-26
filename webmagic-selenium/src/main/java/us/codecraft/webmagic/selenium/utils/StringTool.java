package us.codecraft.webmagic.selenium.utils;

public class StringTool {

	/**
	 * 抽取规则字符
	 * 
	 * @param str
	 * @return
	 */
	public static String extract(String str) {
		return str.replace(" ", "").replace("\n", "");
	}

	/**
	 * 替换参数
	 * 
	 * @param str
	 * @param param
	 * @return
	 */
	public static String format(String str, String param) {
		return originFormat(str, param, "%s");
	}

	public static String format(String str, String param1, String param2) {
		String newStr = originFormat(str, param1, "%s");
		newStr = originFormat(newStr, param2, "%f");
		return newStr.toString();
	}

	private static String originFormat(String str, String param, String separator) {
		String[] strs = str.split(separator);
		StringBuffer newStr = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {

			newStr.append(strs[i]);
			if (i < strs.length - 1)
				newStr.append(param);
		}
		if (str.endsWith(separator))
			newStr.append(param);
		return newStr.toString();
	}

	public static void main(String[] args) {
		System.out.println(format("www%sskalfkl%sa%s", "00"));
	}
}
