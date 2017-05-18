package fr.feasil.comicDownloader.lite.graphic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultTreeModel;

import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.TomeLite;
import fr.feasil.comicDownloader.webComic.ListComicLite;

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
	
	
	public void filtrer(String contient)
	{
		contient = contient.toLowerCase().trim();
		while ( contient.contains("**") )
			contient = contient.replace("**", "*");
		if ( contient.startsWith("*") )
			contient = contient.substring(1);
		if ( contient.endsWith("*") )
			contient = contient.substring(0, contient.length()-1);
		contient = contient.replace("$", "\\$");
		
		Pattern regex = Pattern.compile("[^*]+|(\\*)$");
		Matcher m = regex.matcher(contient);
		StringBuffer b= new StringBuffer("^.*");
		while (m.find()) {
		    if(m.group(1) != null) m.appendReplacement(b, ".*");
		    else m.appendReplacement(b, "\\\\Q" + m.group(0) + "\\\\E");
		}
		m.appendTail(b);
		b.append(".*$");
		String replaced = b.toString();
		
		
		root.removeAllChildren();
		
		TreeComicLeaf leafComic;
		for ( ComicLite c : liste.getComicsLite() )
		{
			if ( c.getName().toLowerCase().matches(replaced) )
			{
				leafComic = new TreeComicLeaf(EnumTreeComicLeaf.COMIC, c);
				
				for ( TomeLite t : c.getTomesLite() )
					leafComic.add(new TreeComicLeaf(EnumTreeComicLeaf.TOME, t));
				
				root.add(leafComic);
			}
		}
	}
	
}

