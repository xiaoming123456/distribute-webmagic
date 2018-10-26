package us.codecraft.webmagic.selenium.utils;

public class ScaleLinkedList<E> {

	private int size = 0;

	private Node<E> first;

	private Node<E> last;

	private Execute<E> execute;

	public Position position = Position.head;

	public ScaleLinkedList(Execute<E> execute) {
		this.execute = execute;
	}

	public void add(E e) {
		final Node<E> l = last;
		final Node<E> newNode = new Node<>(l, e, null);
		last = newNode;
		if (l == null)
			first = newNode;
		else
			l.next = newNode;
		size++;
	}

	public void iterate(int n) {
		if (position == Position.head) {
			asc(n);
		} else if (position == Position.last) {
			desc(n);
		}
	}

	private void asc(int n) {
		Node<E> x = first;
		for (int i = 0; i < size; i++) {

			execute.exe(x, n, i);
			x = x.next;
		}
		position = Position.last;
	}

	private void desc(int n) {
		Node<E> x = last;
		for (int i = 0; i < size; i++) {

			execute.exe(x, n, i);
			x = x.prev;
		}
		position = Position.head;
	}

	public Node<E> getFirst() {
		return first;
	}

	public Node<E> getLast() {
		return last;
	}

	public interface Execute<E> {
		void exe(Node<E> node, int n, int index);
	}

	public int getSize() {
		return size;
	}

	public static class Node<T> {
		public T item;
		public Node<T> next;
		public Node<T> prev;

		Node(Node<T> prev, T element, Node<T> next) {
			this.item = element;
			this.next = next;
			this.prev = prev;
		}
	}

	public enum Position {
		head, last
	}

}
