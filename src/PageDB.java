public final class PageDB {

	public static final class PageRankWorkingThread implements Runnable {

		@Override
		public void run() {

		}

	}

	// static private java.util.concurrent.ConcurrentMap<Integer,
	// WebPageDescriptor> PageDesc;
	static private java.util.NavigableSet<org.mapdb.Fun.Tuple3<Integer, Integer, Integer>> PageContent;
	/* Scheme (DocID, Occur_Cnt, WordID) */
	static private java.util.concurrent.ConcurrentMap<Integer, String> PageTitle;
	// static private java.util.concurrent.ConcurrentMap<Integer, Integer>
	// PagemaxTf;
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
	public static void InitOriginal(org.mapdb.DB SE_DB) {
		if (Init.DEBUG) {
			System.out.println("PageDB Orig Init");
		}
		PageContent = SE_DB.createTreeSet("PAGE_DB_PageContent")
				.serializer(org.mapdb.BTreeKeySerializer.TUPLE3).make();
		PageTitle = SE_DB.createTreeMap("PAGE_DB_PageTitle").make();
		PageLastMod = SE_DB.createTreeMap("PAGE_DB_PageLastMod").make();
		PageRankScore = SE_DB.createTreeMap("PAGE_DB_PageRankScore").make();
		PagePending = SE_DB.createTreeSet("PAGE_DB_PagePending").make();
		PageLink = SE_DB.createTreeSet("PAGE_DB_PageLink")
				.serializer(org.mapdb.BTreeKeySerializer.TUPLE2).make();
		PageRvLink = SE_DB.createTreeSet("PAGE_DB_PageRvLink")
				.serializer(org.mapdb.BTreeKeySerializer.TUPLE2).make();
		PageIDByURL = SE_DB.createTreeMap("PAGE_DB_PageIDByURL")
				.keepCounter(true).make();
		PageURLByID = SE_DB.createTreeMap("PAGE_DB_PageURLByID")
				.keepCounter(true).make();
		AddPendingByUrl("http://www.ust.hk/eng/other/sitemap.htm");
	}

	public static void Init(org.mapdb.DB SE_DB) {
		if (Init.DEBUG) {
			System.out.println("PageDB Init");
		}
		PageContent = SE_DB.getTreeSet("PAGE_DB_PageContent");
		PageTitle = SE_DB.getTreeMap("PAGE_DB_PageTitle");
		PageLastMod = SE_DB.getTreeMap("PAGE_DB_PageLastMod");
		PageRankScore = SE_DB.getTreeMap("PAGE_DB_PageRankScore");
		PagePending = SE_DB.getTreeSet("PAGE_DB_PagePending");
		PageLink = SE_DB.getTreeSet("PAGE_DB_PageLink");
		PageRvLink = SE_DB.getTreeSet("PAGE_DB_PageRvLink");
		PageIDByURL = SE_DB.getTreeMap("PAGE_DB_PageIDByURL");
		PageURLByID = SE_DB.getTreeMap("PAGE_DB_PageURLByID");
	}

	private static boolean URL_Filter(String url) {
		return true;
	}

	public static Integer PollOnePending() {
		return PagePending.pollFirst();
	}

	public static void AddPendingByUrl(String PageUrl) {
		Integer pageid = CreatePage(PageUrl);
		if (pageid != null) {
			AddOnePending(pageid);
		}
	}

	public static void AddOnePending(Integer pageid) {
		PagePending.add(pageid);
	}

	public static String GetPageUrl(Integer Page_id) {
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
		if (URL_Filter(url)) {
			Integer assigned_id = Integer.valueOf(PageURLByID.size());
			PagePending.add(assigned_id);
			PageIDByURL.put(url, assigned_id);
			PageURLByID.put(assigned_id, url);
			return assigned_id;
		} else {
			return null;
		}
	}

	private static void CreateLink(Integer parent, Integer child) {
		PageLink.add(org.mapdb.Fun.t2(parent, child));
		PageRvLink.add(org.mapdb.Fun.t2(child, parent));
	}

	public static void UpdateLink(Integer pageid,
			java.util.Set<String> links_urls, double damp) {
		// DeleteLinks(pageID);
		if (links_urls != null) {
			for (String url : links_urls) {
				Integer newpageid = CreatePage(url);
				if (newpageid != null) {
					CreateLink(pageid, newpageid);
				}
			}
		}
	}

	public static void AddDocWord(Integer pageID, Integer word_id,
			Integer word_freq) {
		PageContent.add(org.mapdb.Fun.t3(pageID, word_freq, word_id));
	}

	public static void CreateMeta(Integer pageID, String title, Long last_mod) {
		PageTitle.put(pageID, title);
		PageLastMod.put(pageID, last_mod);
		// PagemaxTf.put(pageID, max_tf);
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
