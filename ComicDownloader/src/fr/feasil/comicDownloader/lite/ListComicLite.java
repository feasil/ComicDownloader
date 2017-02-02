package fr.feasil.comicDownloader.lite;

import java.util.List;
import java.util.Observable;

public abstract class ListComicLite extends Observable {
	
	public abstract String getSite();
	
	public abstract int getNbPagesLues();
	
	public abstract long getTimestampLecture();
	
	public abstract List<ComicLite> getComicsLite();
	
	public abstract void readFile();
	
	public abstract boolean updateListComic();
	
	public abstract int getNbPagesSite();
}
