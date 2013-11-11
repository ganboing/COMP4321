import java.util.HashSet;

public final class PageDB {

	static private java.util.concurrent.ConcurrentMap<Integer, WebPageDescriptor> PageDesc;
	static private java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer ,Integer>> PageContent;
	static private java.util.concurrent.ConcurrentMap<Integer, Integer> PePageID2Time;
	static private java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer, Integer>> PePageTime2ID;
	static private java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer, Integer>> PageLink;
	static private java.util.NavigableSet<org.mapdb.Fun.Tuple2<Integer, Integer>> PageRvLink;
	static private java.util.concurrent.ConcurrentMap<String, Integer> PageIDByURL;
	static private java.util.concurrent.ConcurrentMap<Integer, String> PageURLByID;

	private static void DeleteLinks(Integer pageid) {
		java.util.NavigableSet<org.mapdb.Fun.Tuple2<Long, Long>> children = PageLink
				.subSet(org.mapdb.Fun.t2(pageid, null),
						org.mapdb.Fun.t2(pageid, org.mapdb.Fun.HI));
		for(org.mapdb.Fun.Tuple2<Long, Long> lnk : children)
		{
			PageRvLink.remove(org.mapdb.Fun.t2(lnk.b, lnk.a));
		}
		PageLink.removeAll(children);
	}

	private static Integer CreatePage(String url) {
		if(PageIDByURL.containsKey(url))
		{
			Integer ret = PageIDByURL.get(url);
			if(ret == null)
			{
				System.exit(-2);
			}
			return ret;
		}
		Integer assigned_id = Integer.valueOf(PageURLByID.size());
		PageIDByURL.put(url, assigned_id);
		PageURLByID.put(assigned_id, url);
		return assigned_id;
	}

	private static void AddLinks(Integer pageid, java.util.Set<Integer> links) {
		for (Integer child_id : links) {
			PageLink.add(org.mapdb.Fun.t2(pageid, child_id));
			PageRvLink.add(org.mapdb.Fun.t2(child_id, pageid));
		}
	}

	public static void UpdateLink(Integer pageID,
			java.util.Set<String> links_urls, double damp) {
		DeleteLinks(pageID);
		java.util.Set<Integer> link_resolved = new java.util.HashSet<Integer>();
		for (String url : links_urls) {
			Integer newpageid = CreatePage(url);
			link_resolved.add(newpageid);
		}
		AddLinks(pageID, link_resolved);
	}

	public static void UpadteDesc(Long pageID, WebPageDescriptor desc) {

	}
}
