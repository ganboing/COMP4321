

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;

import java.util.*;
import java.io.IOException;
import java.io.Serializable;

class Posting implements Serializable
{
	public String doc;
	public int freq;
	Posting(String doc, int freq)
	{
		this.doc = doc;
		this.freq = freq;
	}
}

public class Base<A>
{
	private RecordManager recman;
	private RecordManager page;
	private RecordManager word;
	private RecordManager pageId;
	private HTree hashtable;
	private HTree pageinfo;
	private HTree wordlist;
	private HTree pageid;
	
	private Set<String> URLs = new HashSet<String>();
    private int Max_WId;
    private int Max_UId;


	Base(String recordmanager, String objectname) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);
		page = RecordManagerFactory.createRecordManager(recordmanager);
		word = RecordManagerFactory.createRecordManager(recordmanager);
		long recid = recman.getNamedObject(objectname);
		
		long max_wid_handle = recman.getNamedObject("max_wid");
		
		if (max_wid_handle != 0)
		    Max_WId = ((Integer)recman.fetch(max_wid_handle)).intValue();
		else
			recman.setNamedObject("wid", 1);
		
		long max_uid_handle = recman.getNamedObject("max_uid");
		if (max_uid_handle != 0)
		    Max_UId = ((Integer)recman.fetch(max_uid_handle)).intValue();
		else
			recman.setNamedObject("uid", 1);
		
		if (recid != 0)
			hashtable = HTree.load(recman, recid);
		else
		{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject( "ht", hashtable.getRecid() );
		}
		
		recid = page.getNamedObject(objectname);
		
		if (recid != 0)
			pageinfo = HTree.load(page, recid);
		else
		{
			pageinfo = HTree.createInstance(page);
			page.setNamedObject( "p", pageinfo.getRecid() );
		}
		
		recid = word.getNamedObject(objectname);
	
	    if (recid != 0)
		    wordlist = HTree.load(word, recid);
	    else
	    {
		    wordlist = HTree.createInstance(word);
		    word.setNamedObject( "w", wordlist.getRecid() );
	    }
	    
	    recid = page.getNamedObject(objectname);
		
	    if (recid != 0)
		    pageid = HTree.load(pageId, recid);
	    else
	    {
		    pageid = HTree.createInstance(pageId);
		    pageId.setNamedObject( "pi", pageid.getRecid() );
	    }
	}

	

	public void finalize() throws IOException
	{
		recman.commit();
		page.commit();
		word.commit();
		pageId.commit();
		recman.close();	
		page.close();	
		word.close();
		pageId.close();
	} 

	public void addEntry(String url) throws IOException
	{	
		String id = (String) pageid.get(url);
		
		if(id == null)
			id = Integer.toString(Max_WId++);	

		hashtable.put(id, url);	
		pageid.put(url, id);
	}
	
	public void addEntry(String id, String p_title, String date, String size, Set<String> word, Set<String> child) throws IOException
	{
		String new_entry = p_title+"\n"+date+size+"\n";
		
		for(Iterator<String> it = word.iterator(); it.hasNext();)
			new_entry = new_entry + it.next() + "\n";
		
		for(Iterator<String> it = child.iterator(); it.hasNext();)
			new_entry = new_entry + it.next() + "\n";
		
		if (pageinfo.get(id)!=null && ((String) pageinfo.get(id)).contains(new_entry))
		{
			return;
		}

		pageinfo.put(id, new_entry);
		
		for(Iterator<String> it = child.iterator(); it.hasNext();)
		{
			String u = it.next();
			if(!((String) pageinfo.get(id)).contains(u))
			URLs.add(u);
		}
		
	}
	
	public void addWord(String word, String id) throws IOException
	{
		if (wordlist.get(word)!=null && ((String) wordlist.get(word)).contains(id)) 
		{
			return;
		}

		wordlist.put(word, id);	
	}
	
	public void delEntry(String word) throws IOException
	{
		// Delete the word and its list from the hashtable
		hashtable.remove(word);
	
	} 
	public void printAll() throws IOException
	{
		// Print all the data in the hashtable
		FastIterator iter = hashtable.keys();
		String key;
		while((key=(String)iter.next())!=null)
		{
			System.out.println(key + " = " + hashtable.get(key));
		}
	
	}	
	
	public Set<String> geturl(int n) throws IOException
	{
		Set<String> output = new HashSet<String>();
		Iterator<String> it = URLs.iterator();
		int i = 0;
		
		while(it.hasNext() && i < n)
		{
			String u = it.next();
			output.add(u);
			URLs.remove(u);
			i++;
		}
		
		return output;
	}
 }
