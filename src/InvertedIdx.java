
class Post implements java.io.Serializable
{
	long docId;
	long termFreq;
}

public class InvertedIdx{

	//public static class 
	
	jdbm.htree.HTree htree;
	
	public InvertedIdx(jdbm.RecordManager recman)
	{
		long dbid ;
		try {
			dbid = recman.getNamedObject("InvertedIdx.db");
			htree = jdbm.htree.HTree.load(recman, dbid);
		} catch (java.io.IOException e) {
			//XXX: Auto-generated catch block
			e.printStackTrace();
			System.exit(-2);
		}
	}
	
	public static class TermTree extends java.util.TreeMap<Long ,Long> implements java.io.Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	public TermTree GetTermTree(String term)
	{
		
	}
	
	public void commit()
	{
		
	}
}
