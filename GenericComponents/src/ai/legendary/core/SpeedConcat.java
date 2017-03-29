package ai.legendary.core;

import java.io.Writer;

public class SpeedConcat {
	private final class Node {
		public String val = "";
		public Node next = null;
		public Node(final String in) {
			val = in;
		}
	}
	private Node head = null;
	private Node tail = null;
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
		if (o==null) {
			return this;
		}
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
	 * Takes an array of objects, iterates through them, appends them individually, and returns this.
	 * @param Object[] o The objects being appended.
	 * @return SpeedConcat this.
	 */
	public final SpeedConcat append(final Object[] o) {
		for (int index = 0; index < o.length; index++) {
			append(o[index]);
		}
		return this;
	}
	/**
	 * Takes an array of objects and a join statement, iterates through them, appends them individually, and returns this. Kind of like stringUtils.join, but appending to this.
	 * @param Object[] o The objects being appended.
	 * @param Object o the join statement. If null, it's treated as an empty String.
	 * @return SpeedConcat this.
	 */
	public final SpeedConcat joinAppend(final Object[] os, final Object o) {
		if (o==null) {
			return joinAppend(os, "");
		}
		if (os.length > 0) {
			append(os[0]);
		}
		for (int index = 1; index < os.length; index++) {
			append(o);
			append(os[index]);
		}
		return this;
	}
	/**
	 * Takes an int, makes a string from that int, appends the resulting string to the list of things to be concatenated, and returns this.
	 * @param int i The int being made into a string and concatenated.
	 * @return SpeedConcat this
	 */
	public final SpeedConcat append(final int i) {
		return append(Integer.toString(i));
	}
	/**
	 * Takes an short, makes a string from that short, appends the resulting string to the list of things to be concatenated, and returns this.
	 * @param short s The short being made into a string and concatenated.
	 * @return SpeedConcat this
	 */
	public final SpeedConcat append(final short s) {
		return append(Short.toString(s));
	}
	/**
	 * Takes an long, makes a string from that long, appends the resulting string to the list of things to be concatenated, and returns this.
	 * @param long l The long being made into a string and concatenated.
	 * @return SpeedConcat this
	 */
	public final SpeedConcat append(final long l) {
		return append(Long.toString(l));
	}
	/**
	 * Takes an char, makes a string from that char, appends the resulting string to the list of things to be concatenated, and returns this.
	 * @param char c The char being made into a string and concatenated.
	 * @return SpeedConcat this
	 */
	public final SpeedConcat append(final char c) {
		return append(Character.toString(c));
	}
	/**
	 * Takes an double, makes a string from that double, appends the resulting string to the list of things to be concatenated, and returns this.
	 * @param double d The double being made into a string and concatenated.
	 * @return SpeedConcat this
	 */
	public final SpeedConcat append(final double d) {
		return append(Double.toString(d));
	}
	/**
	 * Takes an float, makes a string from that float, appends the resulting string to the list of things to be concatenated, and returns this.
	 * @param float f The float being made into a string and concatenated.
	 * @return SpeedConcat this
	 */
	public final SpeedConcat append(final float f) {
		return append(Float.toString(f));
	}
	/**
	 * Takes an byte, makes a string from that byte, appends the resulting string to the list of things to be concatenated, and returns this.
	 * @param byte b The byte being made into a string and concatenated.
	 * @return SpeedConcat this
	 */
	public final SpeedConcat append(final byte b) {
		return append(Byte.toString(b));
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
	/**
	 * Directly prints a SpeedConcat object to a writer, without any concatenation. When pipelining is involved, this is A LOT faster.
	 * @param SpeedConcat sc The SpeedConcat object.
	 * @param Writer w The writer being written to.
	 * @exception Exception e An exception that may be thrown due to writer errors, SpeedConcat errors, null pointer errors, and machine errors.
	 */
	public static final void print(final SpeedConcat sc, final Writer w) throws Exception {
		if (sc==null) {
			throw new Exception();
		}
		if (w==null) {
			throw new Exception();
		}
		Node node = sc.head;
		if (node==null) {
			return;
		}
		while (node != null) {
			w.write(node.val);
			node = node.next;
		}
		return;
	}
}
