package fr.feasil.comicDownloader.lite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComicLite implements Comparable<ComicLite>, DownloadableLite {
	
	private final String category;
	private final String titreCategory;
	private final String titreViaTome;
	private final List<TomeLite> tomesLite;
	private long lastAjoutTome = 0;
	
	public ComicLite(String category, String titreCategory, String titreViaTome) {
		this.category = category;
		this.titreCategory = titreCategory;
		this.titreViaTome = titreViaTome;
		
		tomesLite = new ArrayList<TomeLite>();
	}
	
	public String getCategory() {
		return category;
	}
	public String getTitreCategory() {
		return titreCategory;
	}
	public String getTitreViaTome() {
		return titreViaTome;
	}
	
	
	public void addTomeLite(TomeLite tomeLite) {
		tomesLite.add(tomeLite);
		lastAjoutTome = Math.max(lastAjoutTome, tomeLite.getTimestampAjout());
	}
	public List<TomeLite> getTomesLite() {
		return tomesLite;
	}
	
	public boolean isPreviewError() {
		for ( TomeLite t : tomesLite )
			if ( !t.isPreviewError() )
				return false;
		return true;
	}
	
	
	@Override
	public int compareTo(ComicLite c) {
		if ( c == null )
			return 1;
		if ( getCategory() == null && c.getCategory() == null )
			return 0;
		if ( getCategory() == null )
			return -1;
		if ( c.getCategory() == null )
			return 1;
		return getCategory().compareTo(c.getCategory());
	}
	
	
	
	
	public static class ComparatorDate implements Comparator<ComicLite> {
		@Override
		public int compare(ComicLite c1, ComicLite c2) {
			if ( c1 == c2 )
				return 0;
			if ( c1 == null )
				return -1;
			if ( c2 == null )
				return 1;
			if ( c1.lastAjoutTome == c2.lastAjoutTome )
				return c1.compareTo(c2);
			if ( c1.lastAjoutTome > c2.lastAjoutTome )
				return -1;
			else
				return 1;
		}
	}




	@Override
	public String getUrl() {
		if ( getTomesLite().size() > 0 && getTomesLite().get(0) != null )
			return getTomesLite().get(0).getUrl();
		return null;
	}

	@Override
	public boolean isComic() {
		return true;
	}
	
	@Override
	public String getName() {
		return getTitreCategory();
	}
}

