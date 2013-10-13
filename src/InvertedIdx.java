class Post implements java.io.Serializable {
	long docId;
	long termFreq;
}

class SyncTermObj {
	public TermTree treePtr;
	private int status;
	private int writeCnt;
	private int readCnt;
	private java.util.concurrent.Semaphore semRead;
	private java.util.concurrent.Semaphore semMut;

	private static final int STATUS_READ = 2;
	private static final int STATUS_WRITE = 1;
	private static final int STATUS_IDLE = 0;

	private SyncTermObj(TermTree _treePtr, int _status, int _write_cnt,
			int _read_cnt) {
		treePtr = _treePtr;
		status = _status;
		writeCnt = _write_cnt;
		readCnt = _read_cnt;
		semRead = new java.util.concurrent.Semaphore(0);
		semMut = new java.util.concurrent.Semaphore(0);
	}

	static public SyncTermObj createSyncTermObjR(TermTree _treePtr) {
		SyncTermObj ret = new SyncTermObj(_treePtr, STATUS_READ, 0, 1);
		return ret;
	}

	static public SyncTermObj createSyncTermObjRW(TermTree _treePtr) {
		SyncTermObj ret = new SyncTermObj(_treePtr, STATUS_WRITE, 1, 0);
		return ret;
	}

	public boolean CommitRead() {
		assert (status == STATUS_READ);
		readCnt--;
		if (readCnt == 0) {
			if (writeCnt == 0) {
				status = STATUS_IDLE;
			} else {
				status = STATUS_WRITE;
			}
			semMut.release();
		}
		return (status == STATUS_IDLE);
	}

	public boolean CommitModify() {
		assert (status == STATUS_WRITE);
		writeCnt--;
		if (readCnt == 0) {
			if (writeCnt == 0) {
				status = STATUS_IDLE;
			}
			semMut.release();
		} else {
			status = STATUS_READ;
			semRead.release(readCnt);
		}
		return (status == STATUS_IDLE);
	}

	public java.util.concurrent.Semaphore TryModify() {
		switch (status) {
		case STATUS_IDLE:
			assert (readCnt == 0);
			assert (writeCnt == 0);
			assert (semMut.availablePermits() == 1);
			semMut.acquireUninterruptibly();
			writeCnt++;
			status = STATUS_WRITE;
			return null;
		case STATUS_READ:
		case STATUS_WRITE:
			writeCnt++;
			return semMut;
		default:
			assert (false);
			return null;
		}
	}

	public java.util.concurrent.Semaphore TryRead() {
		switch (status) {
		case STATUS_IDLE:
			assert (readCnt == 0);
			assert (writeCnt == 0);
			assert (semMut.availablePermits() == 1);
			semMut.acquireUninterruptibly();
			readCnt++;
			status = STATUS_READ;
			return null;
		case STATUS_READ:
			readCnt++;
			return null;
		case STATUS_WRITE:
			readCnt++;
			return semRead;
		default:
			assert (false);
			return null;
		}
	}
}

class PtrCache<K, V> extends java.util.LinkedHashMap<K, V> {
	/**
	 * 
	 */
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
			dbid = recman.getNamedObject("InvertedIdx.db");
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
					ret = st.treePtr;
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
					if (syncMap.size() > syncMap_Thres) {
						syncMap.remove(pair.getKey());
					}
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
				ret = st.treePtr;
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
				if (syncMap.size() > syncMap_Thres) {
					syncMap.remove(term);
					try {
						htree.put(term, st.treePtr);
					} catch (java.io.IOException e) {
						e.printStackTrace();
						System.exit(-2); // XXX: Should be nicer
					}
				}
			}
		}
	}
}