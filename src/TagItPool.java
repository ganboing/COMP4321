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
				if (firstit == null) {
					return org.mapdb.Fun.t2(pageid, ret);
				}
			} while ((firstit.GetTag() == pageid));
			PushItWithoutUpdate(firstit);
			return org.mapdb.Fun.t2(pageid, ret);
		}
		return null;
	}

	public org.mapdb.Fun.Tuple2<E, java.util.List<K>> GetNxtWholeVect() {
		java.util.List<K> ret = null;
		TagIt<E, K> firstit = itqueue.poll();
		if (firstit == null) {
			return null;
		}
		while (true) {
			while (firstit.GetSlot() != 0) {
				PushItAndUpdate(firstit);
				firstit = itqueue.poll();
				if (firstit == null) {
					return null;
				}
			}
			E tag = firstit.GetTag();
			int nxt_slot = 0;
			ret = new java.util.LinkedList<K>();
			do {
				ret.add(firstit.GetVal());
				nxt_slot++;
				PushItAndUpdate(firstit);
				if (nxt_slot == orig_size) {
					return org.mapdb.Fun.t2(tag, ret);
				}
				firstit = itqueue.poll();
				if (firstit == null) {
					return null;
				}
			} while ((firstit.GetTag() == tag)
					&& (firstit.GetSlot() == nxt_slot));
		}
	}

	public E GetNxtWholeTag() {
		TagIt<E, K> firstit = itqueue.poll();
		if (firstit == null) {
			return null;
		}
		while (true) {
			while (firstit.GetSlot() != 0) {
				PushItAndUpdate(firstit);
				firstit = itqueue.poll();
				if (firstit == null) {
					return null;
				}
			}
			E tag = firstit.GetTag();
			int nxt_slot = 0;
			do {
				nxt_slot++;
				PushItAndUpdate(firstit);
				if (nxt_slot == orig_size) {
					return tag;
				}
				firstit = itqueue.poll();
				if (firstit == null) {
					return null;
				}
			} while ((firstit.GetTag() == tag)
					&& (firstit.GetSlot() == nxt_slot));
		}
	}
}
