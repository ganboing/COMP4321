public abstract class TagIt<E extends Comparable<E>, V> implements
		Comparable<TagIt<E, V>> {
	int slot;

	public TagIt(int _slot) {
		slot = _slot;
	}

	public int GetSlot() {
		return slot;
	}

	public abstract E GetTag();

	public abstract boolean NextAndUpdate();

	public abstract V GetVal();

	public int compareTo(TagIt<E, V> o) {
		if (this.GetTag().compareTo(o.GetTag()) == 0) {
			return this.GetSlot() - o.GetSlot();
		}
		return this.GetTag().compareTo(o.GetTag());
	}
}