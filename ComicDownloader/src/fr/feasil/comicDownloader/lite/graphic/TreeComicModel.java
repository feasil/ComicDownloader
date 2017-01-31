package fr.feasil.comicDownloader.lite.graphic;

import javax.swing.tree.DefaultTreeModel;

import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.ListComicLite;
import fr.feasil.comicDownloader.lite.TomeLite;

public class TreeComicModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	//private static final SimpleDateFormat SDF_DATELOG = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	private TreeComicLeaf root = new TreeComicLeaf(EnumTreeComicLeaf.ROOT);
	
	private ListComicLite liste;
	
	public TreeComicModel(ListComicLite liste) 
	{
		super(null);
		
		this.liste = liste;
		initTree();
		
		setRoot(root);
	}
	
	private void initTree()
	{
		TreeComicLeaf leafComic;
		for ( ComicLite c : liste.getComicsLite() )
		{
			leafComic = new TreeComicLeaf(EnumTreeComicLeaf.COMIC, c);
			
			for ( TomeLite t : c.getTomesLite() )
				leafComic.add(new TreeComicLeaf(EnumTreeComicLeaf.TOME, t));
			
			root.add(leafComic);
		}
	}
	
	
	
	
//	private TreeComicLeaf findNode(TreeComicLeaf search, TomeLite tome)
//	{
//		TreeComicLeaf leaf;
//		for ( int i = 0 ; i < search.getChildCount() ; i++ )
//		{
//			if ( tome.equals(((TreeComicLeaf)search.getChildAt(i)).getTome()) )
//				return (TreeComicLeaf) search.getChildAt(i);
//			if ( search.getChildAt(i).getChildCount() > 0 )
//			{
//				leaf = findNode((TreeComicLeaf) search.getChildAt(i), tome);
//				if ( leaf != null )
//					return leaf;
//			}
//		}
//		return null;
//	}
//	
//	private void deleteNode(TreeComicLeaf node)
//	{
//		TreeComicLeaf parent = (TreeComicLeaf)node.getParent();
//		
//		if(parent == null)
//			return ;
//		
//		int[] childIndex = new int[1];
//		Object[] removedArray = new Object[1];
//		childIndex[0] = parent.getIndex(node);
//		parent.remove(childIndex[0]);
//		removedArray[0] = node;
//		nodesWereRemoved(parent, childIndex, removedArray);
//	}
}

