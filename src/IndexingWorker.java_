public class IndexingWorker implements java.lang.Runnable {

	java.net.URL url_to_fetch;

	public IndexingWorker(java.net.URL _url) {
		url_to_fetch = _url;
	}

	@Override
	public void run() {

		try {
			java.net.URLConnection url_connection = url_to_fetch
					.openConnection();
			long last_mod = url_connection.getLastModified();
			if (last_mod == 0) {
				last_mod = url_connection.getDate();
			}
			if (Init.DEBUG) {
				System.out.printf("last mod == %d\n", last_mod);
			}
			org.htmlparser.Parser parser = new org.htmlparser.Parser(
					url_to_fetch.toString());
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
			org.htmlparser.beans.StringBean sb = new org.htmlparser.beans.StringBean();
			sb.setURL(url_to_fetch.toString());
			String words = sb.getStrings();
			org.htmlparser.beans.LinkBean lb = new org.htmlparser.beans.LinkBean();
			lb.setURL(url_to_fetch.toString());
			java.net.URL[] links = lb.getLinks();
			WebPageDescriptor pageDesc = new WebPageDescriptor(url_to_fetch,
					last_mod, title, words, links);
			PageDB.addPage(url_to_fetch, links, pageDesc);
			
		} catch (Exception e) {

		}
	}

}

/*
 * if(DEBUG) { System.out.printf("getting %s \n", url_to_fetch); } if( == null)
 * { break; } try{ java.net.URLConnection url_connection =
 * url_to_fetch.openConnection(); long last_mod =
 * url_connection.getLastModified(); if(last_mod == 0) { last_mod =
 * url_connection.getDate(); } if(DEBUG) { System.out.printf("last mod == %d\n",
 * last_mod); } org.htmlparser.Parser parser = new
 * org.htmlparser.Parser(url_to_fetch.toString()); String title; try{ title =
 * parser.extractAllNodesThatMatch(new
 * org.htmlparser.filters.TagNameFilter("title"
 * )).elementAt(0).toPlainTextString(); } catch (Exception e){ title = null; }
 * org.htmlparser.beans.StringBean sb = new org.htmlparser.beans.StringBean();
 * sb.setURL(url_to_fetch.toString()); String words = sb.getStrings();
 * org.htmlparser.beans.LinkBean lb = new org.htmlparser.beans.LinkBean();
 * lb.setURL(url_to_fetch.toString()); java.net.URL[] links = lb.getLinks();
 * WebPageDescriptor pageDesc = new WebPageDescriptor(url_to_fetch, last_mod,
 * title, words, links); PageDB.addPage(url_to_fetch, links, pageDesc); }
 * catch(Exception e) { if(DEBUG) {
 * System.out.printf("fetching page %s failed\n", url_to_fetch.toString()); }
 * PageDB.addPage(url_to_fetch, null, null); }
 */