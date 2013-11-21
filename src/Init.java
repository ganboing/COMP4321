public class Init {

	public static boolean DEBUG;
	
	public static java.util.concurrent.Semaphore DBSem = new java.util.concurrent.Semaphore(1);
	
	private static org.mapdb.DB SE_DB = null;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		//assert(args.length == 2);
		java.util.Map<String, String> arg_map = new java.util.HashMap<String, String>();
		if((args.length % 2) != 0)
		{
			System.out.println("invalid args");
		}
		for(int i =0;i<args.length;i+=2)
		{
			arg_map.put(args[i], args[i+1]);
		}
		String DbFileName = arg_map.get("-dbfile");
		if(DbFileName != null)
		{
			java.io.File dbfile = new java.io.File(DbFileName);
			SE_DB =  org.mapdb.DBMaker.newFileDB(dbfile).make();
		}
		else
		{
			System.err.println("Db file not found!");
		}
		boolean is_init = false;
		{
			String init_status = arg_map.get("Init");
			if(init_status != null)
			{
				is_init = Boolean.parseBoolean(init_status);
			}
		}
		if(is_init)
		{
			PageDB.InitOriginal(SE_DB);
			InvertedIdx.InitOriginal(SE_DB);
		}
		else
		{
			PageDB.Init(SE_DB);
			InvertedIdx.Init(SE_DB);
		}
		
		recman = jdbm.RecordManagerFactory.createRecordManager(args[0]);
		java.net.URL url_to_start = new java.net.URL(args[1]);
		int page_num_to_fetch = Integer.parseInt(args[2]);
		PageDB.printAll();
		PageDB.commit();
		recman.commit();
		recman.close();
		return;
	}

}
