package fr.feasil.comicDownloader.webComic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Observable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.luugiathuy.apps.downloadmanager.DownloadManager;

import fr.feasil.comicDownloader.Tome;

public abstract class WebComic extends Observable {
	
	protected final static String URL_VIEWCOMIC = "http://viewcomic.com";
	protected final static String URL_VIEWCOMIC2 = "http://view-comic.com";
	protected final static String URL_READCOMICBOOKSONLINE = "http://readcomicbooksonline.com";
	
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
				return new ListComicLiteViewComic(FILE_LIST_COMIC_VIEWCOMIC);
			//TODO
//			else if ( url.toLowerCase().startsWith(URL_VIEWCOMIC2) )
//				return new ListComicLiteViewComic2(FILE_LIST_COMIC_VIEWCOMIC2);
//			On ne gère pas les readcomicbooksonline pour le moment...
//			else if ( url.toLowerCase().startsWith("http://readcomicbooksonline.com/") )
//				return new ReadComicBooksOnline(url);
		}
		return null;
	}
	
	
	
	
	
	public abstract List<Tome> getTomes() throws IOException;
	
	public abstract Tome getTome() throws IOException;
	
	
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
	
	protected static InputStream getImage(String urlstr) throws IOException 
    {
		URL url = new URL(urlstr);
		URLConnection conn = null;
		if ( DownloadManager.getInstance().useProxy() )
			conn = url.openConnection(DownloadManager.getInstance().getProxy());
		else
			conn = url.openConnection();
		
		InputStream in = conn.getInputStream();
		return in;
    }

	
	
}
