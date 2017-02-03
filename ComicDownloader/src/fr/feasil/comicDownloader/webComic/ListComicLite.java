package fr.feasil.comicDownloader.webComic;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import javax.imageio.ImageIO;

import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.TomeLite;

public abstract class ListComicLite extends Observable {
	
	public abstract String getSite();
	
	public abstract int getNbPagesLues();
	
	public abstract long getTimestampLecture();
	
	public abstract List<ComicLite> getComicsLite();
	
	public abstract void readFile();
	
	public abstract boolean updateListComic();
	
	public abstract int getNbPagesSite();
	
	
	public void sortByName() {
		Collections.sort(getComicsLite());
		for ( ComicLite c : getComicsLite() )
			Collections.sort(c.getTomesLite());
	}
	
	public void sortByDate() {
		Collections.sort(getComicsLite(), new ComicLite.ComparatorDate());
		for ( ComicLite c : getComicsLite() )
			Collections.sort(c.getTomesLite(), new TomeLite.ComparatorDate());
	}
	
	
	
	
	public static BufferedImage getPreview(TomeLite tome, boolean resize) {
		BufferedImage resizedImage = null;
		try {
			BufferedImage img = ImageIO.read(WebComic.getImage(tome.getUrlPreview()));
			
			//Resize de l'image
			int thumbnailWidth = 500;
			int widthToScale, heightToScale;
			if (img.getWidth() > img.getHeight()) {
			    heightToScale = (int)(1 * thumbnailWidth);
			    widthToScale = (int)((heightToScale * 1.0) / img.getHeight() * img.getWidth());
			} else {
			    widthToScale = (int)(1 * thumbnailWidth);
			    heightToScale = (int)((widthToScale * 1.0) / img.getWidth() * img.getHeight());
			}
			
			resizedImage = new BufferedImage(widthToScale, 
			heightToScale, img.getType());
			Graphics2D g = resizedImage.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(img, 0, 0, widthToScale, heightToScale, null);
			g.dispose();
			//------
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return resizedImage;
	}
	
}
