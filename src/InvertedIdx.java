import java.io.IOException;
import java.util.TreeMap;

import jdbm.htree.HTree;

class TermTree extends java.util.TreeMap<Long ,Long> implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}

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
		} catch (IOException e) {
			//XXX: Auto-generated catch block
			e.printStackTrace();
			System.exit(-2);
		}
	}
	
	
	
	public void commit()
	{
		
	}
}
