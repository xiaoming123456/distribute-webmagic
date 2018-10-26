package us.codecraft.webmagic.selenium.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

public class DataVector<E> extends Vector<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8051953755941391025L;

	public DataVector() {
		super(10);
	}

	public DataVector(Collection<? extends E> c) {
		synchronized (DataVector.class) { // 此处目的为了多实例共享唯一锁
			elementData = c.toArray();
			elementCount = elementData.length;
			// c.toArray might (incorrectly) not return Object[] (see 6260652)
			if (elementData.getClass() != Object[].class)
				elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
			c.clear(); // 改变源码后额外增加的清空
		}
	}

	public boolean add(E e) {
		synchronized (DataVector.class) {
			super.add(e);
		}
		return true;
	}

}
