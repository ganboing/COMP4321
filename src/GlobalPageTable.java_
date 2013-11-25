public class GlobalPageTable{

	jdbm.btree.BTree pageId_btree;
	jdbm.htree.HTree pageURL_htree;
	
	public GlobalPageTable(jdbm.RecordManager recman,long rec_id)
	{
		try {
			pageId_btree = jdbm.btree.BTree.load(recman, rec_id);
		} catch (java.io.IOException e) {
			// XXX Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
}
