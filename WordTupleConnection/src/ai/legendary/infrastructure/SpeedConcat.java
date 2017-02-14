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
	public SpeedConcat () {
	}
	public void append(final Object o){
		final String add = o.toString();
		if (head==null) {
			head = new Node(add);
			tail = head;
			return;
		}
		if (add.equals("")) {
			return;
		}
		tail.next = new Node(add);
		tail = tail.next;
		return;
	}
	public String concat() {
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
