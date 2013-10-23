public final class PageDB {

	private static jdbm.htree.HTree pageID_info;
	private static jdbm.htree.HTree pageURL_ID;

	private static long HANDLE_URL_tobesolved;
	private static java.util.HashSet<java.net.URL> URL_tobesolved;

	private static java.util.EnumMap<PARA, Long> para;
	private static long HANDLE_para;

	private enum PARA {
		MAX_PAGE_ID, PAGE_DB_SIZE
	};

	private static long Max_Page_Id;
	private static long Page_DB_Size;

	private static jdbm.RecordManager recman;

	public static void Init(jdbm.RecordManager _recman) {

		try {
			recman = _recman;

			HANDLE_URL_tobesolved = recman.getNamedObject("URL_RESOLVE_SET");
			if (HANDLE_URL_tobesolved != 0) {
				URL_tobesolved = (java.util.HashSet<java.net.URL>) (recman
						.fetch(HANDLE_URL_tobesolved));
				if (Init.DEBUG) {
					System.out.printf("URL_RESOLVE_SET loaded with size %d\n",
							URL_tobesolved.size());
				}
			} else {
				URL_tobesolved = new java.util.HashSet<java.net.URL>();
				HANDLE_URL_tobesolved = recman.insert(URL_tobesolved);
				recman.setNamedObject("URL_RESOLVE_SET", HANDLE_URL_tobesolved);
				if (Init.DEBUG) {
					System.out.printf("URL_RESOLVE_SET created\n");
				}
			}

			HANDLE_para = recman.getNamedObject("PARA");
			if (HANDLE_para != 0) {
				// System.out.printf("max_pageid found!\n");
				para = ((java.util.EnumMap<PARA, Long>) recman
						.fetch(HANDLE_para));
				Max_Page_Id = para.get(PARA.MAX_PAGE_ID).longValue();
				Page_DB_Size = para.get(PARA.PAGE_DB_SIZE).longValue();
				if (Init.DEBUG) {
					System.out
							.printf("PARA Loaded, Max_Page_Id == %d, Page_DB_Size == %d\n",
									Max_Page_Id, Page_DB_Size);
				}
			} else {
				para = new java.util.EnumMap<PARA, Long>(PARA.class);
				Max_Page_Id = 1;
				Page_DB_Size = 0;
				HANDLE_para = recman.insert(para);
				recman.setNamedObject("PARA", HANDLE_para);
				if (Init.DEBUG) {
					System.out.printf("PARA Created\n");
				}
			}

			{
				long HANDLE_pageID_info = recman.getNamedObject("PAGE_ID_INFO");
				if (HANDLE_pageID_info != 0) {
					pageID_info = jdbm.htree.HTree.load(recman,
							HANDLE_pageID_info);
					if (Init.DEBUG) {
						System.out.printf("PAGE_ID_INFO Loaded\n");
					}
				} else {
					pageID_info = jdbm.htree.HTree.createInstance(recman);
					recman.setNamedObject("PAGE_ID_INFO",
							pageID_info.getRecid());
					if (Init.DEBUG) {
						System.out.printf("PAGE_ID_INFO Created\n");
					}
				}
			}

			{
				long HANDLE_pageURL_ID = recman.getNamedObject("PAGE_URL_ID");
				if (HANDLE_pageURL_ID != 0) {
					pageURL_ID = jdbm.htree.HTree.load(recman,
							HANDLE_pageURL_ID);
					if (Init.DEBUG) {
						System.out.printf("PAGE_URL_ID Loaded\n");
					}
				} else {
					pageURL_ID = jdbm.htree.HTree.createInstance(recman);
					recman.setNamedObject("PAGE_URL_ID", pageURL_ID.getRecid());
					if (Init.DEBUG) {
						System.out.printf("PAGE_URL_ID Created\n");
					}
				}
			}
		} catch (java.io.IOException e) {
			System.exit(-2);
			e.printStackTrace();
		}
	}

	public static void commit() {
		try {
			para.put(PARA.MAX_PAGE_ID, Long.valueOf(Max_Page_Id));
			para.put(PARA.PAGE_DB_SIZE, Long.valueOf(Page_DB_Size));
			recman.update(HANDLE_URL_tobesolved, URL_tobesolved);
			recman.update(HANDLE_para, para);
		} catch (java.io.IOException e) {
			System.exit(-2);
			e.printStackTrace();
		}
	}

	public static long addPage(java.net.URL url, java.net.URL[] links,
			WebPageDescriptor pageDesc) {
		Long PageId = null;
		try {
			PageId = (Long) pageURL_ID.get(url);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		if (PageId == null) {
			if (URL_tobesolved.contains(url)) {
				URL_tobesolved.remove(url);
			}
			PageId = new Long(Max_Page_Id);
			try {
				pageURL_ID.put(url, PageId);
				pageID_info.put(PageId, pageDesc);
			} catch (java.io.IOException e) {
				System.exit(-2);
				e.printStackTrace();
			}
			Max_Page_Id++;
			Page_DB_Size++;
			if (links != null) {
				for (java.net.URL u : links) {
					try {
						if (pageURL_ID.get(u) == null) {
							URL_tobesolved.add(u);
						}
					} catch (java.io.IOException e) {
						System.exit(-2);
						e.printStackTrace();
					}
				}
			}
		}
		return PageId.longValue();
	}

	public static long get_size() {
		return Page_DB_Size;
	}

	public static java.net.URL get_unresolved() {
		java.util.Iterator<java.net.URL> i = URL_tobesolved.iterator();
		if(i.hasNext())
		{
			return i.next();
		}
		return null;
	}

	public static void add_unresolved(java.net.URL url) {
		try {
			if (pageURL_ID.get(url) == null) {
				URL_tobesolved.add(url);
			}
		} catch (java.io.IOException e) {
			System.exit(-2);
			e.printStackTrace();
		}
	}

	public static void printAll() {
		try {
			jdbm.helper.FastIterator i;
			i = pageID_info.values();
			WebPageDescriptor desc = null;
			while ((desc = (WebPageDescriptor) (i.next())) != null) {
				desc.print();
				System.out.printf("--------------------------------------------------\n");
			}
		} catch (java.io.IOException e) {
			System.exit(-2);
			e.printStackTrace();
		}

	}
}
