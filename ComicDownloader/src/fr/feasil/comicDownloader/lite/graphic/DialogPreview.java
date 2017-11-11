package fr.feasil.comicDownloader.lite.graphic;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

import fr.feasil.comicDownloader.Tome;
import fr.feasil.comicDownloader.graphic.WaitingForDownload;
import fr.feasil.comicDownloader.lite.TomeLite;
import fr.feasil.comicDownloader.webComic.ListComicLite;

public class DialogPreview  extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private static final ImageIcon IMAGE_THUMB_WAIT = new ImageIcon(DialogComicList.class.getResource("/fr/feasil/images/thumb_wait_500_750.png"));
	
	private TomeLite tomeLite;
	private int page;
	private Tome tome = null;
	
	private JLabel lblPreview;
	private JSlider sldPage;
	
	public DialogPreview(TomeLite tomeLite) 
	{
		super();
		
		this.tomeLite = tomeLite;
		this.page = -1;
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		
		
		lblPreview = new JLabel(IMAGE_THUMB_WAIT);
		lblPreview.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DialogPreview.this.dispose();
			}
		});
		
		sldPage = new JSlider(JSlider.HORIZONTAL, 0, 20, 0) {
			private static final long serialVersionUID = 1L;
			{
		        MouseListener[] listeners = getMouseListeners();
		        for (MouseListener l : listeners)
		            removeMouseListener(l); // remove UI-installed TrackListener
		        final BasicSliderUI ui = (BasicSliderUI) getUI();
		        BasicSliderUI.TrackListener tl = ui.new TrackListener() {
		            // this is where we jump to absolute value of click
		        	@Override
		        	public void mouseReleased(MouseEvent e) {
		        		super.mouseReleased(e);
		        	}
		            @Override public void mouseClicked(MouseEvent e) {
		                Point p = e.getPoint();
		                int value = ui.valueForXPosition(p.x);
		                
		                setValue(value);
		            }
		            // disable check that will invoke scrollDueToClickInTrack
		            @Override public boolean shouldScroll(int dir) {
		                return false;
		            }
		        };
		        addMouseListener(tl);
		    }
		};;
		sldPage.setValueIsAdjusting(true);
		sldPage.setMajorTickSpacing(5);
		sldPage.setMinorTickSpacing(1);
		sldPage.setPaintTicks(true);
		sldPage.setPaintLabels(true);
		
		sldPage.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent evt) {
				int newPage = sldPage.getValue()-1;
				if ( page != newPage )
				{
					page = newPage;
					preview();
				}
			}
		});
		
		sldPage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if ( evt.getKeyCode() == KeyEvent.VK_ENTER
						|| evt.getKeyCode() == KeyEvent.VK_ESCAPE )
					DialogPreview.this.dispose();
//				else if ( evt.getKeyCode() == KeyEvent.VK_RIGHT )
//				{
//					if ( tome == null || page < (tome.getPages().size()-1) )
//					{
//						page++;
//						preview();
//					}
//				}
//				else if ( evt.getKeyCode() == KeyEvent.VK_LEFT )
//				{
//					if ( page > -1 )
//					{
//						page--;
//						preview();
//					}
//				}
			}
		});
		
		setLayout(new BorderLayout());
		add(lblPreview, BorderLayout.CENTER);
		add(sldPage, BorderLayout.SOUTH);
		
		preview();
		
	}
	
	
	private void preview() {
		new Thread(){
			@Override
			public void run() {
				final SwingWorker<BufferedImage, Void> mySwingWorker = new SwingWorker<BufferedImage, Void>(){
		    		@Override
		    		protected BufferedImage doInBackground() throws IOException {
		    			if ( page == -1 )
		    				return ListComicLite.getPreview(tomeLite.getUrlPreview(), true);
		    			if ( tome == null )
							try {
								tome = tomeLite.getTome();
								sldPage.setMaximum(tome.getPages().size());
							} catch (IOException e1) {
								//Astuce pour proposer le preview même sans disponibilité du site
								try{
									tome = tomeLite.getTomeAlternatif();
									sldPage.setMaximum(tome.getPages().size());
								} catch (Exception e2) {
									e1.printStackTrace();
									return null;
								}
								
							}
		    			return ListComicLite.getPreview(tome.getPages().get(page).getUrl(), false);
		    		}
		    	};
		    	mySwingWorker.execute();
		    	
		    	new WaitingForDownload(DialogPreview.this, mySwingWorker, null);
		    	
		    	BufferedImage img = null;
				try {
					img = mySwingWorker.get();
				} catch (Exception e1) {
					if ( e1.getCause() instanceof FileNotFoundException )
						JOptionPane.showMessageDialog(DialogPreview.this, "Le comic n'existe plus ou n'est pas accessible...", "Warning", JOptionPane.WARNING_MESSAGE);
					else
						JOptionPane.showMessageDialog(DialogPreview.this, "Erreur lors du chargement de la preview...", "Error", JOptionPane.ERROR_MESSAGE);
					
					if ( !tomeLite.isPreviewError() )
						tomeLite.setPreviewError(true);
					
					dispose();
					return;
				}
				
				if ( tomeLite.isPreviewError() )
					tomeLite.setPreviewError(false);
				
				lblPreview.setIcon(new ImageIcon(img));
				pack();
				
				changeTitle();
			}
		}.start();
	}
	
	
	
	private void changeTitle() {
		if ( page == -1 )
			setTitle(tomeLite.getTitre() + " preview");
		else if ( tome == null )
			setTitle(tomeLite.getTitre() + " preview - Page " + (page+1));
		else
			setTitle(tomeLite.getTitre() + " preview - Page " + (page+1) + "/" + tome.getPages().size());
	}
}
