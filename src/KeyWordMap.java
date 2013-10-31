public class KeyWordMap extends java.util.HashMap<String, KeyWordDescriptor> {

	/**
	 * 
	 */
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

	/*
	 * public void print() { System.out.print("{ "); for
	 * (java.util.Map.Entry<String, Integer> i : this.entrySet()) {
	 * System.out.printf("(%s, %d), ", i.getKey(), i.getValue()); }
	 * System.out.print(" }\n"); }
	 */

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.print("not implemented");
		// TODO Auto-generated method stub

	}

}
