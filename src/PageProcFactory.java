import java.util.LinkedList;


public class PageProcFactory {

	public  static void ProcPage(IntermediatePageDescriptor imm_desc)
	{
		PageDB.UpdateLink(imm_desc.PageID, imm_desc.links, 0.5);
		WebPageDescriptor old_desc = PageDB.GetDesc(imm_desc.PageID);
		if(old_desc != null)
		{
			java.util.List<Long> keyword = old_desc.keywords;
			for(Long key_word_id : keyword)
			{
				InvertedIdx.RemoveWord(imm_desc.PageID, key_word_id);
			}
		}
		java.util.List<Long> key_word_id_list = new java.util.LinkedList<Long>();
		for(java.util.Map.Entry<String, KeyWordDescriptor> i : imm_desc.keyword_map.entrySet())
		{
			Long key_word_id = InvertedIdx.FindIDByWord(i.getKey(), i.getValue().Cnt());
			key_word_id_list.add(key_word_id);
			InvertedIdx.InsertWord(imm_desc.PageID, key_word_id, i.getValue());
		}
		WebPageDescriptor new_desc = new WebPageDescriptor(imm_desc,key_word_id_list);
		PageDB.UpdateDesc(imm_desc.PageID, new_desc);
	}
}
