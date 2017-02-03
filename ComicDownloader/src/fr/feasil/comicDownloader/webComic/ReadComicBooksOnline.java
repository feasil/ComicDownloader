package fr.feasil.comicDownloader.webComic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.feasil.comicDownloader.Page;
import fr.feasil.comicDownloader.Tome;

public class ReadComicBooksOnline extends WebComic {
	
	private final static String URL = "http://readcomicbooksonline.com/reader/";
	private String url;
	
	protected ReadComicBooksOnline(String url) 
	{
		this.url = url;
	}
	
	
	@Override
	public List<Tome> getTomes() throws IOException
	{
		List<Tome> tomes = new ArrayList<Tome>();
		
		//Pour viewcomic.com
		Document doc = getDocument(url);
		
		String urlComic = "";
		List<String> urlTomes = new ArrayList<String>();
		
		//Select : manga
		Element select = doc.select("select[name=manga]").first();
		Elements options = select.children();
		for ( Element o : options )
			if ( o.hasAttr("selected") )
			{
				urlComic = o.attr("value");
				break;
			}
		
		//select : chapitre
		select = doc.select("select[name=chapter]").first();
		options = select.children();
		for ( Element o : options )
			urlTomes.add(o.attr("value"));
		//
		
		
		setChanged();
		Object[] o = {"total", 100};
		notifyObservers(o);
		
		
		//boolean afterGoodOne = false;
		Tome tome; Page page;
		//int i = 1;
		int nbTome = 1;
		List<String> urlPages = new ArrayList<String>();
		for ( String urlTome : urlTomes )
		{
			
			/*if ( url.equals(DownloadManager.verifyURL(urlTome).toString()) )
				afterGoodOne = true;
			if ( afterGoodOne )*/
			{
				/*if ( i > NB_MAX_TOMES )
					break;//TO DO a dégager
					*/
				doc = getDocument(URL + urlComic + "/" + urlTome + "/" + 1);
				
				select = doc.select("select[name=page]").first();
				options = select.children();
				urlPages.clear();
				for ( Element opt : options )
					urlPages.add(URL + urlComic + "/" + urlTome + "/" + opt.attr("value"));
				
				//Pour récupérer le titre
				String title = urlTome.replace("_", " ").trim();
				while ( title.startsWith(".") )
					title = title.substring(1).trim();
				while ( title.endsWith(".") )
					title = title.substring(0, title.length()-1).trim();
				//System.out.println(title);
				//
				//System.out.println("Tome : " + title);
				tome = new Tome(nbTome, title);
				
				for ( int n = 0 ; n < urlPages.size() ; n++ )
				{
					setChanged();
					o = new Object[]{"avancement", (int)((100f/urlTomes.size())*(nbTome-1) + ((100f/urlTomes.size())/urlPages.size())*n)};
					notifyObservers(o);
					
					
					if ( n >= 1 )//break;
						doc = getDocument(urlPages.get(n));
					//System.out.println((100*(n+1))/urlPages.size() + "%");
					//Pour récupérer les images
					Elements jpgs = doc.select("img[src$=.jpg]");
					for ( Element jpg : jpgs )
					{
						if ( !jpg.attr("src").startsWith("http") )
							page = new Page(URL + jpg.attr("src").replace(" ", "%20"));
						else
							page = new Page(jpg.attr("src").replace(" ", "%20"));
						
						tome.addPage(page);
					}
				}
				//
				
				tomes.add(tome);
				//i++;
			}
			nbTome++;
		}
			
		return tomes;
	}
	
	
}