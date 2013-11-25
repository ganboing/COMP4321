final class WrdPhIt extends TagIt<Integer, Integer> {
	java.util.Iterator<Integer> it = null;
	Integer pos = null;

	public WrdPhIt(int _slot, java.util.List<Integer> occur_list) {
		super(_slot);
		if (occur_list != null) {
			it = occur_list.iterator();
		}
	}

	@Override
	public Integer GetTag() {
		return pos - GetSlot();
	}

	@Override
	public boolean NextAndUpdate() {
		if (it != null) {
			if (it.hasNext()) {
				pos = it.next();
				return true;
			}
		}
		return false;
	}

	@Override
	public Integer GetVal() {
		return GetTag();
	}

}

public final class KeyWordDescriptor implements java.io.Serializable {

	public static class KeyWordCnt implements java.io.Serializable {

		private static final long serialVersionUID = 7033036500229205509L;

		public Integer title_occur = null;
		public Integer body_occur = null;

		KeyWordCnt(Integer _title_occur, Integer _body_occur) {
			title_occur = _title_occur;
			body_occur = _body_occur;
		}
	}

	private static final long serialVersionUID = 7787801390683099069L;

	public static KeyWordCnt ProcPhase(java.util.List<KeyWordDescriptor> phase) {
		int i = 0;
		TagItPool<Integer, Integer> title_phase = new TagItPool<Integer, Integer>();
		TagItPool<Integer, Integer> body_phase = new TagItPool<Integer, Integer>();
		for (KeyWordDescriptor keyworddesc : phase) {
			WrdPhIt title_it = new WrdPhIt(i, keyworddesc.title_occur);
			WrdPhIt body_it = new WrdPhIt(i, keyworddesc.body_occur);
			title_phase.AddIt(title_it);
			body_phase.AddIt(body_it);
			i++;
		}
		int title_occur_cnt = 0;
		int body_occur_cnt = 0;
		while (title_phase.GetNxtWholeTag() != null) {
			title_occur_cnt++;
		}
		while (body_phase.GetNxtWholeTag() != null) {
			body_occur_cnt++;
		}
		if((title_occur_cnt>0) || (body_occur_cnt>0))
		{
			return new KeyWordCnt(title_occur_cnt, body_occur_cnt);
		}
		return null;
	}

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
		if (title_occur != null) {
			return title_occur.size();
		}
		return 0;
	}

	public int Cnt() {
		return GetBodyOccur() + GetTitleOccur();
	}

	public KeyWordCnt GetCntObj() {
		return new KeyWordCnt(GetTitleOccur(), GetBodyOccur());
	}
}
