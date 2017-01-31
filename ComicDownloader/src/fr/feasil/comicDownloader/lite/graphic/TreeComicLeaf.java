package fr.feasil.comicDownloader.lite.graphic;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.TomeLite;

public class TreeComicLeaf extends DefaultMutableTreeNode// implements Transferable
{
	private static final long serialVersionUID = 1L;
	
	private EnumTreeComicLeaf type;
	private ComicLite comic;
	private TomeLite tome;
	
	public TreeComicLeaf(EnumTreeComicLeaf type) 
	{
		this.type = type;
		this.comic = null;
		this.tome = null;
	}
	
	public TreeComicLeaf(EnumTreeComicLeaf type, ComicLite comic) 
	{
		this.type = type;
		this.comic = comic;
		this.tome = null;
	}
	
	public TreeComicLeaf(EnumTreeComicLeaf type, TomeLite tome) 
	{
		this.type = type;
		this.comic = null;
		this.tome = tome;
	}
	
	public EnumTreeComicLeaf getType() 
	{
		return type;
	}
	
	public ComicLite getComic() {
		return comic;
	}
	public TomeLite getTome() {
		return tome;
	}
	
	
	
	
	
	
	
	@Override
	public String toString() {
		return "[" + getType() + ", " + getComic() + ", " +getTome() + "]";
	}
	
	
	@Override
	public boolean equals(Object arg) {
		if ( !(arg instanceof TreeComicLeaf) )
			return false;
		TreeComicLeaf o = (TreeComicLeaf) arg;
		if ( (type == null && o.getType() != null) || (type != null && o.getType() == null) )
			return false;
		if ( (tome == null && o.getTome() != null) || (tome != null && o.getTome() == null) )
			return false;
		
		return ((type==null && o.getType() == null) || (type.equals(o.getType())))
			&& ((tome==null && o.getTome() == null) || (tome.equals(o.getTome())));
	}
	
	
	
	
//	@Override
//	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException 
//	{
//		if(!isDataFlavorSupported(flavor))
//			throw new UnsupportedFlavorException(flavor);
//		if ( TreeScriptTransferHandler.getFlavors()[0].equals(flavor) )
//			return this;
//		
//		if ( TreeScriptTransferHandler.getFlavors()[1].equals(flavor) )
//			return getFichier();
//		
//		List<String> r = new ArrayList<String>();
//		if ( getFichier() != null )
//			r.add(getFichier().getPath());
//		return r;
//	}
//	@Override
//	public DataFlavor[] getTransferDataFlavors() {
//		return TreeScriptTransferHandler.getFlavors();
//	}
//	@Override
//	public boolean isDataFlavorSupported(DataFlavor flavor) {
//		return TreeScriptTransferHandler.getFlavors()[0].equals(flavor) || 
//				TreeScriptTransferHandler.getFlavors()[1].equals(flavor) ||
//				TreeScriptTransferHandler.getFlavors()[2].equals(flavor);
//	}
//	
	
}
