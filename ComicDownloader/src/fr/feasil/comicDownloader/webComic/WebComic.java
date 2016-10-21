package fr.feasil.comicDownloader.webComic;

import java.io.IOException;
import java.util.List;
import java.util.Observable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.luugiathuy.apps.downloadmanager.DownloadManager;

import fr.feasil.comicDownloader.Tome;

public abstract class WebComic extends Observable {
	
	public static WebComic getWebComic(String url)
	{
		if ( url != null )
		{
			if ( url.toLowerCase().startsWith("http://viewcomic.com/") )
				return new ViewComic(url);
			else if ( url.toLowerCase().startsWith("http://readcomicbooksonline.com/") )
				return new ReadComicBooksOnline(url);
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
