/*final class SyncTermObj {
 public Object objptr;
 private int status;
 private int writeCnt;
 private int readCnt;
 private java.util.concurrent.Semaphore semRead;
 private java.util.concurrent.Semaphore semMut;

 private static final int STATUS_READ = 2;
 private static final int STATUS_WRITE = 1;
 private static final int STATUS_IDLE = 0;

 private SyncTermObj(Object _objptr, int _status, int _write_cnt,
 int _read_cnt) {
 objptr = _objptr;
 status = _status;
 writeCnt = _write_cnt;
 readCnt = _read_cnt;
 semRead = null;
 semMut = null;
 }

 static public SyncTermObj createSyncTermObjR(Object _objptr) {
 return new SyncTermObj(_objptr, STATUS_READ, 0, 1);
 }

 static public SyncTermObj createSyncTermObjRW(Object _objptr) {
 return new SyncTermObj(_objptr, STATUS_WRITE, 1, 0);
 }

 public boolean CommitRead() {
 assert(status == STATUS_READ);
 assert(readCnt > 0);
 readCnt--;
 if (readCnt == 0) {
 if (writeCnt == 0) {
 if(semMut != null)
 {
 semMut.release();
 }
 status = STATUS_IDLE;
 } else {
 assert(semMut != null);
 semMut.release();
 status = STATUS_WRITE;
 }
 }
 return (status == STATUS_IDLE);
 }

 public boolean CommitModify() {
 assert (status == STATUS_WRITE);
 assert(writeCnt > 0);
 writeCnt--;
 if (readCnt == 0) {
 if (writeCnt == 0) {
 status = STATUS_IDLE;
 }
 else
 {
 assert(semMut != null);
 }
 if(semMut != null)
 {
 semMut.release();
 }
 } else {
 status = STATUS_READ;
 assert(semRead != null);
 semRead.release(readCnt);
 }
 return (status == STATUS_IDLE);
 }

 public java.util.concurrent.Semaphore TryModify() {
 switch (status) {
 case STATUS_IDLE:
 assert (readCnt == 0);
 assert (writeCnt == 0);
 writeCnt++;
 status = STATUS_WRITE;
 return null;
 case STATUS_WRITE:
 case STATUS_READ:
 switch (status) {
 case STATUS_WRITE:
 if (semMut == null) {
 assert (writeCnt == 1);
 semMut = new java.util.concurrent.Semaphore(0);
 }
 break;
 case STATUS_READ:
 if (semMut == null) {
 assert (writeCnt == 0);
 semMut = new java.util.concurrent.Semaphore(0);
 }
 break;
 }
 writeCnt++;
 return semMut;
 default:
 assert (false);
 System.exit(-4);
 return null;
 }
 }

 public java.util.concurrent.Semaphore TryRead() {
 switch (status) {
 case STATUS_IDLE:
 assert (readCnt == 0);
 assert (writeCnt == 0);
 status = STATUS_READ;
 case STATUS_READ:
 readCnt++;
 return null;
 case STATUS_WRITE:
 if(semRead == null)
 {
 assert(readCnt == 0);
 semRead = new java.util.concurrent.Semaphore(0);
 }
 readCnt++;
 return semRead;
 default:
 assert (false);
 System.exit(-4);
 return null;
 }
 }
 }

 class PtrCache<K, V> extends java.util.LinkedHashMap<K, V> {

 private static final long serialVersionUID = 4099067150469224472L;

 final int size;

 PtrCache(int s) {
 size = s;
 }

 protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
 return size() > size;
 }
 }

 public class InvertedIdx {

 private static final int syncMap_Thres = 1 * 1024 * 1024;

 // public static class

 private jdbm.htree.HTree htree;

 private Object Mut_Access_TermTree;

 java.util.HashMap<String, SyncTermObj> syncMap;

 // PtrCache<String, TermTree> ptrCache_Usr;
 // PtrCache<String, TermTree> ptrCache_Idxer;

 // java.util.HashMap<String, Long> WritePtrCache;

 public InvertedIdx(jdbm.RecordManager recman) {
 long dbid;
 try {
 dbid = recman.getNamedObject("InvertedIdx");
 htree = jdbm.htree.HTree.load(recman, dbid);
 } catch (java.io.IOException e) {
 e.printStackTrace();
 System.exit(-2); // XXX: Should be nicer
 }
 Mut_Access_TermTree = new Object();
 }

 public void GetTermTreeGroupR(java.util.Set<String> term_set,
 java.util.Map<String, TermTree> result_map) {

 for (String term : term_set) {

 TermTree ret = null;
 java.util.concurrent.Semaphore semR = null;

 synchronized (Mut_Access_TermTree) {

 SyncTermObj st = syncMap.get(term);
 if (st == null) {
 try {
 ret = (TermTree) htree.get(term);
 } catch (java.io.IOException e) {
 e.printStackTrace();
 System.exit(-2); // XXX: Should be nicer
 }
 if (ret != null) {
 st = SyncTermObj.createSyncTermObjR(ret);
 syncMap.put(term, st);
 }
 } else {
 ret = (TermTree)st.objptr;
 assert(ret != null);
 semR = st.TryRead();
 }
 }

 if (semR != null) {
 semR.acquireUninterruptibly();
 }
 if (ret != null) {
 result_map.put(term, ret);
 }
 }
 }

 public void CommitTermTreeGroupR(java.util.Map<String, TermTree> result_map) {
 for (java.util.Map.Entry<String, TermTree> pair : result_map.entrySet()) {
 synchronized (Mut_Access_TermTree) {
 SyncTermObj st = syncMap.get(pair.getKey());
 assert (st != null);
 if (st.CommitRead()) {
 syncMap.remove(pair.getKey());
 }
 }
 }
 }

 public TermTree GetTermTreeRW(String term) {

 TermTree ret = null;
 java.util.concurrent.Semaphore semW = null;

 synchronized (Mut_Access_TermTree) {
 SyncTermObj st = syncMap.get(term);
 if (st == null) {
 try {
 ret = (TermTree) htree.get(term);
 } catch (java.io.IOException e) {
 e.printStackTrace();
 System.exit(-2); // XXX: Should be nicer
 }
 if (ret == null) {
 ret = new TermTree();
 }
 st = SyncTermObj.createSyncTermObjRW(ret);
 syncMap.put(term, st);
 } else {
 ret = (TermTree)st.objptr;
 assert(ret != null);
 semW = st.TryModify();
 }
 }

 if(semW != null)
 {
 semW.acquireUninterruptibly();
 }

 return ret;
 }

 public void CommitTermTreeRW(String term) {
 synchronized (Mut_Access_TermTree) {
 SyncTermObj st = syncMap.get(term);
 assert (st != null);
 if (st.CommitModify()) {
 syncMap.remove(term);
 try {
 htree.put(term, st.objptr);
 } catch (java.io.IOException e) {
 e.printStackTrace();
 System.exit(-2); // XXX: Should be nicer
 }

 }
 }
 }
 }
 */

