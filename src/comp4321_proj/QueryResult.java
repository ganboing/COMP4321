package comp4321_proj;
public final class QueryResult implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1772646732843695246L;
	public Double Score = null;
	public String Title = null;
	public Long LastMod = null;
	public String Url = null;

	public QueryResult(Double _score, String _title, Long _lastmod,
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