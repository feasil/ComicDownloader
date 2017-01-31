package fr.feasil.comicDownloader.lite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListComicLite {
	
	private String site;
	private int nbPagesLues;
	private long timestampLecture;
	private List<ComicLite> comicsLite;
	
	public ListComicLite(File fichier) {
		readFile(fichier);
	}
	
	public String getSite() {
		return site;
	}
	
	public int getNbPagesLues() {
		return nbPagesLues;
	}
	public long getTimestampLecture() {
		return timestampLecture;
	}
	
	
	public List<ComicLite> getComicsLite() {
		return comicsLite;
	}
	
	
	
	
	private void readFile(File fichier)
	{
		BufferedReader bReader = null;
		String[] contenu;
		String line;
		String category, titreCategory, titreComicViaTome, titreTome;
		long timestamp;
		ComicLite tmp;
		
		try {
			bReader = new BufferedReader(new FileReader(fichier));
//			bWriter = new BufferedWriter(new FileWriter("out/" + file));
			
			comicsLite = new ArrayList<ComicLite>();
			
			if ( (line = bReader.readLine()) != null )
			{//Lecture de la première ligne qui contient des infos générales
				//site;nbPagesLues;timestampLecture
				
				contenu = line.split(";");
				this.site = contenu[0];
				try {
					this.nbPagesLues = Integer.parseInt(contenu[1]);
				} catch (NumberFormatException e) {this.nbPagesLues = -1;}
				try {
					this.timestampLecture = Long.parseLong(contenu[2]);
				} catch (NumberFormatException e) {this.timestampLecture = -1;}
			}
			
			for (  ; (line = bReader.readLine()) != null ;  )
			{//Lectures des lignes suivantes qui contiennent les tomes
				//category;titreBrut;url;urlPreview;timestampAjout
				
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
				
				tmp = getComicLite(category);
				if ( tmp == null )
				{
					tmp = new ComicLite(category, titreCategory, titreComicViaTome);
					comicsLite.add(tmp);
				}
				
				try {
					timestamp = Long.parseLong(contenu[4]);
				} catch(NumberFormatException e) { timestamp = -1; }
				
				tmp.addTomeLite(new TomeLite(titreTome, contenu[2], contenu[3], timestamp));
				
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finally {
			if ( bReader != null ) try{bReader.close();}catch(IOException e){}
		}
		
		
		Collections.sort(comicsLite);
		for ( ComicLite c : comicsLite )
			Collections.sort(c.getTomesLite());
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
}
