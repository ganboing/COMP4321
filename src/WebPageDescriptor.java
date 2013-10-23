public final class WebPageDescriptor implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1143624063023262874L;

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	
	/*long interalId;
	long collectedTime;
	long lastModifiedTime;
	
	public String pageURL;
	String children[]; //url
	String parents[]; //url
	*/
	

	private static java.util.regex.Pattern pattern_alpha = java.util.regex.Pattern.compile("[a-zA-Z]+");
	
	public java.net.URL page_url;
	public String title;
	public java.net.URL[] links;
	public java.util.Date last_mod;
	public long size;
	public KeyWordMap keyword_map;
	
	public WebPageDescriptor(java.net.URL _url, long _last_mod, String _title,
			String _content, java.net.URL[] _links) {

		last_mod = new java.util.Date(_last_mod);
		size = _content.length();
		page_url = _url;
		title = _title;
		links = _links;
		
		keyword_map = new KeyWordMap();
		// java.util.Scanner scanner = new java.util.Scanner(_content);
		java.util.regex.Matcher matcher = pattern_alpha.matcher(_content);
		while (matcher.find()) {
			String word = matcher.group();
			if ((word = StopStem.process_input_word(word)) != null)
				keyword_map.addWord(word);
		}
		
		if(Init.DEBUG)
		{
			print();
		}
		
		/*try {
			while (true) {
				String word = scanner.nextLine();
				
				if( (word = StopStem.process_input_word(word)) != null)
				keyword_map.addWord(word);
			}
		} catch (Exception e) {
			assert (e instanceof java.util.NoSuchElementException);
		}
		scanner.close();*/
		/*try {
			synchronized (Class.forName("IntermediatePageDescriptor")) {
				System.out.printf("Imm Page: %s\n", title);
				keyword_map.print();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		

	}
	
	public void print()
	{
		if(title != null)
		{
			System.out.printf("Page: %s\n", title);
		}
		else
		{
			System.out.print("Page: No title\n");
		}
		System.out.printf("URL: %s\n",page_url);
		if(last_mod != null)
		{
			System.out.print("Last Modified: ");
			System.out.println(last_mod);
		}
		System.out.printf("Page Size: %d\n", size);
		keyword_map.print();
		if(links != null)
		{
			for(java.net.URL u : links)
			{
				System.out.printf("ChildLink: %s\n", u);
			}
		}
	}
}
