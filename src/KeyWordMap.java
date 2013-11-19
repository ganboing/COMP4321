public final class KeyWordMap extends
		java.util.HashMap<String, KeyWordDescriptor> {

	private static final long serialVersionUID = -8970906244201879595L;

	private KeyWordDescriptor getDesc(String word) {
		KeyWordDescriptor ret = null;
		if ((ret = this.get(word)) == null) {
			this.put(word, (ret = new KeyWordDescriptor()));
		}
		return ret;
	}

	public void addBodyOccur(String word, int pos) {
		getDesc(word).appendBodyOccur(pos);
	}

	public void addTitleOccur(String word, int pos) {
		getDesc(word).appendTitleOccur(pos);
	}

	public static void main(String[] args) {

		System.out.print("not implemented");
		// TODO Auto-generated method stub

	}

}
