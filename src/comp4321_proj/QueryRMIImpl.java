package comp4321_proj;
public class QueryRMIImpl extends java.rmi.server.UnicastRemoteObject implements
		QueryRMIInterface {

	public QueryRMIImpl() throws java.rmi.RemoteException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6039432783779490301L;

	

	public java.util.List<String> MostFreqTerm(Integer pageid, int max_term) throws java.rmi.RemoteException {
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

	public void PrintQueryResult(java.util.List<QueryResultEle> result) {
		for (QueryResultEle r : result) {
			r.print();
		}
	}

	public java.util.SortedSet<org.mapdb.Fun.Tuple2<Double, Integer>> query(
			String query_term) {

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
		ret = InvertedIdx.Query(keyword_weight_map, keyphase_weight_map);
		Init.DBSem.release();
		return ret;
	}

	@Override
	public java.util.List<QueryResultEle> Query(String query_term)
			throws java.rmi.RemoteException {
		System.out.printf("searching for %s\n", query_term);
		java.util.List<QueryResultEle> ret = new java.util.LinkedList<QueryResultEle>();
		java.util.SortedSet<org.mapdb.Fun.Tuple2<Double, Integer>> result_rank = query(query_term);
		try {
			Init.DBSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		for (org.mapdb.Fun.Tuple2<Double, Integer> page_rank_id : result_rank) {
			ret.add(0,new QueryResultEle(page_rank_id.a, PageDB
					.GetTitle(page_rank_id.b), PageDB
					.GetLastMod(page_rank_id.b), PageDB
					.GetPageUrl(page_rank_id.b)));
		}
		Init.DBSem.release();
		return ret;
	}

	@Override
	public java.util.List<String> GetAllWord() throws java.rmi.RemoteException {
		try {
			Init.DBSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		java.util.List<String> ret = InvertedIdx.GetAllWord();
		Init.DBSem.release();
		return ret;
	}
}
