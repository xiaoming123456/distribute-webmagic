package us.codecraft.webmagic.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import us.codecraft.webmagic.cache.CacheFatory;

public class TaskQueue<E> extends Vector<E> {

	private static final long serialVersionUID = 1L;

	/**
	 * 根据业务情况每次消费记录
	 * 
	 * @param length
	 * @return
	 */
	public synchronized List<E> getQueue(int length) {
		// 长度为0提取全部数据
		if (length == 0)
			length = elementCount;
		length = elementCount > length ? length : elementCount;
		List<E> frontData = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			frontData.add(super.get(i));
			super.remove(i);
			i--;
			length--;
		}

		return frontData;
	}

	/**
	 * 添加未消费记录进入数组 服务端方法 将使用缓存
	 * 
	 * @param c
	 * @return
	 */
	public synchronized boolean addServiceAll(E[] c) {
		if (c == null)
			return false;
		ArrayList<E> validList = new ArrayList<>();
		for (int i = 0; i < c.length; i++) {
			String str = (String) c[i];
			// 缓存比较
			if (StringUtils.isNotEmpty(str) && CacheFatory.SingleCache().get(str) == null) {
				validList.add(c[i]);
				CacheFatory.SingleCache().put(str, 1);
			}
		}

		return super.addAll(validList);
	}

	public synchronized boolean addClientAll(E[] a) {
		modCount++;
		int numNew = a.length;
		ensureCapacityHelper(elementCount + numNew);
		System.arraycopy(a, 0, elementData, elementCount, numNew);
		elementCount += numNew;
		return numNew != 0;
	}

	private void ensureCapacityHelper(int minCapacity) {
		// overflow-conscious code
		if (minCapacity - elementData.length > 0)
			grow(minCapacity);
	}

	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	private void grow(int minCapacity) {
		// overflow-conscious code
		int oldCapacity = elementData.length;
		int newCapacity = oldCapacity + ((capacityIncrement > 0) ? capacityIncrement : oldCapacity);
		if (newCapacity - minCapacity < 0)
			newCapacity = minCapacity;
		if (newCapacity - MAX_ARRAY_SIZE > 0)
			newCapacity = hugeCapacity(minCapacity);
		elementData = Arrays.copyOf(elementData, newCapacity);
	}

	private static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
	}
}
