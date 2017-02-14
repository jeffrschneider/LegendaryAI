package ai.legendary.infrastructure;

public class SpeedConcat {
	private class Node {
		public String val = "";
		public Node next = null;
		public Node(final String in) {
			val = in;
		}
	}
	Node head = null;
	Node tail = null;
	/**
	 * Makes a new SpeedConcat object. This is a poorly-written reimplementation of the Javascript version of Speedconcat: https://www.npmjs.com/package/speedconcat
	 */
	public SpeedConcat () {}
	/**
	 * Takes an object which implements toString, appends it to the list of things to be concatenated, and returns this.
	 * @param Object o The thing being added as a String.
	 * @return SpeedConcat this.
	 */
	public final SpeedConcat append(final Object o){
		final String add = o.toString();
		if (head==null) {
			head = new Node(add);
			tail = head;
			return this;
		}
		if (add.equals("")) {
			return this;
		}
		tail.next = new Node(add);
		tail = tail.next;
		return this;
	}
	/**
	 * Generates the resulting String in an A log2(B) banner.
	 * @return String result the resulting String.
	 */
	public final String concat() {
		while (head.next!=null) {
			Node ptr = head;
			while (ptr != null) {
				if (ptr.next!=null) {
					ptr.val = ptr.val + ptr.next.val;
					ptr.next = ptr.next.next;
				}
				ptr = ptr.next;
			}
		}
		return head.val;
	}
}
