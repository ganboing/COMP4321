public final class KeyWordDescriptor implements Comparable<KeyWordDescriptor>,
		java.util.Comparator<KeyWordDescriptor> {
	long pageId;
	int[] pos = null;

	public int compare(KeyWordDescriptor o1, KeyWordDescriptor o2) {
		Long.compare(o1.pageId, o2.pageId);
		return 0;
	}

	public int compareTo(KeyWordDescriptor o) {
		Long.compare(this.pageId, o.pageId);
		return 0;
	}
}
