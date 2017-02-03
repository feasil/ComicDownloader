package fr.feasil.comicDownloader.lite.graphic;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;


public class TreeComicCellRenderer extends DefaultTreeCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	private static final DateFormat DF_AFFICHAGE_DATE = new SimpleDateFormat("dd/MM/yyyy");
	private static final ImageIcon ICON_CLOSED = new ImageIcon(TreeComicCellRenderer.class.getResource("/fr/feasil/images/book_closed_16_16.png"));
	private static final ImageIcon ICON_OPEN = new ImageIcon(TreeComicCellRenderer.class.getResource("/fr/feasil/images/book_open_16_16.png"));
	
	
	private TreeComicLeaf comicLeaf;
	
	
	public TreeComicCellRenderer() 
	{
		
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) 
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		if ( value instanceof TreeComicLeaf )
		{
			comicLeaf = (TreeComicLeaf) value;
			
			setIcon(null);
			
			
			switch ( comicLeaf.getType() )
			{
			case ROOT:
				setText(null);
				break;
			case COMIC:
				if ( expanded )
					setIcon(ICON_OPEN);
				else
					setIcon(ICON_CLOSED);
				try {
					setText(comicLeaf.getComic().getTitreCategory() + " (" + comicLeaf.getComic().getTomesLite().size() + ")");
				}catch (Exception e) {
					setText("ERROR - ILLISIBLE");
				}
				break;
			case TOME:
				try {
					setText(comicLeaf.getTome().getTitre() + " - " + DF_AFFICHAGE_DATE.format(comicLeaf.getTome().getTimestampAjout()));
				}catch (Exception e) {
					setText("ERROR - ILLISIBLE");
				}
				break;
				
			default :
				setText("");
			}
			
			
		}
		
		return this;
	}
	
}
