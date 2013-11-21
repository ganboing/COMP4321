public class Query {

	public static volatile boolean should_continue = false;

	public static java.util.List<Integer> query(String query_term) {

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
		java.util.List<Integer> ret = null;
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
