public class RandomAccessSet<E> implements java.io.Serializable,
		java.util.RandomAccess {

	private final java.util.Comparator<? super E> comparator;

	public RandomAccessSet() {
		comparator = null;
	}

	public RandomAccessSet(java.util.Comparator<? super E> _comp) {
		comparator = _comp;
	}

	static final class Node<E> {
		public static final boolean RED = false;
		public static final boolean BLACK = true;
		int size = 1;
		public E value;
		Node<E> left = null;
		Node<E> right = null;
		Node<E> parent;
		boolean color = BLACK;

		Node(E value, Node<E> parent) {
			this.value = value;
			this.parent = parent;
		}
	}

	public int size() {
		return subTreeSize(root);
	}

	private transient Node<E> root = null;

	static <E> int subTreeSize(Node<E> t) {
		if (t != null) {
			return t.size;
		}
		return 0;
	}

	static <E> boolean checkTreeSize(Node<E> root) {
		if (root == null) {
			return true;
		} else {
			if (checkTreeSize(root.left) && checkTreeSize(root.right)) {
				if ((subTreeSize(root.left) + subTreeSize(root.right) + 1) == root.size) {
					return true;
				}
			}
			return false;
		}
	}
	

    final Node<E> getNode(E value) {
        // Offload comparator-based version for sake of performance
        if (value == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
		Comparable<? super E> k = (Comparable<? super E>) value;
        Node<E> p = root;
        while (p != null) {
            int cmp = k.compareTo(p.value);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }
        return null;
    }

	public boolean put(E value) {
		Node<E> t = root;
		if (t == null) {
			root = new Node<>(value, null);
			assert (checkTreeSize(root));
			return false;
		}
		int cmp;
		Node<E> parent;
		// split comparator and comparable paths
		java.util.Comparator<? super E> cpr = comparator;
		if (cpr != null) {
			do {
				parent = t;
				cmp = cpr.compare(value, t.value);
				if (cmp < 0)
					t = t.left;
				else if (cmp > 0)
					t = t.right;
				else {
					t.value = value;
					assert (checkTreeSize(root));
					return true;
				}
			} while (t != null);
		} else {
			if (value == null)
				throw new NullPointerException();
			@SuppressWarnings("unchecked")
			Comparable<? super E> k = (Comparable<? super E>) value;
			do {
				parent = t;
				cmp = k.compareTo(t.value);
				if (cmp < 0)
					t = t.left;
				else if (cmp > 0)
					t = t.right;
				else {
					t.value = value;
					assert (checkTreeSize(root));
					return true;
				}
			} while (t != null);
		}
		Node<E> e = new Node<>(value, parent);
		if (cmp < 0)
			parent.left = e;
		else
			parent.right = e;
		fixAfterInsertion(e);
		assert (checkTreeSize(root));
		return false;
	}

	private static final long serialVersionUID = -7118906540937077716L;

	static <E> Node<E> successor(Node<E> t) {
		if (t == null)
			return null;
		else if (t.right != null) {
			Node<E> p = t.right;
			while (p.left != null)
				p = p.left;
			return p;
		} else {
			Node<E> p = t.parent;
			Node<E> ch = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	/**
	 * Returns the predecessor of the specified Entry, or null if no such.
	 */
	static <E> Node<E> predecessor(Node<E> t) {
		if (t == null)
			return null;
		else if (t.left != null) {
			Node<E> p = t.left;
			while (p.right != null)
				p = p.right;
			return p;
		} else {
			Node<E> p = t.parent;
			Node<E> ch = t;
			while (p != null && ch == p.left) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	/**
	 * Balancing operations.
	 * 
	 * Implementations of rebalancings during insertion and deletion are
	 * slightly different than the CLR version. Rather than using dummy
	 * nilnodes, we use a set of accessors that deal properly with null. They
	 * are used to avoid messiness surrounding nullness checks in the main
	 * algorithms.
	 */

	private static <E> boolean colorOf(Node<E> p) {
		return (p == null ? Node.BLACK : p.color);
	}

	private static <E> Node<E> parentOf(Node<E> p) {
		return (p == null ? null : p.parent);
	}

	private static <E> void setColor(Node<E> p, boolean c) {
		if (p != null)
			p.color = c;
	}

	private static <E> Node<E> leftOf(Node<E> p) {
		return (p == null) ? null : p.left;
	}

	private static <E> Node<E> rightOf(Node<E> p) {
		return (p == null) ? null : p.right;
	}

	/** From CLR */
	private void rotateLeft(Node<E> p) {
		if (p != null) {
			Node<E> r = p.right;
			p.size -= r.size;
			r.size += p.size;
			p.right = r.left;
			if (r.left != null) {
				p.size += r.left.size;
				r.left.parent = p;
			}
			r.parent = p.parent;
			if (p.parent == null)
				root = r;
			else if (p.parent.left == p)
				p.parent.left = r;
			else
				p.parent.right = r;
			r.left = p;
			p.parent = r;
		}
	}

	/** From CLR */
	private void rotateRight(Node<E> p) {
		if (p != null) {
			Node<E> l = p.left;
			p.size -= l.size;
			l.size += p.size;
			p.left = l.right;
			if (l.right != null) {
				p.size += l.right.size;
				l.right.parent = p;
			}
			l.parent = p.parent;
			if (p.parent == null)
				root = l;
			else if (p.parent.right == p)
				p.parent.right = l;
			else
				p.parent.left = l;
			l.right = p;
			p.parent = l;
		}
	}

	/** From CLR */
	private void fixAfterInsertion(Node<E> x) {

		// fix tree size:
		{
			Node<E> tmp = x;
			while (tmp.parent != null) {
				tmp.parent.size++;
				tmp = tmp.parent;
			}
		}
		x.color = Node.RED;

		while (x != null && x != root && x.parent.color == Node.RED) {
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				Node<E> y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == Node.RED) {
					setColor(parentOf(x), Node.BLACK);
					setColor(y, Node.BLACK);
					setColor(parentOf(parentOf(x)), Node.RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), Node.BLACK);
					setColor(parentOf(parentOf(x)), Node.RED);
					rotateRight(parentOf(parentOf(x)));
				}
			} else {
				Node<E> y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == Node.RED) {
					setColor(parentOf(x), Node.BLACK);
					setColor(y, Node.BLACK);
					setColor(parentOf(parentOf(x)), Node.RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x), Node.BLACK);
					setColor(parentOf(parentOf(x)), Node.RED);
					rotateLeft(parentOf(parentOf(x)));
				}
			}
		}
		root.color = Node.BLACK;
	}
	
    public boolean remove(E value) {
        Node<E> p = getNode(value);
        if (p == null)
            return false;
        deleteNode(p);
        return true;
    }


	/**
	 * Delete node p, and then rebalance the tree.
	 */
	private void deleteNode(Node<E> p) {

		// If strictly internal, copy successor's element to p and then make p
		// point to successor.
		if (p.left != null && p.right != null) {
			Node<E> s = successor(p);
			p.value = s.value;
			p = s;
		} // p has 2 children

		// Start fixup at replacement node, if it exists.
		Node<E> replacement = (p.left != null ? p.left : p.right);

		if (replacement != null) {
			// Link replacement to parent
			replacement.parent = p.parent;
			if (p.parent == null)
				root = replacement;
			else if (p == p.parent.left)
				p.parent.left = replacement;
			else
				p.parent.right = replacement;

			// Null out links so they are OK to use by fixAfterDeletion.
			p.left = p.right = p.parent = null;

			// Fix replacement
			if (p.color == Node.BLACK)
				fixAfterDeletion(replacement);
		} else if (p.parent == null) { // return if we are the only node.
			root = null;
		} else { // No children. Use self as phantom replacement and unlink.
			if (p.color == Node.BLACK)
				fixAfterDeletion(p);

			if (p.parent != null) {
				if (p == p.parent.left)
					p.parent.left = null;
				else if (p == p.parent.right)
					p.parent.right = null;
				p.parent = null;
			}
		}
	}

	/** From CLR */
	private void fixAfterDeletion(Node<E> x) {

		// fix tree size:
		{
			Node<E> tmp = x;
			while (tmp.parent != null) {
				tmp.parent.size--;
				tmp = tmp.parent;
			}
		}

		while (x != root && colorOf(x) == Node.BLACK) {
			if (x == leftOf(parentOf(x))) {
				Node<E> sib = rightOf(parentOf(x));

				if (colorOf(sib) == Node.RED) {
					setColor(sib, Node.BLACK);
					setColor(parentOf(x), Node.RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == Node.BLACK
						&& colorOf(rightOf(sib)) == Node.BLACK) {
					setColor(sib, Node.RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == Node.BLACK) {
						setColor(leftOf(sib), Node.BLACK);
						setColor(sib, Node.RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), Node.BLACK);
					setColor(rightOf(sib), Node.BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else { // symmetric
				Node<E> sib = leftOf(parentOf(x));

				if (colorOf(sib) == Node.RED) {
					setColor(sib, Node.BLACK);
					setColor(parentOf(x), Node.RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == Node.BLACK
						&& colorOf(leftOf(sib)) == Node.BLACK) {
					setColor(sib, Node.RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == Node.BLACK) {
						setColor(rightOf(sib), Node.BLACK);
						setColor(sib, Node.RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), Node.BLACK);
					setColor(leftOf(sib), Node.BLACK);
					rotateRight(parentOf(x));
					x = root;
				}
			}
		}

		setColor(x, Node.BLACK);
	}

	final Node<E> getFirst() {
		Node<E> p = root;
		if (p != null)
			while (p.left != null)
				p = p.left;
		return p;
	}

	final Node<E> getLast() {
		Node<E> p = root;
		if (p != null)
			while (p.right != null)
				p = p.right;
		return p;
	}

	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out the Comparator and any hidden stuff
		s.defaultWriteObject();

		// Write out size (number of Mappings)
		s.writeInt(subTreeSize(root));

		// Write out keys and values (alternating)
		for (Node<E> i = getFirst(); i != null; i = successor(i)) {
			s.writeObject(i.value);
		}
	}

	private void readObject(final java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in the Comparator and any hidden stuff
		s.defaultReadObject();

		// Read in size
		int size = s.readInt();

		root = buildFromSorted(0, 0, size - 1, computeRedLevel(size), s);
	}

	private final Node<E> buildFromSorted(int level, int lo, int hi,
			int redLevel, java.io.ObjectInputStream str)
			throws java.io.IOException, ClassNotFoundException {

		if (hi < lo)
			return null;

		int mid = (lo + hi) >>> 1;

		Node<E> left = null;
		if (lo < mid)
			left = buildFromSorted(level + 1, lo, mid - 1, redLevel, str);

		// extract key and/or value from iterator or stream

		@SuppressWarnings("unchecked")
		E value = (E) str.readObject();

		Node<E> middle = new Node<>(value, null);

		// color nodes in non-full bottommost level red
		if (level == redLevel)
			middle.color = Node.RED;

		if (left != null) {
			middle.left = left;
			left.parent = middle;
		}

		if (mid < hi) {
			Node<E> right = buildFromSorted(level + 1, mid + 1, hi, redLevel,
					str);
			middle.right = right;
			right.parent = middle;
		}

		middle.size = subTreeSize(middle.left) + subTreeSize(middle.right) + 1;

		return middle;
	}

	private static int computeRedLevel(int sz) {
		int level = 0;
		for (int m = sz - 1; m >= 0; m = m / 2 - 1)
			level++;
		return level;
	}

}
