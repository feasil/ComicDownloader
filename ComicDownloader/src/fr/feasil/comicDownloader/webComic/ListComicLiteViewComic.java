package fr.feasil.comicDownloader.webComic;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.TomeLite;

public class ListComicLiteViewComic extends ListComicLite {
	
	private final static DateFormat DF_DATE_ENGLISH = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
	private final static String DB_URL_START = "jdbc:sqlite:";
	
	
	private String dbFile;
	private String site;
	private int nbPagesLues;
	private long timestampLecture;
	private List<ComicLite> comicsLite;
	
	private Connection conn = null;
	
	public ListComicLiteViewComic(String dbFile) throws ListComicException
	{
		this.dbFile = dbFile;
		
		try { //initie la connexion
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DB_URL_START + dbFile);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ListComicException("Sqlite library not found !");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ListComicException("Unable to connect to database " + dbFile);
		}
		
		createDBIfNotExists();
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
		comicsLite = new ArrayList<ComicLite>();
		
		try
		{
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			
			ResultSet rs = statement.executeQuery(SELECT_LAST_SCAN);
			if ( rs.next() )
			{
				this.site = rs.getString(SCAN_SITE);
				this.nbPagesLues = rs.getInt(SCAN_NB_PAGE);
				this.timestampLecture = rs.getLong(SCAN_DATE_SCAN);
			}
			else
			{
				this.site = WebComic.URL_VIEWCOMIC;
				this.nbPagesLues = 0;
				this.timestampLecture = 0;
			}
			
			
			
			
			String category, titreCategory, titreComicViaTome, titreTome;
			long timestamp;
			ComicLite tmpComic;
			TomeLite tmpTome;
			
			rs = statement.executeQuery(SELECT_ALL_TOMES);
			while ( rs.next() )
			{
				category = rs.getString(TOME_CATEGORIE);
				titreCategory = transformCategory(category);
				
				titreTome = rs.getString(TOME_TITRE);
				
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
				
				timestamp = rs.getLong(TOME_DATE_SITE);
				
				tmpTome = new TomeLite(rs.getString(TOME_TITRE), titreTome, rs.getString(TOME_URL_PAGE), rs.getString(TOME_URL_PREVIEW), timestamp);
				tmpComic.addTomeLite(tmpTome);
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
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
		
		try
		{
			PreparedStatement stmt = conn.prepareStatement(INSERT_SCAN, Statement.RETURN_GENERATED_KEYS);
			stmt.setQueryTimeout(30);  // set timeout to 30 sec.
			
			stmt.setString(1, this.site);
			stmt.setInt(2, nbPagesSite);
			stmt.setLong(3, new Date().getTime());
			stmt.executeUpdate();
			
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			int idScan = rs.getInt(1);
			
			stmt = conn.prepareStatement(INSERT_TOME);
			
			PreparedStatement countStmt = conn.prepareStatement(COUNT_TOME);
			
			
			
			if ( getNbPagesLues() <= nbPagesSite )
			{
				Document doc;
				String category, titre, urlPage, urlPreview;
				long date;
//				StringBuilder sb;
//				List<String> newComicsLite = new ArrayList<String>();
				
				
				//Pour ne pas tomber sur une 404 en allant une page trop loin
				int nbPagesALire = Math.min((nbPagesSite - getNbPagesLues())+1, nbPagesSite);
				//-----------
				setChanged();
				Object[] o = {"total", nbPagesALire};
				notifyObservers(o);
				
				setChanged();
				 o = new Object[]{"prefixe", "Page "};
				notifyObservers(o);
				
				// On scanne les nouvelles pages avec une de plus (au cas où des éléments y auraient été ajoutés)   
				for ( int numeroPage = 1 ; numeroPage <= nbPagesALire ; numeroPage++ )
				{
					setChanged();
					o = new Object[]{"avancement", numeroPage};
					notifyObservers(o);
					//System.out.println(numeroPage);
					try{
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
//								sb = new StringBuilder();
//								sb.append(category);
//								sb.append(';');
//								sb.append(titre);
//								sb.append(';');
//								sb.append(urlPage);
//								sb.append(';');
//								sb.append(urlPreview);
//								sb.append(';');
//								sb.append(Long.toString(date));
//								
//								newComicsLite.add(sb.toString());
								
								countStmt.setString(1, category);
								countStmt.setString(2, titre);
								countStmt.setString(3, urlPage);
								countStmt.setString(4, urlPreview);
								
								rs = countStmt.executeQuery();
								if ( !rs.next() || rs.getInt(1) == 0 )
								{
									stmt.setString(1, category);
									stmt.setString(2, titre);
									stmt.setString(3, urlPage);
									stmt.setString(4, urlPreview);
									stmt.setLong(5, date);
									stmt.setInt(6, idScan);
									
									stmt.executeUpdate();
								}
							}
							else
							{
								System.err.println("C'est null !! : " + div + "   " + titre + "   " + urlPage);
								return false;
							}
						}
					} catch (IOException e)
					{
						System.err.println("Page KO : " + numeroPage + "  Exception : " + e.getMessage());
					}
				}
				
				setChanged();
				o = new Object[]{"infinite"};
				notifyObservers(o);
				
				
				//Mise à jour des éléments
				readFile();
				//------------------------
				
				return true;
			
			}
		
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	
	
	
	private void createDBIfNotExists() 
	{
		File db = new File(dbFile);
//		if ( !(db.exists()) )
//		{
			if ( !db.getParentFile().exists() )
				db.getParentFile().mkdirs();
			
			try
			{
				Statement statement = conn.createStatement();
				statement.setQueryTimeout(30);  // set timeout to 30 sec.
		
				//statement.executeUpdate("drop table if exists " + TABLE_SCAN);
				//statement.executeUpdate("drop table if exists " + TABLE_TOME);
				statement.executeUpdate("create table if not exists " + TABLE_SCAN + " ("
						+ SCAN_ID + " integer primary key autoincrement, "
						+ SCAN_SITE + " string, "
						+ SCAN_NB_PAGE + " integer, "
						+ SCAN_DATE_SCAN + " long)");
				statement.executeUpdate("create table if not exists " + TABLE_TOME + " ("
						+ TOME_ID + " integer primary key autoincrement, "
						+ TOME_CATEGORIE + " string, "
						+ TOME_TITRE + " string, "
						+ TOME_URL_PAGE + " string, "
						+ TOME_URL_PREVIEW + " string, "
						+ TOME_DATE_SITE + " long, "
						+ TOME_ID_SCAN_AJOUT + " integer)");
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
//			finally {
//				try {
//					if( conn != null )
//						conn.close();
//				}
//				catch(SQLException e) {
//					System.err.println(e);
//				}
//			}
//		}
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
	
	
	
	
	private static final String TABLE_SCAN = "SCAN";
	private static final String SCAN_ID = "ID";
	private static final String SCAN_SITE = "SITE";
	private static final String SCAN_NB_PAGE = "NBPAGE";
	private static final String SCAN_DATE_SCAN = "DATESCAN";
	
	private static final String TABLE_TOME = "TOME";
	private static final String TOME_ID = "ID";
	private static final String TOME_CATEGORIE = "CATEGORIE";
	private static final String TOME_TITRE = "TITRE";
	private static final String TOME_URL_PAGE = "URLPAGE";
	private static final String TOME_URL_PREVIEW = "URLPREVIEW";
	private static final String TOME_DATE_SITE = "DATESITE";
	private static final String TOME_ID_SCAN_AJOUT = "IDSCANAJOUT";
	
	
	private final static String SELECT_LAST_SCAN = "select * " + "from " + TABLE_SCAN + " where " + SCAN_ID + " = (select max(" + SCAN_ID + ") from " + TABLE_SCAN + ");";
	private final static String SELECT_ALL_TOMES = "select * " + "from " + TABLE_TOME + " order by " + TOME_CATEGORIE + ", " + TOME_TITRE + ";";
	private final static String INSERT_SCAN = "insert into " + TABLE_SCAN
											+ " (" + SCAN_SITE + ", " + SCAN_NB_PAGE + ", " + SCAN_DATE_SCAN + ")"
											+ " values(?, ?, ?);";
	
	private final static String INSERT_TOME = "insert into " + TABLE_TOME
											+ " (" + TOME_CATEGORIE + ", " + TOME_TITRE + ", " + TOME_URL_PAGE + ", " 
											+ TOME_URL_PREVIEW + ", " + TOME_DATE_SITE + ", " + TOME_ID_SCAN_AJOUT + ")"
											+ " values(?, ?, ?, ?, ?, ?);";
	
	private final static String COUNT_TOME = "select count(*) from TOME	where " 
											+ TOME_CATEGORIE + " = ? and " 
											+ TOME_TITRE + " = ? and " 
											+ TOME_URL_PAGE + " = ? and " 
											+ TOME_URL_PREVIEW + " = ? ;";


}
