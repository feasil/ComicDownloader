package fr.feasil.comicDownloader.webComic;

import fr.feasil.comicDownloader.lite.TomeLite;

public interface ListComicLiteListener {
	public void tomePreviewErrorUpdated(TomeLite tomeLite, boolean newValue);
}
