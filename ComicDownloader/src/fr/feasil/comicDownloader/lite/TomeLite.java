package fr.feasil.comicDownloader.lite;

import java.util.Comparator;

public class TomeLite implements Comparable<TomeLite> {
	
	private final String titreBrut;
	private final String titre;
	private final String url;
	private final String urlPreview;
	private final long timestampAjout;
	
	public TomeLite(String titreBrut, String titre, String url, String urlPreview, long timestampAjout) {
		this.titreBrut = titreBrut;
		this.titre = titre;
		this.url = url;
		this.urlPreview = urlPreview;
		this.timestampAjout = timestampAjout;
	}
	
	public String getTitreBrut() {
		return titreBrut;
	}
	public String getTitre() {
		return titre;
	}
	public String getUrl() {
		return url;
	}
	public String getUrlPreview() {
		return urlPreview;
	}
	public long getTimestampAjout() {
		return timestampAjout;
	}
	
	
	
	@Override
	public int compareTo(TomeLite t) {
		if ( t == null )
			return 1;
		if ( getTitre() == null && t.getTitre() == null )
			return 0;
		if ( getTitre() == null )
			return -1;
		if ( t.getTitre() == null )
			return 1;
		return getTitre().compareTo(t.getTitre());
	}
	
	
	
	public static class ComparatorDate implements Comparator<TomeLite> {
		@Override
		public int compare(TomeLite t1, TomeLite t2) {
			if ( t1 == t2 )
				return 0;
			if ( t1 == null )
				return -1;
			if ( t2 == null )
				return 1;
			if ( t1.timestampAjout == t2.timestampAjout )
				return t1.compareTo(t2);
			if ( t1.timestampAjout > t2.timestampAjout )
				return -1;
			else
				return 1;
		}
	}
	
}
