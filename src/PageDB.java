public final class PageDB {

	public static final class PageRankWorkingThread implements Runnable
	{

		@Override
		public void run() {
			
		}
		
	}
	
	// static private java.util.concurrent.ConcurrentMap<Integer,
	// WebPageDescriptor> PageDesc;
	static private java.util.NavigableSet<org.mapdb.Fun.Tuple3<Integer, Integer, Integer>> PageContent;
	/* Scheme (DocID, Occur_Cnt, WordID) */
	static private java.util.concurrent.ConcurrentMap<Integer, String> PageTitle;
	static private java.util.concurrent.ConcurrentMap<Integer, Integer> PagemaxTf;
	static private java.util.concurrent.ConcurrentMap<Integer, Long> PageLastMod;
	static private java.util.concurrent.ConcurrentNavigableMap<Integer, Double> PageRankScore;
	static private java.util.NavigableSet<Integer> PagePending;
	/*
	 * static private java.util.concurrent.ConcurrentMap<Integer, Integer>
	 * PePageID2TTL; static private
	 * java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer, Integer>>
	 * PePageTTL2ID;
	 */
	static private java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer, Integer>> PageLink;
	static private java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer, Integer>> PageRvLink;
	static private java.util.concurrent.ConcurrentMap<String, Integer> PageIDByURL;
	static private java.util.concurrent.ConcurrentMap<Integer, String> PageURLByID;

	/*
	 * private static void DeleteLinks(Integer pageid) {
	 * java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer, Integer>> children =
	 * PageLink .subSet(org.mapdb.Fun.t2(pageid, Integer.valueOf(0)), true,
	 * org.mapdb.Fun.t2(pageid, Integer.valueOf(Integer.MAX_VALUE)), false); for
	 * (org.mapdb.Fun.Tuple2<Integer, Integer> lnk : children) {
	 * PageRvLink.remove(org.mapdb.Fun.t2(lnk.b, lnk.a)); }
	 * PageLink.removeAll(children); }
	 */

	public static Integer GetOnePending() {
		return PagePending.pollFirst();
	}
	
	public static String GetPageUrl(Integer Page_id)
	{
		return PageURLByID.get(Page_id);
	}

	private static Integer CreatePage(String url) {
		if (PageURLByID.size() != PageIDByURL.size()) {
			System.exit(-2);
		}
		if (PageIDByURL.containsKey(url)) {
			Integer ret = PageIDByURL.get(url);
			if (ret == null) {
				System.exit(-2);
			}
			return ret;
		}
		Integer assigned_id = Integer.valueOf(PageURLByID.size());
		PagePending.add(assigned_id);
		PageIDByURL.put(url, assigned_id);
		PageURLByID.put(assigned_id, url);
		return assigned_id;
	}

	private static void CreateLinks(Integer pageid, java.util.Set<Integer> links) {
		for (Integer child_id : links) {
			PageLink.add(org.mapdb.Fun.t2(pageid, child_id));
			PageRvLink.add(org.mapdb.Fun.t2(child_id, pageid));
		}
	}

	public static void UpdateLink(Integer pageID,
			java.util.Set<String> links_urls, double damp) {
		// DeleteLinks(pageID);
		java.util.Set<Integer> link_resolved = new java.util.HashSet<Integer>();
		for (String url : links_urls) {
			Integer newpageid = CreatePage(url);
			link_resolved.add(newpageid);
		}
		CreateLinks(pageID, link_resolved);
	}

	public static void CreateContent(Integer pageID,
			java.util.Map<Integer, Integer> words) {
		PagePending.remove(pageID);
		for (java.util.Map.Entry<Integer, Integer> wordid_cnt_pair : words
				.entrySet()) {
			PageContent.add(org.mapdb.Fun.t3(pageID,
					wordid_cnt_pair.getValue(), wordid_cnt_pair.getKey()));
		}
	}

	public static void CreateMeta(Integer pageID, String title, Integer max_tf,
			Long last_mod) {
		PageTitle.put(pageID, title);
		PageLastMod.put(pageID, last_mod);
		PagemaxTf.put(pageID, max_tf);
	}

	public static String GetTitle(Integer pageID) {
		return PageTitle.get(pageID);
	}

	public static Long GetLastMod(Integer pageID) {
		return PageLastMod.get(pageID);
	}

	public static java.util.Set<org.mapdb.Fun.Tuple3<Integer, Integer, Integer>> GetContent(
			Integer pageID) {
		return PageContent.subSet(org.mapdb.Fun.t3(pageID, Integer.valueOf(0),
				Integer.valueOf(0)), true, org.mapdb.Fun.t3(pageID,
				Integer.valueOf(Integer.MAX_VALUE),
				Integer.valueOf(Integer.MAX_VALUE)), false);
	}
}
