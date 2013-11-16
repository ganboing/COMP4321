public final class KeyWordDescriptor implements java.io.Serializable {

	public static class KeyWordCnt implements java.io.Serializable {

		private static final long serialVersionUID = 7033036500229205509L;

		public Integer title_occur;
		public Integer body_occur;

		KeyWordCnt(Integer _title_occur, Integer _body_occur) {
			title_occur = _title_occur;
			body_occur = _body_occur;
		}
	}

	static final long serialVersionUID = 7787801390683099069L;

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

	public int GetBodyOccur() {
		if (body_occur != null) {
			return body_occur.size();
		}
		return 0;
	}

	public int GetTitleOccur() {
		if (body_occur != null) {
			return body_occur.size();
		}
		return 0;
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

	public KeyWordCnt GetCntObj() {
		return new KeyWordCnt(GetTitleOccur(), GetBodyOccur());
	}
}
