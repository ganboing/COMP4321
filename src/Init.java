public class Init {

	public static boolean DEBUG = false;

	public static java.util.concurrent.Semaphore DBSem = new java.util.concurrent.Semaphore(
			1);

	private static org.mapdb.DB SE_DB = null;
	

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		// assert(args.length == 2);
		java.util.Map<String, String> arg_map = new java.util.HashMap<String, String>();
		if ((args.length % 2) != 0) {
			System.out.println("invalid args");
			System.exit(-3);
		}
		for (int i = 0; i < args.length; i += 2) {
			arg_map.put(args[i], args[i + 1]);
		}
		System.out.println("search engine init with args:");
		for (java.util.Map.Entry<String, String> e : arg_map.entrySet()) {
			System.out.printf("%s : %s\n", e.getKey(), e.getValue());
		}
		{
			String If_Debug = null;
			If_Debug = arg_map.get("-DEBUG");
			if (If_Debug != null) {
				Init.DEBUG = Boolean.parseBoolean(If_Debug);
			}
			if (Init.DEBUG) {
				System.out.println("debugging");
			}
		}
		String DbFileName = arg_map.get("-dbfile");
		if (DbFileName != null) {
			java.io.File dbfile = new java.io.File(DbFileName);
			SE_DB = org.mapdb.DBMaker.newFileDB(dbfile).make();
		} else {
			System.err.println("Db file not found!");
			System.exit(-3);
		}
		boolean is_init = false;
		{
			String init_status = arg_map.get("-Init");
			if (init_status != null) {
				is_init = Boolean.parseBoolean(init_status);
			}
		}
		if (is_init) {
			PageDB.InitOriginal(SE_DB);
			InvertedIdx.InitOriginal(SE_DB);
		} else {
			PageDB.Init(SE_DB);
			InvertedIdx.Init(SE_DB);
		}
		IndexingProc.Start();
		{
			int i = 0;
			while (true) {
				i++;
				Init.DBSem.acquire();
				int PageDBExistSize = PageDB.GetExistPageSize();
				Init.DBSem.release();
				System.out.printf("page size at %d sec : %d\n", i,
						PageDBExistSize);
				if (PageDBExistSize > 10) {
					System.out.println("trying to stop");
					IndexingProc.Stop();
					break;
				}
				Thread.sleep(1000);
			}
		}
		System.out.println("Indexing stopped");
		PageDB.StartPageRankWorker();
		System.out.println("Page Rank Worker Started");
		//Thread.sleep(30000);
		java.util.Scanner input_scan = new java.util.Scanner(System.in);
		while(true)
		{
			System.out.println("query input:");
			String query = input_scan.nextLine();
			if(query.equals("exit"))
			{
				break;
			}
			Query.PresentQueryResult(Query.query(query));
		}
		PageDB.StopPageRankWorker();
		System.out.println("Page Rank Worker Stopped");
		SE_DB.commit();
		SE_DB.close();
		return;
	}

}
