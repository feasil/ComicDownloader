package fr.feasil.comicDownloader.lite.graphic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
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
import fr.feasil.comicDownloader.lite.DownloadableLite;
import fr.feasil.comicDownloader.lite.TomeLite;
import fr.feasil.comicDownloader.webComic.ListComicLite;

public class DialogComicList extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private static final DateFormat DF_AFFICHAGE_DATE = new SimpleDateFormat("dd/MM/yyyy");
	
	private JTree tree;
	private TreeComicModel modelTree;
	private JButton btnTelecharger;
	private JButton btnUpdate;
	private JButton btnSort;
	
	private ListComicLite liste;
	
	private boolean isCanceled = true;
	private List<DownloadableLite> downloadablesSelected;
	private boolean sortByDate = true;
	
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
				{
					//actionTelecharger();
					actionPreview();
				}
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				
				if ( tree.getSelectionPaths() != null )
				{
					EnumTreeComicLeaf last = null;
					boolean hasBoth = false;
					for ( TreePath p : tree.getSelectionPaths() )
					{
						if ( last == null )
							last = ((TreeComicLeaf)p.getLastPathComponent()).getType();
						else if ( last != ((TreeComicLeaf)p.getLastPathComponent()).getType() )
						{
							//last = null;
							hasBoth = true;
							break;
						}
					}
					
					if ( hasBoth )
					{
						btnTelecharger.setEnabled(true);
						btnTelecharger.setText("Télécharger les éléments");
					}
					else if ( last == EnumTreeComicLeaf.COMIC ) {
						btnTelecharger.setEnabled(true);
						if ( tree.getSelectionPaths().length == 1 )
							btnTelecharger.setText("Télécharger le comic");
						else
							btnTelecharger.setText("Télécharger les comics");
					}
					else if ( last == EnumTreeComicLeaf.TOME ) {
						btnTelecharger.setEnabled(true);
						if ( tree.getSelectionPaths().length == 1 )
							btnTelecharger.setText("Télécharger le tome");
						else
							btnTelecharger.setText("Télécharger les tomes");
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
						actionPreview();
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
		
		btnSort = new JButton("Trier par Date d'ajout");
		btnSort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				actionSort();
			}
		});
	}
	
	private void addComponents() 
	{
		setLayout(new BorderLayout());
		
		this.add(btnSort, BorderLayout.NORTH);
		
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JPanel panBtn = new JPanel(new BorderLayout());
		panBtn.add(btnTelecharger, BorderLayout.CENTER);
		panBtn.add(btnUpdate, BorderLayout.EAST);
		
		this.add(panBtn, BorderLayout.SOUTH);
	}
	
	
	
	private void actionTelecharger()
	{
//		EnumTreeComicLeaf type = null;
//		for ( TreePath p : tree.getSelectionPaths() )
//		{
//			if ( type == null )
//				type = ((TreeComicLeaf)p.getLastPathComponent()).getType();
//			else if ( type != ((TreeComicLeaf)p.getLastPathComponent()).getType() )
//			{
//				type = null;
//				break;
//			}
//		}
//		
//		if ( type == EnumTreeComicLeaf.COMIC )
//		{
//			downloadablesSelected = new ArrayList<DownloadableLite>();
//			switch (type) {
//			case COMIC:
//				for ( TreePath p : tree.getSelectionPaths() )
//					downloadablesSelected.add(((TreeComicLeaf)p.getLastPathComponent()).getComic());
//				break;
//			default:
//				break;
//			}
//			
//			isCanceled = false;
//			dispose();
//		}
		
		downloadablesSelected = new ArrayList<DownloadableLite>();
		List<TomeLite> listeTmpTomes = new ArrayList<TomeLite>();
		ComicLite tmpComic;
		TomeLite tmpTome;
		
		
		for ( TreePath p : tree.getSelectionPaths() )
		{//On ajoute d'abord TOUS les comics
			if ( ((TreeComicLeaf)p.getLastPathComponent()).getType() == EnumTreeComicLeaf.COMIC )
			{
				tmpComic = ((TreeComicLeaf)p.getLastPathComponent()).getComic();
				downloadablesSelected.add(tmpComic);
				
				//On ajoute les tomes pris en charge dans une liste temporaire
				for ( TomeLite t : ((TreeComicLeaf)p.getLastPathComponent()).getComic().getTomesLite() )
					listeTmpTomes.add(t);
			}
		}
		
		for ( TreePath p : tree.getSelectionPaths() )
		{//On ajoute ensuite les tomes en s'assurant qu'ils ne sont pas déjà dans la liste (via les comics)
			if ( ((TreeComicLeaf)p.getLastPathComponent()).getType() == EnumTreeComicLeaf.TOME) 
			{
				tmpTome = ((TreeComicLeaf)p.getLastPathComponent()).getTome();
				//on n'ajoute le tome QUE s'il N'EST PAS déjà dans la liste (via les comics)
				if ( !listeTmpTomes.contains(tmpTome) )
					downloadablesSelected.add(tmpTome);
			}
		}
	
		
		isCanceled = false;
		dispose();
		
		
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
	
	private void actionPreview() {
		if ( tree.getSelectionPaths() != null
				&& tree.getSelectionPaths().length == 1 )
		{
			TomeLite tomeTmp; 
			if ( ((TreeComicLeaf)tree.getSelectionPaths()[0].getLastPathComponent()).getType() == EnumTreeComicLeaf.TOME )
				tomeTmp = ((TreeComicLeaf)tree.getSelectionPaths()[0].getLastPathComponent()).getTome();
			else if ( ((TreeComicLeaf)tree.getSelectionPaths()[0].getLastPathComponent()).getType() == EnumTreeComicLeaf.COMIC )
			{
				if ( ((TreeComicLeaf)tree.getSelectionPaths()[0].getLastPathComponent()).getComic().getTomesLite().size() > 0 )
					tomeTmp = ((TreeComicLeaf)tree.getSelectionPaths()[0].getLastPathComponent()).getComic().getTomesLite().get(0);
				else
					return;
			}
			else
				return;
			
			JDialog dialog = new DialogPreview(tomeTmp);
			
			dialog.pack();
			dialog.setModal(true);
			dialog.setLocationRelativeTo(DialogComicList.this);
			dialog.setVisible(true);
		}
	}
	
	
	
	private void actionSort() 
	{
		if ( sortByDate )
		{
			liste.sortByDate();
			modelTree.setListe(liste);
			modelTree.reload();
			btnSort.setText("Trier par Nom");
		}
		else
		{
			liste.sortByName();
			modelTree.setListe(liste);
			modelTree.reload();
			btnSort.setText("Trier par Date d'ajout");
		}
		sortByDate = !sortByDate;
	}
	
	
	private void updateTitle() {
		setTitle(liste.getSite() 
				+ " - " + liste.getComicsLite().size() + " comics le " 
				+ DF_AFFICHAGE_DATE.format(liste.getTimestampLecture()));
	}
	
	
	public boolean isCanceled() {
		return isCanceled;
	}
	
	public List<DownloadableLite> getDownloadablesLite() {
		if ( isCanceled )
			return null;
		return downloadablesSelected;
	}

}
