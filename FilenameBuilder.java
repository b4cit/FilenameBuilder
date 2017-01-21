
/*
 * FilenameBuilder - A small Java utility for regenerating new filename from old one.
 * Created by b4cit @ Jan 22, 2017
 * */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * An utility to split/tokenize a string with delimiter, a few operation such as
 * append/prepend/replace/remove is also available. Negative index is supported
 * to locate the last x element.
 * 
 * @see FilenameBuilder#make(String)
 * @see FilenameBuilder#make(String, String)
 * @see FilenameBuilder#get(int)
 */
public class FilenameBuilder {

	public static final String AND = "&";
	public static final String BACKSLASH = "\\";
	public static final String COMMA = ",";
	public static final String DOLLAR = "$";
	public static final String HASH = "#";
	public static final String HYPHEN = "-";
	public static final String OR = "|";
	public static final String PERIOD = ".";
	public static final String PLUS = "+";
	public static final String SEMICOLON = ";";
	public static final String SLASH = "/";
	public static final String SPACE = " ";
	public static final String UNDERSCORE = "_";

	/**
	 * Create a FilenameBuilder with a filename and the default delimiter ".".
	 * Given <code>String</code> will be parsed into nodes using this delimiter
	 * , these nodes will be stored into an internal LinkedList.
	 *
	 * <p>
	 * Invocation example 1: <blockquote>
	 *
	 * <pre>
	 * FilenameBuilder fb = new FilenameBuilder("org.myname.project.no1180.zip");
	 * String out = fb.replaceFirst("com").append("_new", -2).changeExtension("tar.gz").toString();
	 * System.out.println(out);
	 * // Such code block will print "com.myname.project.no1180_new.tar.gz".
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * <p>
	 * Invocation example 2: <blockquote>
	 *
	 * <pre>
	 * FilenameBuilder fb = new FilenameBuilder("192.168.1.3.log");
	 * String out = fb.replace("220", 1).prepend("5", 3).addLast("encrypted").toString();
	 * System.out.println(out);
	 * // Such code block will print "192.220.1.53.log.encrypted".
	 * </pre>
	 *
	 * </blockquote>
	 * <p>
	 *
	 * @param str
	 *            The <code>String</code> to be parsed. Please note the given
	 *            String will trim any "<b>.</b>" and will not be parsed into
	 *            nodes.
	 * @see String#split(String regex)
	 * @see LinkedList
	 */
	public FilenameBuilder(String filename) throws IllegalArgumentException {
		this(filename, PERIOD);
	}

	/**
	 * Create a FilenameBuilder with a filename and a custom delimiter. Given
	 * <code>String</code> will be parsed into nodes using this delimiter ,
	 * these nodes will be stored into an internal LinkedList.
	 *
	 * <p>
	 * Invocation example 1: <blockquote>
	 *
	 * <pre>
	 * FilenameBuilder fb = new FilenameBuilder("3,green,apple,and,Jenny", FilenameBuilder.COMMA);
	 * String out = fb.replace("grape", 2).remove(3).appendLast(" Lin").toString();
	 * System.out.println(out);
	 * // Such code block will print "3,green,grape,Jenny Lin".
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * <p>
	 * Invocation example 2: <blockquote>
	 *
	 * <pre>
	 * FilenameBuilder fb = new FilenameBuilder("Another way to rebuild the filename!", FilenameBuilder.SPACE);
	 * String out = fb.replaceFirst("A new").remove(5).appendLast("!").toString();
	 * System.out.println(out);
	 * // Such code block will print "A new way to rebuild filename!!".
	 * </pre>
	 *
	 * </blockquote>
	 * <p>
	 *
	 * @param str
	 *            The <code>String</code> to be parsed. Please note the given
	 *            String will trim any "<b>.</b>" and will not be parsed into
	 *            nodes.
	 * @see String#split(String regex)
	 * @see LinkedList
	 */
	public FilenameBuilder(String filename, String delimiter) throws IllegalArgumentException {
		this.nodes = parse(filename, delimiter);
		this.delimiter = delimiter;
	}

	/**
	 * Parse string into nodes
	 */
	private static final LinkedList<String> parse(String filename, String delimiter) throws IllegalArgumentException {
		if (filename == null || delimiter == null) {
			throw new IllegalArgumentException("input == null");
		}
		LinkedList<String> nodes = new LinkedList<String>();
		StringTokenizer tokens = new StringTokenizer(filename, delimiter);
		while (tokens.hasMoreTokens()) {
			nodes.add(tokens.nextToken());
		}
		return nodes;
	}

	private String delimiter;

	private LinkedList<String> nodes;

