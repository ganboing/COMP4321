public final class KeyWordDescriptor implements java.io.Serializable {

	static final long serialVersionUID = 7787801390683099069L;
	// long pageId;

	public java.util.List<Integer> body_occur = null;
	public java.util.List<Integer> title_occur = null;

	public void appendBodyOccur(int pos) {
		if (body_occur == null) {
			body_occur = new java.util.LinkedList<Integer>();
		}
		body_occur.add(pos);
	}

	public void appendTitleOccur(int pos) {
		if (title_occur == null) {
			title_occur = new java.util.LinkedList<Integer>();
		}
		title_occur.add(pos);
	}

	public int Cnt() {
		int ret = 0;
		if (body_occur != null) {
			ret += body_occur.size();
		}
		if (title_occur != null) {
			ret += title_occur.size();
		}
		return ret;
	}
}
