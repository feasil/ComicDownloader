package fr.feasil.comicDownloader.webComic;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.luugiathuy.apps.downloadmanager.DownloadManager;

import fr.feasil.comicDownloader.Tome;
import fr.feasil.comicDownloader.lite.ListComicLite;

public abstract class WebComic extends Observable {
	
	private final static String URL_VIEWCOMIC = "http://viewcomic.com";
	private final static String URL_VIEWCOMIC2 = "http://view-comic.com";
	private final static String URL_READCOMICBOOKSONLINE = "http://readcomicbooksonline.com";
	
	private final static File FILE_LIST_COMIC_VIEWCOMIC = new File("list/viewComic.csv");
	private final static File FILE_LIST_COMIC_VIEWCOMIC2 = new File("list/viewComic2.csv");
	
	public static WebComic getWebComic(String url)
	{
		if ( url != null )
		{
			if ( url.toLowerCase().startsWith(URL_VIEWCOMIC) )
				return new ViewComic(url);
			else if ( url.toLowerCase().startsWith(URL_VIEWCOMIC2) )
				return new ViewComic2(url);
			else if ( url.toLowerCase().startsWith(URL_READCOMICBOOKSONLINE) )
				return new ReadComicBooksOnline(url);
		}
		return null;
	}
	
	public static ListComicLite getListWebComics(String url)
	{
		if ( url != null )
		{
			if ( url.toLowerCase().startsWith(URL_VIEWCOMIC) )
				return new ListComicLite(FILE_LIST_COMIC_VIEWCOMIC);
			else if ( url.toLowerCase().startsWith(URL_VIEWCOMIC2) )
				return new ListComicLite(FILE_LIST_COMIC_VIEWCOMIC2);
//			On ne gère pas les readcomicbooksonline pour le moment...
//			else if ( url.toLowerCase().startsWith("http://readcomicbooksonline.com/") )
//				return new ReadComicBooksOnline(url);
		}
		return null;
	}
	
	
	
	
	
	public abstract List<Tome> getTomes() throws IOException;
	
	
	
	/**
	 * Pour se connecter à la page
	 * @param url
	 * @return
	 * @throws IOException
	 */
	protected static Document getDocument(String url) throws IOException 
    {
    	//On se connecte à la page
		Connection conn = Jsoup.connect(url);
		
		if ( DownloadManager.getInstance().useProxy() )
		{
//			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url, port));
			conn.proxy(DownloadManager.getInstance().getProxy());
//			Authenticator authenticator = new Authenticator() {
//				public PasswordAuthentication getPasswordAuthentication() {
//					return (new PasswordAuthentication(user, password.toCharArray()));
//				}
//			};
//			Authenticator.setDefault(authenticator);
		}
		
		return conn.timeout(30000).userAgent("Mozilla").get();
	}
}
