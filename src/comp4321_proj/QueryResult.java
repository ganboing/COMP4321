package comp4321_proj;

public final class QueryResult implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1772646732843695246L;
	public Integer PageId = null;
	public Double Score = null;
	public String Title = null;
	public String LastMod = null;
	public String Url = null;

	public Double getScore() {
		return Score;
	}

	public String getTitle() {
		return Title;
	}

	public String getLastMod() {
		return LastMod;
	}

	public String getUrl() {
		return Url;
	}

	public Integer getPageId() {
		return PageId;
	}

	public QueryResult(Integer _Pageid, Double _score, String _title,
			Long _lastmod, String _url) {
		PageId = _Pageid;
		Score = _score;
		Title = _title;
		LastMod = new java.util.Date(_lastmod).toString();
		Url = _url;
	}

	public void print() {
		System.out.printf("%4.2f %s %s\n", Score, LastMod, Title);
	}
}