package fr.feasil.comicDownloader.lite.graphic;

import javax.swing.tree.DefaultTreeModel;

import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.ListComicLite;
import fr.feasil.comicDownloader.lite.TomeLite;

public class TreeComicModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	private TreeComicLeaf root = new TreeComicLeaf(EnumTreeComicLeaf.ROOT);
	
	private ListComicLite liste;
	
	public TreeComicModel(ListComicLite liste) 
	{
		super(null);
		
		setListe(liste);
		
		setRoot(root);
	}
	
	public void setListe(ListComicLite newListe)
	{
		root.removeAllChildren();
		
		this.liste = newListe;
		
		TreeComicLeaf leafComic;
		for ( ComicLite c : liste.getComicsLite() )
		{
			leafComic = new TreeComicLeaf(EnumTreeComicLeaf.COMIC, c);
			
			for ( TomeLite t : c.getTomesLite() )
				leafComic.add(new TreeComicLeaf(EnumTreeComicLeaf.TOME, t));
			
			root.add(leafComic);
		}
	}
	
	
}

