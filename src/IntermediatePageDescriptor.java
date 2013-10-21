public class IntermediatePageDescriptor {

	public String url_fetched;
	public KeyWordMap keyword_map;
	public java.net.URL[] links;

	public IntermediatePageDescriptor(String _url, String title, String _content,
			java.net.URL[] _links) {
		keyword_map = new KeyWordMap();
		java.util.Scanner scanner = new java.util.Scanner(_content);

		try {
			while (true) {
				String word = scanner.next();
				if( (word = StopStem.process_input_word(word)) != null)
				keyword_map.addWord(word);
			}
		} catch (Exception e) {
			assert (e instanceof java.util.NoSuchElementException);
		}

		scanner.close();
		try {
			synchronized (Class.forName("IntermediatePageDescriptor")) {
				System.out.printf("Imm Page: %s\n", title);
				keyword_map.print();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		url_fetched = _url;
		links = _links;
	}
}
