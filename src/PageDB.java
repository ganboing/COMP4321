public final class PageDB {

	public static final class PageRankWorkingTask implements Runnable {

		public volatile boolean should_continue = false;

		@Override
		public void run() {
			while (should_continue) {
				try {
					Init.DBSem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-2);
				}
				java.util.List<Double> tmp_result_list = new java.util.LinkedList<Double>();
				{
					java.util.Iterator<java.util.Map.Entry<Integer, Double>> it = PageRankScore
							.entrySet().iterator();
					while (it.hasNext()) {
						java.util.Map.Entry<Integer, Double> ent = it.next();
						Integer pageid = ent.getKey();
						double rank_score = 0;
						for (org.mapdb.Fun.Tuple2<Integer, Integer> rvlnk : PageRvLink
								.subSet(org.mapdb.Fun.t2(pageid, 0), true,
										org.mapdb.Fun.t2(pageid,
												Integer.MAX_VALUE), false)) {
							rank_score += (PageRankScore.get(rvlnk.b))
									/ (PageLnkCnt.get(rvlnk.b));
						}
						tmp_result_list.add(rank_score / 2 + 0.5);
					}
				}
				{
					java.util.Iterator<java.util.Map.Entry<Integer, Double>> map_it = PageRankScore
							.entrySet().iterator();
					java.util.Iterator<Double> list_it = tmp_result_list
							.iterator();
					while (map_it.hasNext()) {
						map_it.next().setValue(list_it.next());
					}
					if (list_it.hasNext()) {
						System.exit(-3);
					}
				}
				Init.DBSem.release();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-2);
				}
			}
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

	static private java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer, Integer>> PageLink;
	static private java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer, Integer>> PageRvLink;
	static private java.util.concurrent.ConcurrentNavigableMap<Integer, Integer> PageLnkCnt;
	static private java.util.concurrent.ConcurrentMap<String, Integer> PageIDByURL;
	static private java.util.concurrent.ConcurrentMap<Integer, String> PageURLByID;

	// static private String FIL_SITE = "cse.ust.hk";
	static private String FIL_SITE = "";

	static private PageRankWorkingTask pagerank_runnable = null;
	static private Thread pagerank_thread = null;

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
		PagePending = SE_DB.createTreeSet("PAGE_DB_PagePending")
				.keepCounter(true).make();
		PageLink = SE_DB.createTreeSet("PAGE_DB_PageLink")
				.serializer(org.mapdb.BTreeKeySerializer.TUPLE2).make();
		PageRvLink = SE_DB.createTreeSet("PAGE_DB_PageRvLink")
				.serializer(org.mapdb.BTreeKeySerializer.TUPLE2).make();
		PageIDByURL = SE_DB.createTreeMap("PAGE_DB_PageIDByURL")
				.keepCounter(true).make();
		PageURLByID = SE_DB.createTreeMap("PAGE_DB_PageURLByID")
				.keepCounter(true).make();
		PageLnkCnt = SE_DB.createTreeMap("PAGE_DB_PageLnkCnt").make();
		AddPendingByUrl("http://www.cse.ust.hk/~ericzhao/COMP4321/TestPages/testpage.htm");
		// AddPendingByUrl("http://www.cse.ust.hk");
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
		PageLnkCnt = SE_DB.getTreeMap("PAGE_DB_PageLnkCnt");
	}

	private static boolean URL_Filter(String url) {
		try {
			java.net.URI uri = new java.net.URI(url);
			String domain = uri.getHost();
			if (domain.length() >= FIL_SITE.length()) {
				if (domain.substring(domain.length() - FIL_SITE.length())
						.equals(FIL_SITE)) {
					// System.out.println(domain);
					return true;
				}
			}
			return false;
		} catch (java.net.URISyntaxException e) {
			return false;
		}
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
			// System.out.printf("Adding page %s id %d\n", url, assigned_id);
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
		int lnk_cnt = 0;
		if (links_urls != null) {
			for (String url : links_urls) {
				if (Init.DEBUG) {
					System.out.printf("Adding Link to %s\n", url);
				}
				Integer newpageid = CreatePage(url);
				if (newpageid != null) {
					lnk_cnt++;
					CreateLink(pageid, newpageid);
				}
			}
		}
		PageLnkCnt.put(pageid, lnk_cnt);
	}

	public static void AddDocWord(Integer pageID, Integer word_id,
			Integer word_freq) {
		PageContent.add(org.mapdb.Fun.t3(pageID, word_freq, word_id));
	}

	public static void CreateMeta(Integer pageID, String title, Long last_mod) {
		assert (pageID != null);
		if (title == null) {
			title = new String("");
		}
		assert (last_mod != null);
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

	public static void StartPageRankWorker() {
		pagerank_runnable = new PageRankWorkingTask();
		pagerank_thread = new Thread(pagerank_runnable);
		pagerank_runnable.should_continue = true;
		pagerank_thread.start();
	}

	public static void StopPageRankWorker() {
		pagerank_runnable.should_continue = false;
		try {
			pagerank_thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		pagerank_thread = null;
		pagerank_runnable = null;

	}

	public static int GetExistPageSize() {
		if (PageURLByID.size() != PageIDByURL.size()) {
			System.exit(-2);
		}
		return PageURLByID.size() - PagePending.size();
	}
}
