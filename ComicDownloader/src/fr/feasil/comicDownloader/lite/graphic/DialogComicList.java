package fr.feasil.comicDownloader.lite.graphic;

import java.awt.AlphaComposite;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.feasil.comicDownloader.graphic.WaintingForDownload;
import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.ListComicLite;
import fr.feasil.comicDownloader.lite.TomeLite;
import fr.feasil.comicDownloader.webComic.WebComic;

public class DialogComicList extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private static final DateFormat DF_AFFICHAGE_DATE = new SimpleDateFormat("dd/MM/yyyy");
	
	private JTree tree;
	private TreeComicModel modelTree;
	private JButton btnTelecharger;
	private JButton btnUpdate;
	
	private ListComicLite liste;
	
	private boolean isCanceled = true;
	private List<ComicLite> comicsSelected;
	
	public DialogComicList(ListComicLite liste)
	{
		super();
		
		this.liste = liste;
		
		updateTitle();
		
		
		initFrame();
		initComponents();
		addComponents();
		
		setMinimumSize(new Dimension(350, 150));
		setSize(new Dimension(400, 600));
		
		//setResizable(false);
	}
	
	private void initFrame()
	{
		getContentPane().setLayout(new GridBagLayout());
		//setSize(700, 500);
		//setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);
	}
	
	private void initComponents()
	{
		
		tree = new JTree();
		tree.putClientProperty("JTree.lineStyle", "None");
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		//tree.addTreeSelectionListener(new TreeScriptSelectionListener(this));
		
		TreeComicCellRenderer renderer = new TreeComicCellRenderer();
		tree.setCellRenderer(renderer);
		
		modelTree = new TreeComicModel(liste);
		tree.setModel(modelTree);
		
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if ( evt.getKeyCode() == KeyEvent.VK_ENTER )
					actionTelecharger();
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				
				if ( tree.getSelectionPaths() != null )
				{
					EnumTreeComicLeaf last = null;
					for ( TreePath p : tree.getSelectionPaths() )
					{
						if ( last == null )
							last = ((TreeComicLeaf)p.getLastPathComponent()).getType();
						else if ( last != ((TreeComicLeaf)p.getLastPathComponent()).getType() )
						{
							last = null;
							break;
						}
					}
					
					if ( last == EnumTreeComicLeaf.COMIC ) {
						btnTelecharger.setEnabled(true);
						if ( tree.getSelectionPaths().length == 1 )
							btnTelecharger.setText("Télécharger le comic");
						else
							btnTelecharger.setText("Télécharger les comics");
					}
					else
					{
						btnTelecharger.setEnabled(false);
						btnTelecharger.setText("Télécharger");
					}
				}
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ( e.getClickCount() == 2 )
				{
					if ( tree.getSelectionPaths() != null
							&& tree.getSelectionPaths().length == 1 
							&& ((TreeComicLeaf)tree.getSelectionPaths()[0].getLastPathComponent()).getType() == EnumTreeComicLeaf.TOME )
					{
						final TomeLite tome = ((TreeComicLeaf)tree.getSelectionPaths()[0].getLastPathComponent()).getTome();
						
						final SwingWorker<BufferedImage, Void> mySwingWorker = new SwingWorker<BufferedImage, Void>(){
				    		@Override
				    		protected BufferedImage doInBackground() {
				    			BufferedImage img;
								try {
									img = ImageIO.read(WebComic.getImage(tome.getUrlPreview()));
								} catch (IOException e) {
									e.printStackTrace();
									return null;
								}
				    			return img;
				    		}
				    	};
				    	mySwingWorker.execute();
				    	
				    	new WaintingForDownload(DialogComicList.this, mySwingWorker, liste);
				    	
				    	BufferedImage img;
						try {
							img = mySwingWorker.get();
						} catch (Exception e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(DialogComicList.this, "Erreur lors du chargement de la preview...", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						
						//Resize de l'image
						int thumbnailWidth = 500;
						int widthToScale, heightToScale;
						if (img.getWidth() > img.getHeight()) {
						    heightToScale = (int)(1.1 * thumbnailWidth);
						    widthToScale = (int)((heightToScale * 1.0) / img.getHeight() * img.getWidth());
						} else {
						    widthToScale = (int)(1.1 * thumbnailWidth);
						    heightToScale = (int)((widthToScale * 1.0) / img.getWidth() * img.getHeight());
						}
						BufferedImage resizedImage = new BufferedImage(widthToScale, 
						heightToScale, img.getType());
						Graphics2D g = resizedImage.createGraphics();
						g.setComposite(AlphaComposite.Src);
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
						g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g.drawImage(img, 0, 0, widthToScale, heightToScale, null);
						g.dispose();
						//------
						
						
						final JDialog dialog = new JDialog();     
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setTitle(tome.getTitre() + " preview");
						
						
						JLabel lbl = new JLabel(new ImageIcon(resizedImage));
						lbl.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								dialog.dispose();
							}
						});
						
						dialog.add(lbl);
						
						dialog.pack();
						dialog.setModal(true);
						dialog.setLocationRelativeTo(DialogComicList.this);
						dialog.setVisible(true);
					}
				}
			}
		});
		
		
		tree.setRootVisible(false);
		
		
		
		
		btnTelecharger = new JButton("Télécharger");
		btnTelecharger.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				actionTelecharger();
			}
		});
		btnTelecharger.setEnabled(false);
		
		
		btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				actionUpdate();
			}
		});
	}
	
	private void addComponents() 
	{
		setLayout(new BorderLayout());
		
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JPanel panBtn = new JPanel(new BorderLayout());
		panBtn.add(btnTelecharger, BorderLayout.CENTER);
		panBtn.add(btnUpdate, BorderLayout.EAST);
		
		this.add(panBtn, BorderLayout.SOUTH);
	}
	
	
	
	private void actionTelecharger()
	{
		EnumTreeComicLeaf type = null;
		for ( TreePath p : tree.getSelectionPaths() )
		{
			if ( type == null )
				type = ((TreeComicLeaf)p.getLastPathComponent()).getType();
			else if ( type != ((TreeComicLeaf)p.getLastPathComponent()).getType() )
			{
				type = null;
				break;
			}
		}
		
		if ( type == EnumTreeComicLeaf.COMIC )
		{
			comicsSelected = new ArrayList<ComicLite>();
			switch (type) {
			case COMIC:
				for ( TreePath p : tree.getSelectionPaths() )
					comicsSelected.add(((TreeComicLeaf)p.getLastPathComponent()).getComic());
				break;
				
			default:
				break;
			}
			
			isCanceled = false;
			dispose();
		}
	}
	
	
	private void actionUpdate() {
		int nbPages = liste.getNbPagesSite();
		if ( liste.getNbPagesLues() == nbPages )
		{
			if ( JOptionPane.showConfirmDialog(this, "Aucune nouveauté ne semble être présente.\nVoulez-vous actualiser quand même ?", "Information", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION )
				return ;
		}
		else if ( liste.getNbPagesLues() < nbPages )
		{
			if ( JOptionPane.showConfirmDialog(this, "Des nouveautés sont présentes sur le site.\nVoulez-vous mettre à jour le catalogue ?", "Information", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION )
				return ;
		}
		else
			return;
		
		
		final SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>(){
    		@Override
    		protected Void doInBackground() {
    			
    			if ( liste.updateListComic() )
    			{
    				modelTree.setListe(liste);
    				modelTree.reload();
    				
    				updateTitle();
    			}
    			else 
    				JOptionPane.showMessageDialog(DialogComicList.this, "Erreur lors de l'update...", "Error", JOptionPane.ERROR_MESSAGE);
    			
				return null;
    		}
    	};
    	mySwingWorker.execute();
    	
    	new WaintingForDownload(DialogComicList.this, mySwingWorker, liste);
		
	}
	
	
	private void updateTitle() {
		setTitle(liste.getSite() 
				+ " - " + liste.getComicsLite().size() + " comics le " 
				+ DF_AFFICHAGE_DATE.format(liste.getTimestampLecture()));
	}
	
	
	public boolean isCanceled() {
		return isCanceled;
	}
	
	public List<ComicLite> getComicsLite() {
		if ( isCanceled )
			return null;
		return comicsSelected;
	}

}
