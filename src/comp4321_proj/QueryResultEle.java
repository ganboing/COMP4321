package comp4321_proj;
public final class QueryResultEle {
	public Double Score = null;
	public String Title = null;
	public Long LastMod = null;
	public String Url = null;

	public QueryResultEle(Double _score, String _title, Long _lastmod,
			String _url) {
		Score = _score;
		Title = _title;
		LastMod = _lastmod;
		Url = _url;
	}

	public void print() {
		java.util.Date lastmod = new java.util.Date(LastMod);
		System.out.printf("%4.2f %s %s\n", Score, lastmod.toString(), Title);
	}
}