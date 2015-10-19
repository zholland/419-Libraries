package core.components;

/**
 * Simple Pair class
 * 
 * @author Michael Nowicki
 *
 * @param <L> - Left object type
 * @param <R> - Right object type
 */
public class Pair<L, R> {
	
	/**
	 * The left element
	 */
	private L left;
	/**
	 * The right element
	 */
	private R right;

	public Pair(L l, R r) {
		this.left = l;
		this.right = r;
	}
	
	public L getLeft() { return this.left; }

	public R getRight() { return this.right; }

}
