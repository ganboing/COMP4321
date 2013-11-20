public class IntermediatePageDescriptor {

	public Integer PageID = null;
	public String title = null;
	public java.util.Set<String> links = null;
	public long last_mod = 0;
	public KeyWordMap keyword_map = null;
	public boolean interrupted = false;

	public IntermediatePageDescriptor(Integer page_id, boolean should_pending)
	{
		PageID = page_id;
		interrupted = should_pending;
	}
	
	public IntermediatePageDescriptor(Integer page_id, long _last_mod,
			String _title, String _content, java.net.URL[] _links) {

		last_mod = _last_mod;
		title = _title;
		keyword_map = new KeyWordMap();
		// java.util.Scanner scanner = new java.util.Scanner(_content);
		java.util.regex.Matcher matcher_body = StringProc
				.GetWordMatcher(_content);
		int i = 0;
		while (matcher_body.find()) {
			String word = matcher_body.group();
			if ((word = StopStem.process_input_word(word)) != null) {
				keyword_map.addBodyOccur(word, i);
			}
			i++;
		}
		java.util.regex.Matcher matcher_title = StringProc
				.GetWordMatcher(_title);
		i = 0;
		while (matcher_title.find()) {
			String word = matcher_title.group();
			if ((word = StopStem.process_input_word(word)) != null) {
				keyword_map.addTitleOccur(word, i);
			}
			i++;
		}
		for (java.net.URL lnk : _links) {
			if (links == null) {
				links = new java.util.HashSet<String>();
			}
			links.add(lnk.toString());
		}
	}
}
