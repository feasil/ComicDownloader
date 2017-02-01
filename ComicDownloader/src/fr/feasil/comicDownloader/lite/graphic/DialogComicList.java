package fr.feasil.comicDownloader.lite.graphic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.ListComicLite;


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
		
		if ( liste.updateListComic() )
		{
			modelTree.setListe(liste);
			modelTree.reload();
			
			updateTitle();
		}
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
