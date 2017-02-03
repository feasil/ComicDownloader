package fr.feasil.comicDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Tome {
	private int numero;
	private String titre;
	private File folder = null;
	
	private List<Page> pages;
	
	public Tome(int numero, String titre) {
		this.numero = numero;
		this.titre = titre;
		
		pages = new ArrayList<Page>();
	}
	
	public int getNumero() {
		return numero;
	}
	public String getTitre() {
		return titre;
	}
	public String getFolderName() {
		return String.format("%03d", numero) + "-" + titre.replace(":", "").trim();
	}
	
	
	public void addPage(Page page) {
		pages.add(page);
	}
	
	public List<Page> getPages() {
		return pages;
	}
	
	public void setFolder(File folder) {
		this.folder = folder;
	}
	public File getFolder() {
		return folder;
	}
}