	/**
	 * Parse the given <code>String</code> into nodes, then put these into the
	 * backed <code>LinkedList</code> at given index.<br>
	 * Chain invocation is supported.
	 * 
	 * <p>
	 * For example: <blockquote>
	 * 
	 * <pre>
	 * a.b.txt
	 * add("new", 0) will become new.a.b.txt
	 * add("tmp", -1) will become a.b.tmp.txt
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param str
	 *            The <code>String</code> to be parsed and added. Please note
	 *            the given String will trim any "<b>.</b>" and will not be
	 *            parsed into nodes.
	 * @param intoIndex
	 *            The index of backed array, parsed nodes will be inserted
	 *            starting from here. Please note this value could be negative
	 *            to represent the last x node.
	 * @return itself (for chain invocation)
	 * @see {@link LinkedList#addAll(int, java.util.Collection)}
	 * @throws IndexOutOfBoundsException
	 *             If the given index is invalid for the backed array.
	 */
	public FilenameBuilder add(String str, int atIndex) throws IndexOutOfBoundsException {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			int realIndex = fixIndex(atIndex);
			nodes.addAll(realIndex, nodesNew);
			return this;
		}
	}

	/**
	 * Parse the given <code>String</code> into nodes, then put these into the
	 * backed <code>LinkedList</code> starting from beginning.<br>
	 * Chain invocation is supported.
	 * 
	 * <p>
	 * For example: <blockquote>
	 * 
	 * <pre>
	 * a.b.txt
	 * addFirst("new") will become new.a.b.txt
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param str
	 *            The <code>String</code> to be parsed and added. Please note
	 *            the given String will trim any "<b>.</b>" and will not be
	 *            parsed into nodes.
	 * @return itself (for chain invocation)
	 * @see {@link LinkedList#addFirst(Object)}
	 */
	public FilenameBuilder addFirst(String str) {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			nodes.addAll(0, nodesNew);
			return this;
		}
	}

	/**
	 * Parse the given <code>String</code> into nodes, then put these into the
	 * backed <code>LinkedList</code> starting from ending.<br>
	 * Chain invocation is supported.
	 * 
	 * <p>
	 * For example: <blockquote>
	 * 
	 * <pre>
	 * a.b.txt
	 * addLast("new") will become a.b.txt.new
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param str
	 *            The <code>String</code> to be parsed and added. Please note
	 *            the given String will trim any "<b>.</b>" and will not be
	 *            parsed into nodes.
	 * @return itself (for chain invocation)
	 * @see {@link LinkedList#addLast(Object)}
	 */
	public FilenameBuilder addLast(String str) {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			nodes.addAll(nodesNew);
			return this;
		}
	}

	/**
	 * Parse the given <code>String</code> into nodes, append to the given index
	 * of the backed <code>LinkedList</code>.<br>
	 * Chain invocation is supported.
	 * 
	 * <p>
	 * For example: <blockquote>
	 * 
	 * <pre>
	 * a.b.txt
	 * append("_new",0) will become a_new.b.txt
	 * append("_new",-1) will become a.b.txt_new
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param str
	 *            The <code>String</code> to be parsed then appended or added.
	 *            Please note the given String will trim any "<b>.</b>" and will
	 *            not be parsed into nodes.
	 * @param atIndex
	 *            The index of backed array, parsed nodes will be inserted
	 *            starting from here. Please note this value could be negative
	 *            to represent the last x node.
	 * @return itself (for chain invocation)
	 * @see {@link LinkedList#pollFirst()}
	 * @see {@link LinkedList#remove(int)}
	 * @see {@link LinkedList#addFirst(Object)}
	 * @see {@link LinkedList#addAll(int, java.util.Collection)}
	 * @throws IndexOutOfBoundsException
	 *             If the given index is invalid for the backed array.
	 */
	public FilenameBuilder append(String str, int atIndex) throws IndexOutOfBoundsException {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			int realIndex = fixIndex(atIndex);
			String append = nodesNew.pollFirst();
			String original = nodes.get(realIndex);
			nodes.remove(realIndex);
			nodesNew.addFirst(original + append);
			nodes.addAll(realIndex, nodesNew);
			return this;
		}
	}

	/**
	 * Parse the given <code>String</code> into nodes, append to the first node
	 * of the backed <code>LinkedList</code>.<br>
	 * Chain invocation is supported.
	 * 
	 * <p>
	 * For example: <blockquote>
	 * 
	 * <pre>
	 * a.b.txt
	 * appendFirst("_new") will become a_new.b.txt
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param str
	 *            The <code>String</code> to be parsed and added. Please note
	 *            the given String will trim any "<b>.</b>" and will not be
	 *            parsed into nodes.
	 * @return itself (for chain invocation)
	 * @see {@link LinkedList#pollFirst()}
	 * @see {@link LinkedList#addFirst(Object)}
	 * @see {@link LinkedList#addAll(int, java.util.Collection)}
	 */
	public FilenameBuilder appendFirst(String str) {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			String append = nodesNew.pollFirst();
			String original = nodes.pollFirst();
			nodesNew.addFirst(original + append);
			nodes.addAll(0, nodesNew);
			return this;
		}
	}

	/**
	 * Parse the given <code>String</code> into nodes, append to the last node
	 * of the backed <code>LinkedList</code>.<br>
	 * Chain invocation is supported.
	 * 
	 * <p>
	 * For example: <blockquote>
	 * 
	 * <pre>
	 * a.b.txt
	 * appendLast("_new") will become a.b.txt_new
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param str
	 *            The <code>String</code> to be parsed and added. Please note
	 *            the given String will trim any "<b>.</b>" and will not be
	 *            parsed into nodes.
	 * @return itself (for chain invocation)
	 * @see {@link LinkedList#pollFirst()}
	 * @see {@link LinkedList#pollLast()}
	 * @see {@link LinkedList#addFirst(Object)}
	 * @see {@link LinkedList#addAll(java.util.Collection)}
	 */
	public FilenameBuilder appendLast(String str) {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			String append = nodesNew.pollFirst();
			String original = nodes.pollLast();
			nodesNew.addFirst(original + append);
			nodes.addAll(nodesNew);
			return this;
		}
	}

	/**
	 * Parse the given <code>String</code> into nodes, then replace the last
	 * node of the backing <code>LinkedList</code> with it.<br>
	 * This operation is has the same effect of
	 * {@link FilenameBuilder#replaceLast(String)}<br>
	 * Chain invocation is supported.
	 * 
	 * <p>
	 * For example: <blockquote>
	 * 
	 * <pre>
	 * a.b.txt
	 * changeExtension(".rar") will become a.b.rar
	 * changeExtension("zip") will become a.b.zip
	 * changeExtension(".tar.gz") will become a.b.tar.gz
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param newExtension
	 *            The <code>String</code> to be parsed and added. Please note
	 *            the given String will trim any "<b>.</b>" and will not be
	 *            parsed into nodes.
	 * @return itself (for chain invocation)
	 * @see {@link FilenameBuilder#replaceLast(String)}
	 */
	public FilenameBuilder changeExtension(String newExtension) {
		return replaceLast(newExtension);
	}

	/**
	 * Fix the index if the input is negative, based on the size of the backing
	 * <code>LinkedList</code>. Otherwise return original value.
	 * 
	 * <p>
	 * For example: <blockquote>
	 * 
	 * <pre>
	 * Backing <code>LinkedList</code> has a size of 5 ( Max index = 4 )
	 * fixIndex(-1) will return 4
	 * fixIndex(-2) will return 3
	 * fixIndex(0) will return 0
	 * fixIndex(1) will return 1
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param index
	 *            The index to get fixed.
	 * @return the new fixed index, based on the size of the backing
	 *         <code>LinkedList</code>.
	 * @see {@link LinkedList#size()}
	 */
	private int fixIndex(int index) throws IndexOutOfBoundsException {
		synchronized (nodes) {
			int size = nodes.size();
			if (size <= 0) {
				throw new IndexOutOfBoundsException("size = 0, use addFirst(str) instead");
			}
			int lPos = -size;
			int hPos = size - 1;

			if (index < lPos || index > hPos) {
				String errorMessage = String.format("Element at %s does not exist, index range: %s <= x <= %s", index,
						lPos, hPos);
				throw new IndexOutOfBoundsException(errorMessage);
			}
			return (index >= 0) ? index : size + index;
		}
	}

	/**
	 * Get the element at the specified index. Negative index is supported to
	 * locate the last x element.
	 * 
	 * <p>
	 * Invocation example: <blockquote>
	 *
	 * <pre>
	 * FilenameBuilder fb = new FilenameBuilder("192.168.1.3.log");
	 * System.out.println(fb.get(-1));
	 * // Such code block will print "log".
	 * </pre>
	 *
	 * </blockquote>
	 * <p>
	 * 
	 * @param atIndex
	 *            The element at this index to be queried.
	 * @return the <code>String<code> type element
	 */
	public String get(int atIndex) throws IndexOutOfBoundsException {
		synchronized (nodes) {
			int realIndex = fixIndex(atIndex);
			return nodes.get(realIndex);
		}
	}

	public String getFirst() {
		synchronized (nodes) {
			return nodes.getFirst();
		}
	}

	public String getLast() {
		synchronized (nodes) {
			return nodes.getLast();
		}
	}

	/**
	 * Returns the delimiter used to parse the string.
	 * 
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * Returns the last element of the string. Usually this is for the "."
	 * delimiter.
	 * 
	 * @return the last element of the string
	 */
	public String getExtension() {
		synchronized (nodes) {
			return nodes.getLast();
		}
	}

	/**
	 * Returns the iterator of the backing <code>LinkedList</code>, for manually
	 * traversal/remove the underlying elements.
	 * 
	 * @return the iterator of the backing <code>LinkedList</code>
	 */
	public Iterator<String> iterator() {
		return nodes.iterator();
	}

	public FilenameBuilder prepend(String str, int atIndex) throws IndexOutOfBoundsException {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			int realIndex = fixIndex(atIndex);
			String prepend = nodesNew.pollLast();
			String original = nodes.get(realIndex);
			nodes.remove(realIndex);
			nodesNew.addLast(prepend + original);
			nodes.addAll(realIndex, nodesNew);
			return this;
		}
	}

	public FilenameBuilder prependFirst(String str) {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			String prepend = nodesNew.pollLast();
			String original = nodes.pollFirst();
			nodesNew.addLast(prepend + original);
			nodes.addAll(0, nodesNew);
			return this;
		}
	}

	public FilenameBuilder prependLast(String str) {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			String prepend = nodesNew.pollLast();
			String original = nodes.pollLast();
			nodesNew.addLast(prepend + original);
			nodes.addAll(nodesNew);
			return this;
		}
	}

	public FilenameBuilder remove(int atIndex) throws IndexOutOfBoundsException {
		synchronized (nodes) {
			int realIndex = fixIndex(atIndex);
			nodes.remove(realIndex);
			return this;
		}
	}

	public FilenameBuilder removeBatch(int fromIndexInc, int toIndexInc)
			throws IllegalArgumentException, IndexOutOfBoundsException {
		synchronized (nodes) {
			fromIndexInc = fixIndex(fromIndexInc);
			toIndexInc = fixIndex(toIndexInc);
			if (fromIndexInc > toIndexInc) {
				throw new IllegalArgumentException(String.format("Start(%s) > End(%s)", fromIndexInc, toIndexInc));
			}
			for (int i = fromIndexInc; i <= toIndexInc; i++) {
				nodes.remove(fromIndexInc);
			}
			return this;
		}
	}

	public FilenameBuilder removeFirst() {
		synchronized (nodes) {
			nodes.removeFirst();
			return this;
		}
	}

	public FilenameBuilder removeLast() {
		synchronized (nodes) {
			nodes.removeLast();
			return this;
		}
	}

	public FilenameBuilder replace(String str, int atIndex) throws IndexOutOfBoundsException {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			int realIndex = fixIndex(atIndex);
			nodes.remove(realIndex);
			nodes.addAll(realIndex, nodesNew);
			return this;
		}
	}

	public FilenameBuilder replaceFirst(String str) {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			nodes.removeFirst();
			nodes.addAll(0, nodesNew);
			return this;
		}
	}

	public FilenameBuilder replaceLast(String str) {
		synchronized (nodes) {
			LinkedList<String> nodesNew = parse(str, delimiter);
			nodes.removeLast();
			nodes.addAll(nodesNew);
			return this;
		}
	}

	public FilenameBuilder cleanEmptyNodes() {
		synchronized (nodes) {
			Iterator<String> it = nodes.iterator();
			while (it.hasNext()) {
				String tmp = it.next();
				if (tmp == null || tmp.equals("")) {
					it.remove();
				}
			}
			return this;
		}
	}

	public int size() {
		return nodes.size();
	}

	/**
	 * Return the result of {@link LinkedList#toString()} method of the backing
	 * <code>LinkedList</code>.
	 * 
	 * @return the direct <code>String</code> representation based on the
	 *         backing <code>LinkedList</code>
	 * @see {@link LinkedList#toString()}
	 */
	public String toNodesString() {
		synchronized (nodes) {
			return nodes.toString();
		}
	}

	/**
	 * Construct the backing <code>LinkedList</code> into the final result
	 * <code>String</code>.
	 * 
	 * @return a new <code>String</code> that is composed from the backing
	 *         <code>LinkedList</code>
	 * @see {@link StringBuffer}
	 * @see {@link LinkedList#size()}
	 * @see {@link LinkedList#iterator()}
	 */
	public String toString() {
		synchronized (nodes) {
			StringBuffer sb = new StringBuffer(nodes.size() * 3);
			Iterator<String> it = nodes.iterator();
			while (it.hasNext()) {
				sb.append(it.next());
				if (it.hasNext()) {
					sb.append(delimiter);
				}
			}
			return sb.toString();
		}
	}

}
