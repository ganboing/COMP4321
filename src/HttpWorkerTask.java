final class HttpWorkerTask implements
		java.util.concurrent.Callable<IntermediatePageDescriptor> {
	public static final class FetchException extends Exception {
		private static final long serialVersionUID = 3151508604775978819L;
		public Integer pageid = null;
		public FetchException(Integer _pageid) {
			super();
			pageid = _pageid;
		}
	}

	static volatile boolean should_continue = true;

	public static void stop_fetching() {
		should_continue = false;
	}

	Integer page_id;
	String url_to_fetch;

	public HttpWorkerTask(String _url_to_fetch, Integer _page_id) {
		url_to_fetch = _url_to_fetch;
		page_id = _page_id;
	}

	@Override
	public IntermediatePageDescriptor call() throws Exception {
		System.out.printf("HttpWorker For %s is running\n", url_to_fetch);
		java.net.URL url = new java.net.URL(url_to_fetch);
		try {
			if (!should_continue) {
				throw new Exception("discontinued\n");
			}
			java.net.URLConnection url_connection = url.openConnection();
			long last_mod = url_connection.getLastModified();
			if (last_mod == 0) {
				last_mod = url_connection.getDate();
			}
			if (Init.DEBUG) {
				System.out.printf("last mod == %d\n", last_mod);
			}
			org.htmlparser.Parser parser = new org.htmlparser.Parser(
					url_to_fetch);
			// String title = new
			// org.htmlparser.visitors.HtmlPage(parser).getTitle();
			String title;
			try {
				title = parser
						.extractAllNodesThatMatch(
								new org.htmlparser.filters.TagNameFilter(
										"title")).elementAt(0)
						.toPlainTextString();
			} catch (Exception e) {
				title = null;
			}
			if (!should_continue) {
				throw new Exception("discontinued\n");
			}
			org.htmlparser.beans.StringBean sb = new org.htmlparser.beans.StringBean();
			sb.setURL(url_to_fetch);
			String words = sb.getStrings();
			if (!should_continue) {
				throw new Exception("discontinued\n");
			}
			org.htmlparser.beans.LinkBean lb = new org.htmlparser.beans.LinkBean();
			lb.setURL(url_to_fetch);
			java.net.URL[] URL_array = lb.getLinks();
			if (!should_continue) {
				throw new Exception("discontinued\n");
			}
			return new IntermediatePageDescriptor(page_id, last_mod, title,
					words, URL_array);
		} catch (Exception e) {
			throw new FetchException(page_id);
		} finally {
			if (Init.DEBUG) {
				System.out.printf("HttpWorker For %s is finished\n",
						url_to_fetch);
			}
		}
	}
}