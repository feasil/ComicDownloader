package fr.feasil.comicDownloader.webComic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.luugiathuy.apps.downloadmanager.DownloadManager;

import fr.feasil.comicDownloader.Page;
import fr.feasil.comicDownloader.Tome;

public class Manganel extends WebComic {
	
	private final static int NB_MAX_TOMES = 10;
	
	private final String url;
	
	protected Manganel(String url) 
	{
		this.url = url;
	}
	
	
	@Override
	public List<Tome> getTomes() throws IOException
	{
		List<Tome> tomes = null;
		
		if ( url != null )
		{
			tomes = new ArrayList<Tome>();
			
			//Pour manganel.com
			Document doc = getDocument(url);
			
			
			String urlStart = url.substring(0, url.length()-1);
			
			//select id = selectbox
			Element select = doc.select("select#c_chapter").first();
			Elements options = select.children();
			int totalNbTomes = options.size();
			//
			
			setChanged();
			Object[] o = {"total", totalNbTomes};
			notifyObservers(o);
			
			boolean afterGoodOne = false;
			Tome tome; Page page;
			int i = 1;
			int nbTome = 1;
			String urlTome;
			
			for ( int nb = 1 ; nb <= totalNbTomes ; nb++ )
			{
				urlTome = urlStart + nb;
				
				setChanged();
				o = new Object[]{"avancement", nbTome-1};
				notifyObservers(o);
				
				
				//Si on ne veut pas prendre les tomes antérieurs 
				if ( url.equals(DownloadManager.verifyURL(urlTome).toString()) )
					afterGoodOne = true;
				if ( afterGoodOne )
				{
					if ( i > NB_MAX_TOMES )
						break;
				
					doc = getDocument(urlTome);
					
					//Pour récupérer le titre
					String title = doc.title().replace(" Online For Free - Manganel.com", "").trim();
					//System.out.println(title);
					//
					tome = new Tome(nbTome, title);
					
					//Pour récupérer les images
					Elements jpgs = doc.select("img[src$=.jpg]");
					for ( Element jpg : jpgs )
					{
						//if ( !jpg.attr("src").contains("donate-button") )//Permet de ne pas embarquer le bouton de donation
						//{
						//	if ( jpg.attr("src").startsWith("//") )//Permet de contourner un bug sur le site
						//		page = new Page("http:" + jpg.attr("src"));
						//	else
								page = new Page(jpg.attr("src"));
							
							tome.addPage(page);
						//}
					}
					//
					
					tomes.add(tome);
					i++;
				}
				nbTome++;
			}
			
		}
		
		return tomes;
	}
	
	
	
	@Override
	public Tome getTome() throws IOException
	{
		/*Tome tome = null;
		
		if ( url != null )
		{
			//Pour viewcomic.com
			Document doc = getDocument(url);
			//
			
			//Pour récupérer le numéro du tome dans le comic
			Element select = doc.select("select#selectbox").first();
			Elements options = select.children();
			int numeroTome = options.size();
			for ( Element o : options )
			{
				if ( url.equals(DownloadManager.verifyURL(o.attr("value")).toString()) )
					break;
				numeroTome--;
			}
			//--------------
			
			Page page;
			
			//Pour récupérer le titre
			String title = doc.title().replace("…", "").replace(" | View Comic", "").trim();
			while ( title.startsWith(".") )
				title = title.substring(1).trim();
			while ( title.endsWith(".") )
				title = title.substring(0, title.length()-1).trim();
			//System.out.println(title);
			//
			tome = new Tome(numeroTome, title);
			
			//Pour récupérer les images
			Elements jpgs = doc.select("img[src$=.jpg]");
			for ( Element jpg : jpgs )
			{
				if ( !jpg.attr("src").contains("donate-button") )//Permet de ne pas embarquer le bouton de donation
				{
					if ( jpg.attr("src").startsWith("//") )//Permet de contourner un bug sur le site
						page = new Page("http:" + jpg.attr("src"));
					else
						page = new Page(jpg.attr("src"));
					
					tome.addPage(page);
				}
			}
			//
			
		}
		
		return tome;*/
		return null;
	}
}