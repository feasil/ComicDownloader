package fr.feasil.comicDownloader.lite;

import java.util.ArrayList;
import java.util.List;

public class ComicLite implements Comparable<ComicLite> {
	
	private final String category;
	private final String titreCategory;
	private final String titreViaTome;
	private final List<TomeLite> tomesLite;
	
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
	}
	public List<TomeLite> getTomesLite() {
		return tomesLite;
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
	
}
