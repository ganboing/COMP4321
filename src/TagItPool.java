public final class TagItPool<E extends Comparable<E>, K> {

	java.util.PriorityQueue<TagIt<E, K>> itqueue = null;
	int orig_size = 0;

	public int GetOrigSize() {
		return orig_size;
	}

	private void PushItAndUpdate(TagIt<E, K> it) {
		if (it.NextAndUpdate()) {
			itqueue.add(it);
		}
	}

	private void PushItWithoutUpdate(TagIt<E, K> it) {
		itqueue.add(it);
	}

	public TagItPool() {
		itqueue = new java.util.PriorityQueue<TagIt<E, K>>();
	}

	public void AddIt(TagIt<E, K> it) {
		orig_size++;
		PushItAndUpdate(it);
	}

	public TagItPool(java.util.List<TagIt<E, K>> its) {
		orig_size = its.size();
		itqueue = new java.util.PriorityQueue<TagIt<E, K>>();
		for (TagIt<E, K> it : its) {
			PushItAndUpdate(it);
		}
	}

	public org.mapdb.Fun.Tuple2<E, K> poll() {
		TagIt<E, K> firstit = itqueue.poll();
		if (firstit != null) {
			E tag = firstit.GetTag();
			K val = firstit.GetVal();
			PushItAndUpdate(firstit);
			return org.mapdb.Fun.t2(tag, val);
		}
		return null;
	}

	public org.mapdb.Fun.Tuple2<E, java.util.List<org.mapdb.Fun.Tuple2<Integer, K>>> GetNxtVect() {
		TagIt<E, K> firstit;
		firstit = itqueue.poll();
		if (firstit != null) {
			E pageid = firstit.GetTag();
			java.util.List<org.mapdb.Fun.Tuple2<Integer, K>> ret = new java.util.LinkedList<org.mapdb.Fun.Tuple2<Integer, K>>();
			do {
				Integer slot = Integer.valueOf(firstit.GetSlot());
				K val = firstit.GetVal();
				ret.add(org.mapdb.Fun.t2(slot, val));
				PushItAndUpdate(firstit);
				firstit = itqueue.poll();
			} while ((firstit != null) && (firstit.GetTag() == pageid));
			if (firstit != null) {
				PushItWithoutUpdate(firstit);
			}
			return org.mapdb.Fun.t2(pageid, ret);
		}
		return null;
	}

	/*
	 * public org.mapdb.Fun.Tuple2<E, java.util.List<K>> GetNxtWholeVect() {
	 * TagIt<E, K> firstit = itqueue.poll(); while (firstit != null) {
	 * java.util.List<K> ret = null; E tag = firstit.GetTag(); for (int i = 0;
	 * (firstit.GetSlot() == i) && (firstit.GetTag() == tag); i++) { if (ret ==
	 * null) { ret = new java.util.LinkedList<K>(); } ret.add(firstit.GetVal());
	 * PushItToQueue(firstit); if (i == (orig_size - 1)) { return
	 * org.mapdb.Fun.t2(tag, ret); } firstit = itqueue.poll(); if (firstit ==
	 * null) { return null; } } PushItToQueue(firstit); firstit =
	 * itqueue.poll(); } return null; }
	 */

	public E GetNxtWholeTag() {
		TagIt<E, K> firstit = null;
		while ((firstit = itqueue.poll()) != null) {
			E tag = firstit.GetTag();
			for (int i = 0; (firstit.GetSlot() == i)
					&& (firstit.GetTag() == tag); i++) {
				PushItAndUpdate(firstit);
				if (i == (orig_size - 1)) {
					return tag;
				}
				firstit = itqueue.poll();
				if (firstit == null) {
					return null;
				}
			}
			PushItAndUpdate(firstit);
		}
		return null;
	}
}
