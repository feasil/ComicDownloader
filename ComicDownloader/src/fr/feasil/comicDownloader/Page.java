package fr.feasil.comicDownloader;

import com.luugiathuy.apps.downloadmanager.Downloader;

public class Page {
	
	private String url;
	private Downloader downloader;
	
	public Page(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	
	public void setDownloader(Downloader downloader) {
		this.downloader = downloader;
	}
	public Downloader getDownloader() {
		return downloader;
	}
}
