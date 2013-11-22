public class Query {

	public static final class QueryResult {
		public Double Score = null;
		public String Title = null;
		public Long LastMod = null;

		public QueryResult(Double _score, String _title, Long _lastmod) {
			Score = _score;
			Title = _title;
			LastMod = _lastmod;
		}
		
		public void print()
		{
			java.util.Date lastmod = new java.util.Date(LastMod);
			System.out.printf("%4.2f %s %s\n", Score, lastmod.toString(), Title);
		}
	}

	public static volatile boolean should_continue = true;

	public static java.util.List<String> MostFreqTerm(Integer pageid,
			int max_term) {
		try {
			Init.DBSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		java.util.Set<org.mapdb.Fun.Tuple3<Integer, Integer, Integer>> page_content = PageDB
				.GetContent(pageid);
		java.util.Iterator<org.mapdb.Fun.Tuple3<Integer, Integer, Integer>> it = page_content
				.iterator();
		java.util.List<String> ret = new java.util.LinkedList<String>();
		for (int idx = 0; idx < max_term; idx++) {
			if (it.hasNext()) {
				ret.add(InvertedIdx.FindWordByID(it.next().c));
			} else {
				break;
			}
		}
		return ret;
	}

	public static java.util.List<QueryResult> PresentQueryResult(
			String query_term) {
		java.util.List<QueryResult> ret = new java.util.LinkedList<Query.QueryResult>();
		java.util.SortedSet<org.mapdb.Fun.Tuple2<Double, Integer>> result_rank = query(query_term);
		// java.util.List<E>
		for (org.mapdb.Fun.Tuple2<Double, Integer> page_rank_id : result_rank) {
			ret.add(new QueryResult(page_rank_id.a, PageDB
					.GetTitle(page_rank_id.b), PageDB
					.GetLastMod(page_rank_id.b)));
		}
		return ret;
	}

	public static void PrintQueryResult(java.util.List<QueryResult> result)
	{
		for(QueryResult r : result)
		{
			r.print();
		}
	}

	public static java.util.SortedSet<org.mapdb.Fun.Tuple2<Double, Integer>> query(
			String query_term) {

		if (!should_continue) {
			return null;
		}

		java.util.Map<String, Integer> keyword_weight_map = new java.util.HashMap<String, Integer>();
		java.util.Map<String, Integer> keyphase_weight_map = new java.util.HashMap<String, Integer>();

		java.util.regex.Matcher phase_matcher = StringProc
				.GetPhaseMatcher(query_term);

		int phase_end_pos = 0;

		while (phase_matcher.find()) {
			String previous_keywords = query_term.substring(phase_end_pos,
					phase_matcher.start());
			java.util.regex.Matcher keyword_matcher = StringProc
					.GetWordMatcher(previous_keywords);
			while (keyword_matcher.find()) {
				String keyword = keyword_matcher.group();
				Integer weight = keyword_weight_map.get(keyword);
				if (weight == null) {
					keyword_weight_map.put(keyword, 1);
				} else {
					keyword_weight_map.put(keyword, weight + 1);
				}
			}
			phase_end_pos = phase_matcher.end();
			String phase = phase_matcher.group();
			Integer weight = keyphase_weight_map.get(phase);
			if (weight == null) {
				keyphase_weight_map.put(phase, 1);
			} else {
				keyphase_weight_map.put(phase, weight + 1);
			}
		}
		String next_keywords = query_term.substring(phase_end_pos);
		java.util.regex.Matcher keyword_matcher = StringProc
				.GetWordMatcher(next_keywords);
		while (keyword_matcher.find()) {
			String keyword = keyword_matcher.group();
			Integer weight = keyword_weight_map.get(keyword);
			if (weight == null) {
				keyword_weight_map.put(keyword, 1);
			} else {
				keyword_weight_map.put(keyword, weight + 1);
			}
		}
		java.util.SortedSet<org.mapdb.Fun.Tuple2<Double, Integer>> ret = null;
		try {
			Init.DBSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		if (!should_continue) {
			Init.DBSem.release();
			return null;
		}
		ret = InvertedIdx.Query(keyword_weight_map, keyphase_weight_map);
		Init.DBSem.release();
		return ret;
	}
}
