package fr.feasil.comicDownloader.lite;

import java.util.List;

public abstract class ListComicLite {
	
	public abstract String getSite();
	
	public abstract int getNbPagesLues();
	
	public abstract long getTimestampLecture();
	
	public abstract List<ComicLite> getComicsLite();
	
	public abstract boolean updateListComic();
	
	public abstract int getNbPagesSite();
}
