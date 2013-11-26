package comp4321_proj;

final class WordIt<K> extends TagIt<Integer, K> {

	java.util.Map.Entry<org.mapdb.Fun.Tuple2<Integer, Integer>, K> nxt = null;

	java.util.Iterator<java.util.Map.Entry<org.mapdb.Fun.Tuple2<Integer, Integer>, K>> it = null;

	public WordIt(
			int slot,
			java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, K> map) {
		super(slot);
		it = map.entrySet().iterator();
	}

	@Override
	public Integer GetTag() {
		return nxt.getKey().b;
	}

	@Override
	public boolean NextAndUpdate() {
		if (it.hasNext()) {
			nxt = it.next();
			// it_cnt++;
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
	// private org.mapdb.Fun.Tuple2<Integer, java.util.List<KeyWordDescriptor>>
	// nxt_worddesc_list = null;
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
			it_list.add(new WordIt<KeyWordDescriptor>(i++, InvertedIdx
					.GetTermDesc(word)));
		}
		phase_word_pool = new TagItPool<Integer, KeyWordDescriptor>(it_list);
	}

	public PhaseIt(int slot) {
		super(slot);
		phase_word_pool = new TagItPool<Integer, KeyWordDescriptor>();
	}

	public void PutWordIt(WordIt<KeyWordDescriptor> wordit) {
		phase_word_pool.AddIt(wordit);
	}

	@Override
	public Integer GetTag() {
		return pageid;
	}