final class WordIt<K> extends TagIt<Integer, K> {

	java.util.Map.Entry<org.mapdb.Fun.Tuple2<Integer, Integer>, K> nxt = null;

	java.util.Iterator<java.util.Map.Entry<org.mapdb.Fun.Tuple2<Integer, Integer>, K>> it = null;

	public WordIt(
			int slot,
			Integer keyword_id,
			java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, K> map) {
		super(slot);
		it = map.subMap(
				org.mapdb.Fun.t2(keyword_id, 0),
				true,
				org.mapdb.Fun.t2(keyword_id, Integer.valueOf(Integer.MAX_VALUE)),
				false).entrySet().iterator();
	}

	@Override
	public Integer GetTag() {
		return nxt.getKey().b;
	}

	@Override
	public boolean NextAndUpdate() {
		if(it.hasNext())
		{
			nxt = it.next();
			return true;
		}
		return false;
	}

	@Override
	public K GetVal() {
		return nxt.getValue();
	}
}

final class PhaseIt extends TagIt<Integer, KeyWordDescriptor.KeyWordCnt> {

	private TagItPool<Integer, KeyWordDescriptor> phase_word_pool = null;
	//private org.mapdb.Fun.Tuple2<Integer, java.util.List<KeyWordDescriptor>> nxt_worddesc_list = null;
	private Integer pageid = null;
	private KeyWordDescriptor.KeyWordCnt keyword_cnt = null;
	
	public PhaseIt(
			int slot,
			java.util.List<Integer> phase,
			java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor> map) {
		super(slot);
		java.util.List<TagIt<Integer, KeyWordDescriptor>> it_list = new java.util.LinkedList<TagIt<Integer, KeyWordDescriptor>>();
		int i = 0;
		for (Integer word : phase) {
			it_list.add(new WordIt<KeyWordDescriptor>(i++, word, map));
		}
		phase_word_pool = new TagItPool<Integer, KeyWordDescriptor>(it_list);
	}

