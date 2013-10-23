public class Init {

	public static boolean DEBUG;
	
	public static jdbm.RecordManager recman;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		//assert(args.length == 2);
		if(args.length == 4)
		{
			DEBUG = args[3].equals("DEBUG");
			if(DEBUG)
			{
				System.out.println("debugging!!!");
			}
		}
		recman = jdbm.RecordManagerFactory.createRecordManager(args[0]);
		java.net.URL url_to_start = new java.net.URL(args[1]);
		int page_num_to_fetch = Integer.parseInt(args[2]);
		PageDB.Init(recman);
		PageDB.add_unresolved(url_to_start);
		while(true)
		{
			Thread.sleep(1000);
			if(PageDB.get_size() >= page_num_to_fetch)
			{
				break;
			}
			else
			{
				java.net.URL url_to_fetch = PageDB.get_unresolved();
				if(DEBUG)
				{
					System.out.printf("getting %s \n", url_to_fetch);
				}
				if(url_to_fetch == null)
				{
					break;
				}
				try{
					java.net.URLConnection url_connection = url_to_fetch.openConnection();
					long last_mod = url_connection.getLastModified();
					if(last_mod == 0)
					{
						last_mod = url_connection.getDate();
					}
					if(DEBUG)
					{
						System.out.printf("last mod == %d\n", last_mod);
					}
					org.htmlparser.Parser parser = new org.htmlparser.Parser(url_to_fetch.toString());
					String title;
					try{
						title = parser.extractAllNodesThatMatch(new org.htmlparser.filters.TagNameFilter("title")).elementAt(0).toPlainTextString();
					}
					catch (Exception e){
						title = null;
					}
					org.htmlparser.beans.StringBean sb = new org.htmlparser.beans.StringBean();
					sb.setURL(url_to_fetch.toString());
					String words = sb.getStrings();
					org.htmlparser.beans.LinkBean lb = new org.htmlparser.beans.LinkBean();
					lb.setURL(url_to_fetch.toString());
					java.net.URL[] links = lb.getLinks();
					WebPageDescriptor pageDesc = new WebPageDescriptor(url_to_fetch, last_mod, title, words, links);
					PageDB.addPage(url_to_fetch, links, pageDesc);
				}
				catch(Exception e)
				{
					if(DEBUG)
					{
						System.out.printf("fetching page %s failed\n", url_to_fetch.toString());
					}
					PageDB.addPage(url_to_fetch, null, null);
				}
			}
		}
		PageDB.printAll();
		PageDB.commit();
		recman.commit();
		recman.close();
		return;
	}

}
