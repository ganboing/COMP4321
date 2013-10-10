public class GlobalPageTable{

	jdbm.htree.HTree htree;
	
	public GlobalPageTable(jdbm.RecordManager recman,long rec_id)
	{
		try {
			htree = jdbm.htree.HTree.load(recman, rec_id);
		} catch (java.io.IOException e) {
			// XXX Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
}
