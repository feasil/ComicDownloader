package fr.feasil.comicDownloader.lite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.feasil.comicDownloader.Tome;
import fr.feasil.comicDownloader.webComic.ListComicLiteListener;
import fr.feasil.comicDownloader.webComic.WebComic;

public class TomeLite implements Comparable<TomeLite>, DownloadableLite {
	
	private final int id;
	private final String titreBrut;
	private final String titre;
	private final String url;
	private final String urlPreview;
	private final long timestampAjout;
	private boolean previewError;
	
	public TomeLite(int id, String titreBrut, String titre, String url, String urlPreview, long timestampAjout, boolean previewError) {
		this.id = id;
		this.titreBrut = titreBrut;
		this.titre = titre;
		this.url = url;
		if ( urlPreview != null && urlPreview.startsWith("//") )
			this.urlPreview = "http:" + urlPreview;
		else
			this.urlPreview = urlPreview;
		this.timestampAjout = timestampAjout;
		this.previewError = previewError;
	}
	
	public int getId() {
		return id;
	}
	public String getTitreBrut() {
		return titreBrut;
	}
	public String getTitre() {
		return titre;
	}
	@Override
	public String getUrl() {
		return url;
	}
	public String getUrlPreview() {
		return urlPreview;
	}
	public long getTimestampAjout() {
		return timestampAjout;
	}
	public boolean isPreviewError() {
		return previewError;
	}
	
	public void setPreviewError(boolean previewError) {
		this.previewError = previewError;
		fireTomePreviewErrorUpdated();
	}
	
	
	public Tome getTome() throws IOException {
		return WebComic.getWebComic(url).getTome();
	}
	//Astuce pour proposer le preview même sans disponibilité du site
	public Tome getTomeAlternatif() throws IOException {
		return WebComic.getWebComicAlternatif(url).getTome();
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
	
	
	@Override
	public boolean isComic() {
		return false;
	}
	
	@Override
	public String getName() {
		return getTitre();
	}
	
	
	
	private final List<ListComicLiteListener> listeners = new ArrayList<ListComicLiteListener>();
	public void addListComicLiteListener(ListComicLiteListener listener) {
		listeners.add(listener);
	}
	public void removeListComicLiteListener(ListComicLiteListener listener) {
		listeners.remove(listener);
	}
	
	private void fireTomePreviewErrorUpdated() {
		for ( ListComicLiteListener l : listeners )
			l.tomePreviewErrorUpdated(this, this.isPreviewError());
	}
}