	@Override
	public boolean NextAndUpdate() {
		while (true) {
			org.mapdb.Fun.Tuple2<Integer, java.util.List<KeyWordDescriptor>> nxt_desc_list = phase_word_pool
					.GetNxtWholeVect();
			if (nxt_desc_list == null) {
				pageid = null;
				keyword_cnt = null;
				return false;
			}
			pageid = nxt_desc_list.a;
			keyword_cnt = KeyWordDescriptor.ProcPhase(nxt_desc_list.b);
			if (keyword_cnt != null) {
				// it_cnt++;
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

	// static org.mapdb.LongConcurrentLRUMap<Long> WordCnter = null;

	public static java.util.List<String> GetAllWord() {
		java.util.List<String> ret = new java.util.LinkedList<String>();
		for (String e : WordIDByStr.keySet()) {
			ret.add(e);
		}
		return ret;
	}

	public static void InitOriginal(org.mapdb.DB SE_DB) {
		if (Init.DEBUG) {
			System.out.println("Inv Idx Orig Init");
		}
		WordDfByID = SE_DB.createTreeMap("INV_IDX_WordDfByID").make();
		WordDescByWordDocID = SE_DB
				.createTreeMap("INV_IDX_WordDescByWordDocID")
				.keySerializer(org.mapdb.BTreeKeySerializer.TUPLE2).make();
		WordTfByWordDocID = SE_DB.createTreeMap("INV_IDX_WordTfByWordDocID")
				.keySerializer(org.mapdb.BTreeKeySerializer.TUPLE2).make();
		WordIDByStr = SE_DB.createTreeMap("INV_IDX_WordIDByStr")
				.keepCounter(true).make();
		WordStrByID = SE_DB.createTreeMap("INV_IDX_WordStrByID")
				.keepCounter(true).make();
	}

	public static void Init(org.mapdb.DB SE_DB) {
		if (Init.DEBUG) {
			System.out.println("Inv Idx Init");
		}
		WordDfByID = SE_DB.getTreeMap("INV_IDX_WordDfByID");
		WordDescByWordDocID = SE_DB.getTreeMap("INV_IDX_WordDescByWordDocID");
		WordTfByWordDocID = SE_DB.getTreeMap("INV_IDX_WordTfByWordDocID");
		WordIDByStr = SE_DB.getTreeMap("INV_IDX_WordIDByStr");
		WordStrByID = SE_DB.getTreeMap("INV_IDX_WordStrByID");
	}

	public static int GetDBSize() {
		return WordStrByID.size();
	}

	public static Integer GetDfByID(Integer id) {
		return WordDfByID.get(id);
	}

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

	public static Integer InsertWordDoc(Integer page_id, String word,
			KeyWordDescriptor word_desc) {
		// System.out.printf("adding word %s in doc %s of freq %d\n", word,
		// PageDB.GetPageUrl(page_id), word_desc.Cnt());
		// assert (page_id != null);
		Integer keyword_id = CreateWord(word);
		WordDescByWordDocID.put(org.mapdb.Fun.t2(keyword_id, page_id),
				word_desc);
		WordTfByWordDocID.put(org.mapdb.Fun.t2(keyword_id, page_id),
				word_desc.GetCntObj());
		Integer keyword_Df = WordDfByID.get(keyword_id);
		if (keyword_Df == null) {
			keyword_Df = 1;
		} else {
			keyword_Df++;
		}
		WordDfByID.put(keyword_id, keyword_Df);
		return keyword_id;
	}

	public static void PrintPostList(
			Integer word_id,
			java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor.KeyWordCnt> post_map) {
		System.out.printf("post list of %s :", FindWordByID(word_id));
		for (java.util.Map.Entry<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor.KeyWordCnt> ent : post_map
				.entrySet()) {
			System.out.printf("(%d, [%d, %d]), ", ent.getKey().b,
					ent.getValue().title_occur, ent.getValue().body_occur);
		}
		System.out.printf("\n");
	}

	public static java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor.KeyWordCnt> GetTermFreq(
			Integer keyword_id) {
		java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor.KeyWordCnt> ret = WordTfByWordDocID
				.subMap(org.mapdb.Fun.t2(keyword_id, 0),
						true,
						org.mapdb.Fun.t2(keyword_id,
								Integer.valueOf(Integer.MAX_VALUE)), false);
		//PrintPostList(keyword_id, ret);
		return ret;
	}

	public static java.util.concurrent.ConcurrentNavigableMap<org.mapdb.Fun.Tuple2<Integer, Integer>, KeyWordDescriptor> GetTermDesc(
			Integer keyword_id) {
		return WordDescByWordDocID.subMap(org.mapdb.Fun.t2(keyword_id, 0),
				true, org.mapdb.Fun.t2(keyword_id,
						Integer.valueOf(Integer.MAX_VALUE)), false);
	}

	// Core algo

	public static java.util.SortedSet<org.mapdb.Fun.Tuple2<Double, Integer>> Query(
			java.util.Map<String, Integer> keywords_weight,
			java.util.Map<String, Integer> keyphases_weight) {
		int i = 0;
		double query_vect_len = 0;
		java.util.List<Integer> query_weight_array = new java.util.ArrayList<Integer>();
		java.util.List<Double> idf_array = new java.util.ArrayList<Double>();
		TagItPool<Integer, KeyWordDescriptor.KeyWordCnt> WrdPhCntPool = new TagItPool<Integer, KeyWordDescriptor.KeyWordCnt>();
		for (java.util.Map.Entry<String, Integer> keyword_weight : keywords_weight
				.entrySet()) {
			String stemed = StopStem
					.process_input_word(keyword_weight.getKey());
			Integer keyword_id = null;
			if (stemed != null) {
				keyword_id = FindIDByWord(stemed);
			}
			if (keyword_id != null) {
				WordIt<KeyWordDescriptor.KeyWordCnt> it = new WordIt<KeyWordDescriptor.KeyWordCnt>(
						i, GetTermFreq(keyword_id));
				WrdPhCntPool.AddIt(it);
				idf_array.add(Math.log(((double) GetDBSize())
						/ GetDfByID(keyword_id)));
				Integer weight = keyword_weight.getValue();
				query_weight_array.add(weight);
				query_vect_len += weight * weight;
				i++;
			}
		}
		for (java.util.Map.Entry<String, Integer> keyphase_weight : keyphases_weight
				.entrySet()) {
			System.out.printf("searching phase: %s\n", keyphase_weight.getKey());
			java.util.regex.Matcher matcher = StringProc
					.GetWordMatcher(keyphase_weight.getKey());
			PhaseIt phit = null;
			int j = 0;
			int phase_df = 0;
			while (matcher.find()) {
				String nxt_word = StopStem.process_input_word(matcher.group());
				Integer word_id = null;
				if (nxt_word != null) {
					word_id = FindIDByWord(nxt_word);
				}
				if (word_id != null) {
					//GetTermFreq(word_id);
					if (phit == null) {
						phit = new PhaseIt(i);
					}
					phit.PutWordIt(new WordIt<KeyWordDescriptor>(j,
							GetTermDesc(word_id)));
					phase_df += GetDfByID(word_id);
					j++;
				}
			}
			if (phit != null) {
				WrdPhCntPool.AddIt(phit);
				idf_array.add(Math.log(((double) GetDBSize()) / phase_df * j));
				Integer weight = keyphase_weight.getValue();
				query_weight_array.add(weight);
				query_vect_len += weight * weight;
				i++;
			}
		}
		query_vect_len = Math.sqrt(query_vect_len);
		org.mapdb.Fun.Tuple2<Integer, java.util.List<org.mapdb.Fun.Tuple2<Integer, KeyWordDescriptor.KeyWordCnt>>> vect_it = null;
		java.util.SortedSet<org.mapdb.Fun.Tuple2<Double, Integer>> rank = new java.util.TreeSet<org.mapdb.Fun.Tuple2<Double, Integer>>();
		while ((vect_it = WrdPhCntPool.GetNxtVect()) != null) {
			double cos_score = 0;
			double doc_vect_len = 0;
			int title_cnt = 0;
			for (org.mapdb.Fun.Tuple2<Integer, KeyWordDescriptor.KeyWordCnt> word_f : vect_it.b) {
				double word_tf_idf = idf_array.get(word_f.a)
						* (word_f.b.body_occur + word_f.b.title_occur * 255);
				title_cnt += word_f.b.title_occur;
				doc_vect_len += word_tf_idf * word_tf_idf;
				cos_score += word_tf_idf * query_weight_array.get(word_f.a);
			}
			doc_vect_len = Math.sqrt(doc_vect_len);
			cos_score /= (doc_vect_len * query_vect_len);
			cos_score += title_cnt*255;
			rank.add(org.mapdb.Fun.t2(
					cos_score * PageDB.GetPageRank(vect_it.a), vect_it.a));
		}
		return rank;
	}
}