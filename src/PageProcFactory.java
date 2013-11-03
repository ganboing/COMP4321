
public class PageProcFactory {

	public  static void ProcPage(Long PageID, IntermediatePageDescriptor desc)
	{
		PageDB.UpdateLink(PageID, desc.links, 0.5);
		WebPageDescriptor old_desc = PageDB.GetDesc(PageID);
		if(old_desc != null)
		{
			
		}
	}
}
