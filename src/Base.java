

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;

import java.util.*;
import java.io.IOException;
import java.io.Serializable;

public class Base<A>
{
	private RecordManager recman;
	private RecordManager page;
	private RecordManager word;
	private RecordManager pageId;
	private RecordManager Hword;
	private HTree hashtable;
	private HTree hashword;
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
	    
	    recid = pageId.getNamedObject(objectname);
		
	    if (recid != 0)
		    pageid = HTree.load(pageId, recid);
	    else
	    {
		    pageid = HTree.createInstance(pageId);
		    pageId.setNamedObject( "pi", pageid.getRecid() );
	    }
	    
        recid = Hword.getNamedObject(objectname);
		
	    if (recid != 0)
		    hashword = HTree.load(Hword, recid);
	    else
	    {
		    hashword = HTree.createInstance(Hword);
		    Hword.setNamedObject( "hw", hashword.getRecid() );
	    }
	}
	
	
	public void finalize() throws IOException
	{
		recman.commit();
		page.commit();
		word.commit();
		pageId.commit();
		Hword.commit();
		recman.close();	
		page.close();	
		word.close();
		pageId.close();
		Hword.close();
	} 

	public void addEntry_url(String url) throws IOException
	{	
		String id = (String) pageid.get(url);
		
		if(id == null)
			id = Integer.toString(Max_UId++);	

		hashtable.put(id, url);	
		pageid.put(url, id);
	}
	
	public void addEntry_word(String word) throws IOException
	{	
		String id = (String) wordlist.get(word);
		
		if(id == null)
			id = Integer.toString(Max_WId++);	

		hashword.put(id, word);	
		wordlist.put(word, id);
	}
	
	public void addEntry_pageinfo(String id, String p_title, String date, String size, Set<String> word, Set<String> child) throws IOException
	{
		String url = (String) hashtable.get(id);
		
		String new_entry = p_title+"\n"+url+"\n"+date+size+"\n";
		
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
			if(!((String) hashtable.get(id)).contains(u))
			URLs.add(u);
		}
		
	}
	
	public void delEntry_url(String url) throws IOException
	{
		// Delete the word and its list from the hashtable
		String id = (String)pageid.get(url);
		hashtable.remove(id);
		pageid.remove(url);	
		pageinfo.remove(id);
	} 
	
	public void delEntry_word(String word) throws IOException
	{
		// Delete the word and its list from the hashtable
		String id = (String)wordlist.get(word);
		hashword.remove(id);
		wordlist.remove(word);	
	} 
	
	public void printAll() throws IOException
	{
		// Print all the data in the hashtable
		FastIterator iter = pageinfo.keys();
        String key;
		
        while((key=(String)iter.next())!=null)
		{
			System.out.println(pageinfo.get(key));
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