	@Override
	public Integer GetTag() {
		return pageid;
	}

	@Override
	public boolean NextAndUpdate() {
		while(true)
		{
			org.mapdb.Fun.Tuple2<Integer, java.util.List<KeyWordDescriptor>> nxt_desc_list = phase_word_pool.GetNxtWholeVect();
			if(nxt_desc_list == null)
			{
				pageid = null;
				keyword_cnt = null;
				return false;
			}
			pageid = nxt_desc_list.a;
			keyword_cnt = KeyWordDescriptor.ProcPhase(nxt_desc_list.b);
			if(keyword_cnt != null)
			{
				return true;
			}
		}
	}

	@Override
	public KeyWordDescriptor.KeyWordCnt GetVal() {
		return keyword_cnt;
	}
}

public class InvertedIdx {
	static java.util.concurrent.ConcurrentNavigableMap<Integer, Integer> WordDfByID;
	static java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor> WordDescByWordDocID;
	static java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor.KeyWordCnt> WordTfByWordDocID;

	static java.util.concurrent.ConcurrentNavigableMap<String, Integer> WordIDByStr;
	static java.util.concurrent.ConcurrentNavigableMap<Integer, String> WordStrByID;
	static org.mapdb.LongConcurrentLRUMap<Long> WordCnter = null;

	public static Integer CreateWord(String word) {
		if (WordIDByStr.size() != WordStrByID.size()) {
			System.exit(-2);
		}
		if (WordIDByStr.containsKey(word)) {
			Integer ret = WordIDByStr.get(word);
			if (ret == null) {
				System.exit(-2);
			}
			return ret;
		}
		Integer assign_id = Integer.valueOf(WordStrByID.size());
		WordStrByID.put(assign_id, word);
		WordIDByStr.put(word, assign_id);
		return assign_id;
	}

	public static String FindWordByID(Integer id) {
		return WordStrByID.get(id);
	}

	public static Integer FindIDByWord(String word) {
		return WordIDByStr.get(word);
	}

	public static void InsertDoc(Integer page_id,
			java.util.Map<String, KeyWordDescriptor> key_word_to_add) {
		// assert (page_id != null);
		if (key_word_to_add != null) {
			for (java.util.Map.Entry<String, KeyWordDescriptor> keyword_desc_pair : key_word_to_add
					.entrySet()) {
				Integer keyword_id = CreateWord(keyword_desc_pair.getKey());
				WordDescByWordDocID.put(org.mapdb.Fun.t2(keyword_id, page_id),
						keyword_desc_pair.getValue());
				Integer keyword_Df = WordDfByID.get(keyword_id);
				if (keyword_Df == null) {
					keyword_Df = Integer.valueOf(keyword_desc_pair.getValue()
							.Cnt());
				} else {
					keyword_Df = keyword_Df
							+ Integer.valueOf(keyword_desc_pair.getValue()
									.Cnt());
				}
				WordDfByID.put(keyword_id, keyword_Df);
			}
		}
	}

	public static java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor.KeyWordCnt> GetTermFreq(
			Integer keyword_id) {
		return WordTfByWordDocID
				.subMap(org.mapdb.Fun.t2(keyword_id, 0),
						true,
						org.mapdb.Fun.t2(keyword_id,
								Integer.valueOf(Integer.MAX_VALUE)), false);
	}

	public static java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor> GetTermDesc(
			Integer keyword_id) {
		return WordDescByWordDocID.subMap(org.mapdb.Fun.t2(keyword_id, 0),
				true, org.mapdb.Fun.t2(keyword_id,
						Integer.valueOf(Integer.MAX_VALUE)), false);
	}

	// public static java.util.

	public static 
	
	public static java.util.List<Integer> Query(java.util.List<String> term, java.util.List<java.util.List<String>> phase)
	{
		java.util.Vector<Integer> a;
		a.add(Integer.valueOf(1));
		a[1]
		java.util.LinkedList<Integer> ret = new java.util.LinkedList<Integer>();
		java.util.PriorityQueue<CompIt<Integer,java.util.Iterator<E>>>
		while(??)
		{
			a.poll();
		}
		//java.util.Vector<String> r = new java.util.Vector<String>(term);
		return ret;
	}
}