package us.codecraft.webmagic.selenium.onlineRetailers.handle;

/**
 * @author 丁烽
 * @version 创建时间：2018年10月15日 下午2:29:05
 */
public enum ElementKind {
	radio_Select(RadioSelectHandle.class), lable(LabelHandle.class), select(SelectHandle.class);

	private Class<? extends ElementHandle> clazz;

	ElementKind(Class<? extends ElementHandle> clazz) {
		this.clazz = clazz;
	}

	public ElementHandle buildInstance() throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}

}
