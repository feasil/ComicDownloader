package fr.feasil.comicDownloader.webComic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.ListComicLite;
import fr.feasil.comicDownloader.lite.TomeLite;

public class ListComicLiteViewComic extends ListComicLite {
	
	private final static DateFormat DF_DATE_ENGLISH = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
	
	private File fichier;
	private String site;
	private int nbPagesLues;
	private long timestampLecture;
	private List<ComicLite> comicsLite;
	
	public ListComicLiteViewComic(File fichier) {
		this.fichier = fichier;
	}
	
	@Override
	public String getSite() {
		return site;
	}
	
	@Override
	public int getNbPagesLues() {
		return nbPagesLues;
	}
	
	@Override
	public long getTimestampLecture() {
		return timestampLecture;
	}
	
	@Override
	public List<ComicLite> getComicsLite() {
		return comicsLite;
	}
	
	
	@Override
	public void readFile()
	{
		readUpdateFile(null, -1);
	}
	
	
	/**
	 * If listElementUpdate != null, on est en update, sinon c'est du readOnly
	 * @param fichier
	 * @param compare
	 */
	private void readUpdateFile(List<String> listElementUpdate, int nbPageUpdate)
	{
		BufferedReader bReader = null;
		BufferedWriter bWriter = null;
		File tmpFile = null;
		String[] contenu;
		String line = null;
		
		boolean updateMode = (listElementUpdate!=null);
		
		try {
			if ( !fichier.exists() )
				fichier.createNewFile();
			bReader = new BufferedReader(new FileReader(fichier));
			if ( updateMode )
			{
				tmpFile = new File(fichier.getParentFile(), fichier.getName() + "_tmp");
				tmpFile.createNewFile();
				bWriter = new BufferedWriter(new FileWriter(tmpFile));
			}
			
			comicsLite = new ArrayList<ComicLite>();
			
			if ( (line = bReader.readLine()) != null || updateMode )
			{//Lecture de la première ligne qui contient des infos générales
				//site;nbPagesLues;timestampLecture
				
				if ( updateMode )
				{
					this.site = WebComic.URL_VIEWCOMIC;
					this.nbPagesLues = nbPageUpdate;
					this.timestampLecture = new Date().getTime();
					
					bWriter.append(this.site);
					bWriter.append(';');
					bWriter.append(Integer.toString(this.nbPagesLues));
					bWriter.append(';');
					bWriter.append(Long.toString(this.timestampLecture));
					bWriter.append('\n');
					bWriter.flush();
				}
				else
				{
					contenu = line.split(";");
					this.site = contenu[0];
					try {
						this.nbPagesLues = Integer.parseInt(contenu[1]);
					} catch (NumberFormatException e) {this.nbPagesLues = -1;}
					try {
						this.timestampLecture = Long.parseLong(contenu[2]);
					} catch (NumberFormatException e) {this.timestampLecture = -1;}
				}
				
			}
			line = null;
			if ( (updateMode && (line = bReader.readLine()) != null) || updateMode )
			{//Lecture de la deuxième ligne pour la mise à jour
				for ( String lineUpdate : listElementUpdate )
				{
					if ( !lineUpdate.equals(line) )
						addToListComicsLite(lineUpdate, bWriter);
					else 
						break;
				}
				if ( line != null )
					addToListComicsLite(line, bWriter);
			}
			
			
			for (  ; (line = bReader.readLine()) != null ;  )
			{//Lectures des lignes suivantes qui contiennent les tomes
				//category;titreBrut;url;urlPreview;timestampAjout
				
				//Si on est pas en updateMode, bWriter est null
				addToListComicsLite(line, bWriter);
			}
			
			
			if ( updateMode )
			{
				bReader.close();
				bWriter.close();
				
				if ( fichier.exists() )
					fichier.delete();
				tmpFile.renameTo(fichier);
				
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finally {
			if ( bReader != null ) try{bReader.close();}catch(IOException e){}
			if ( updateMode && bWriter != null ) try{bWriter.close();}catch(IOException e){}
		}
		
		
		Collections.sort(comicsLite);
		for ( ComicLite c : comicsLite )
			Collections.sort(c.getTomesLite());
	}
	
	private void addToListComicsLite(String line, BufferedWriter bWriter) throws IOException
	{
		String category, titreCategory, titreComicViaTome, titreTome;
		long timestamp;
		ComicLite tmpComic;
		TomeLite tmpTome;
		String[] contenu;
		
		contenu = line.split(";");
		category = contenu[0];
		titreCategory = transformCategory(category);
		
		
		titreTome = contenu[1];
		
		//
		titreTome = titreTome.replace("…", "").trim();
		while ( titreTome.startsWith(".") )
			titreTome = titreTome.substring(1).trim();
		while ( titreTome.endsWith(".") )
			titreTome = titreTome.substring(0, titreTome.length()-1).trim();
		titreComicViaTome = titreTome;
		
		//On enleve l'annee
		if ( titreComicViaTome.length() > 6 && titreComicViaTome.endsWith(")") && titreComicViaTome.charAt(titreComicViaTome.length()-6) == '(' )
			titreComicViaTome = titreComicViaTome.substring(0, titreComicViaTome.length()-6).trim();
		//On enleve le (of 00)
		if ( titreComicViaTome.length() > 7 && titreComicViaTome.endsWith(")") && titreComicViaTome.substring(titreComicViaTome.length()-7, titreComicViaTome.length()-3).equals("(of ") )
			titreComicViaTome = titreComicViaTome.substring(0, titreComicViaTome.length()-7).trim();
		//On enleve le (of 0)
		if ( titreComicViaTome.length() > 6 && titreComicViaTome.endsWith(")") && titreComicViaTome.substring(titreComicViaTome.length()-6, titreComicViaTome.length()-2).equals("(of ") )
			titreComicViaTome = titreComicViaTome.substring(0, titreComicViaTome.length()-6).trim();
		//On enleve le numero
		while ( titreComicViaTome.endsWith("0") || titreComicViaTome.endsWith("1") || titreComicViaTome.endsWith("2") || titreComicViaTome.endsWith("3") || 
				titreComicViaTome.endsWith("4") || titreComicViaTome.endsWith("5") || titreComicViaTome.endsWith("6") || titreComicViaTome.endsWith("7") || 
				titreComicViaTome.endsWith("8") || titreComicViaTome.endsWith("9") || titreComicViaTome.endsWith(".") )
			titreComicViaTome = titreComicViaTome.substring(0, titreComicViaTome.length()-1);
		titreComicViaTome = titreComicViaTome.trim();
		//
		if ( titreComicViaTome.startsWith("– ") )
			titreComicViaTome = titreComicViaTome.substring(2);
		if ( titreComicViaTome.length() > 4 && !titreComicViaTome.startsWith("’") && titreComicViaTome.charAt(4) == '–')
			titreComicViaTome = titreComicViaTome.substring(5).trim();
		if ( titreComicViaTome.length() > 3 && !titreComicViaTome.startsWith("’") && titreComicViaTome.charAt(3) == '–')
			titreComicViaTome = titreComicViaTome.substring(4).trim();
		//
		
		
		tmpComic = getComicLite(category);
		if ( tmpComic == null )
		{
			tmpComic = new ComicLite(category, titreCategory, titreComicViaTome);
			comicsLite.add(tmpComic);
		}
		
		try {
			timestamp = Long.parseLong(contenu[4]);
		} catch(NumberFormatException e) { timestamp = -1; }
		
		
		tmpTome = new TomeLite(contenu[1], titreTome, contenu[2], contenu[3], timestamp);
		tmpComic.addTomeLite(tmpTome);
		
		
		if ( bWriter != null )
		{
			bWriter.append(tmpComic.getCategory());
			bWriter.append(';');
			bWriter.append(tmpTome.getTitreBrut());
			bWriter.append(';');
			bWriter.append(tmpTome.getUrl());
			bWriter.append(';');
			bWriter.append(tmpTome.getUrlPreview());
			bWriter.append(';');
			bWriter.append(Long.toString(tmpTome.getTimestampAjout()));
			bWriter.append('\n');
			
			bWriter.flush();
		}
	}

	private ComicLite getComicLite(String category)
	{
		for ( ComicLite c : comicsLite )
			if ( c.getCategory().equals(category) )
				return c;
		return null;
	}
	
	private String transformCategory(String category) {
		StringBuilder sb = new StringBuilder();
		boolean isNextUpperCase = true;
		for ( char c : category.toCharArray() )
		{
			if ( c == '-' )
			{
				sb.append(' ');
				isNextUpperCase = true;
			}
			else if ( isNextUpperCase )
			{
				sb.append(Character.toUpperCase(c));
				isNextUpperCase = false;
			}
			else
				sb.append(c);
		}
		return sb.toString();
	}
	
	
	
	
	
	/**
	 * 
	 * @return true si l'update été fait, false si ce n'était pas nécessaire 
	 */
	@Override
	public boolean updateListComic()
	{
		int nbPagesSite = getNbPagesSite();
		if ( getNbPagesLues() != nbPagesSite )
		{
			Document doc;
			String category, titre, urlPage, urlPreview;
			long date;
			StringBuilder sb;
			List<String> newComicsLite = new ArrayList<String>();
			
			try {
				setChanged();
				Object[] o = {"total", (nbPagesSite - getNbPagesLues())+1};
				notifyObservers(o);
				
				// On scanne les nouvelles pages avec une de plus (au cas où des éléments y auraient été ajoutés)   
				for ( int numeroPage = 1 ; numeroPage <= (nbPagesSite - getNbPagesLues())+1 ; numeroPage++ )
				{
					setChanged();
					o = new Object[]{"avancement", numeroPage-1};
					notifyObservers(o);
					
					doc = WebComic.getDocument(WebComic.URL_VIEWCOMIC + "/page/" + Integer.toString(numeroPage));
					
					Element divPostArea = doc.select("div#post-area").get(0);
					for ( Element div : divPostArea.children() )
					{
						titre = null;
						urlPage = null;
						urlPreview = null;
						date = -1;
						
						category = div.attributes().get("class");
						if ( category.indexOf("category-") != -1 )
						{
							category = category.substring(category.indexOf("category-") + 9);
							if ( category.indexOf(" ") != -1 )
								category = category.substring(0, category.indexOf(" "));
						}
						else 
							category = "UNKNOWN";
						
						
						if ( div.select("img").size() == 1 )
							urlPreview = div.select("img").attr("src");
						
						if ( div.select("a.front-link").size()  == 1 )
						{
							titre = div.select("a.front-link").text();
							urlPage = div.select("a.front-link").attr("href");
						}
						if ( div.select("p.pinbin-date").size() == 1 )
						{
							try {
								date = DF_DATE_ENGLISH.parse(div.select("p.pinbin-date").text()).getTime();
							} catch (ParseException e) {
								date = -1;
							}
						}
						
						if ( titre != null && urlPage != null )
						{//category, titre, urlPage, urlPreview, date
							sb = new StringBuilder();
							sb.append(category);
							sb.append(';');
							sb.append(titre);
							sb.append(';');
							sb.append(urlPage);
							sb.append(';');
							sb.append(urlPreview);
							sb.append(';');
							sb.append(Long.toString(date));
							
							newComicsLite.add(sb.toString());
						}
						else
						{
							System.err.println("C'est null !! : " + div + "   " + titre + "   " + urlPage);
							return false;
						}
					}
					
				}
				
				setChanged();
				o = new Object[]{"infinite"};
				notifyObservers(o);
				
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			
			
			readUpdateFile(newComicsLite, nbPagesSite);
			
			return true;
		}
		return false;
	}
	
	@Override
	public int getNbPagesSite()
	{
		Document doc;
		String urlLastPage = null;
		int nbPages = -1;
		
		try {
			doc = WebComic.getDocument(WebComic.URL_VIEWCOMIC);
			
			Element divPosition = doc.select("div.wp-pagenavi").get(0);
			if ( divPosition.select("a.last").size() == 1 )
				try {
					urlLastPage = divPosition.select("a.last").attr("href");
				} catch (NumberFormatException e) {
					urlLastPage = null;
				}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if ( urlLastPage != null && urlLastPage.toLowerCase().indexOf("/page/") != -1 )
		{
			urlLastPage = urlLastPage.substring(urlLastPage.toLowerCase().indexOf("/page/")+6);
			urlLastPage = urlLastPage.replace("/", "");
			try {
				nbPages = Integer.parseInt(urlLastPage);
			} catch (NumberFormatException e) {
				urlLastPage = null;
				nbPages = -1;
			}
		}
		
		if ( urlLastPage != null )
			return nbPages;
		
		return -1;
	}
	
	
	
}
