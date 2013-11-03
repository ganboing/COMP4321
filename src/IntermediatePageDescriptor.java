public class IntermediatePageDescriptor {

	private static java.util.regex.Pattern pattern_alpha = java.util.regex.Pattern
			.compile("[a-zA-Z]+");

	public java.net.URL page_url;
	public String title;
	public java.util.Set<java.net.URL> links;
	public java.util.Date last_mod;
	public long size;
	public KeyWordMap keyword_map;

	public IntermediatePageDescriptor(java.net.URL _url, long _last_mod, String _title,
			String _content, java.net.URL[] _links) {

		last_mod = new java.util.Date(_last_mod);
		size = _content.length();
		page_url = _url;
		title = _title;
		

		keyword_map = new KeyWordMap();
		// java.util.Scanner scanner = new java.util.Scanner(_content);
		java.util.regex.Matcher matcher_body = pattern_alpha.matcher(_content);
		while (matcher_body.find()) {
			String word = matcher_body.group();
			int pos = matcher_body.start();
			if ((word = StopStem.process_input_word(word)) != null) {
				keyword_map.addBodyOccur(word, pos);
			}
		}
		java.util.regex.Matcher matcher_title = pattern_alpha.matcher(title);
		while (matcher_title.find()) {
			String word = matcher_title.group();
			int pos = matcher_title.start();
			if ((word = StopStem.process_input_word(word)) != null) {
				keyword_map.addTitleOccur(word, pos);
			}
		}
		for(java.net.URL lnk : _links)
		{
			if(links == null)
			{
				links = new java.util.HashSet<java.net.URL>();
			}
			links.add(lnk);
		}
	}
}
