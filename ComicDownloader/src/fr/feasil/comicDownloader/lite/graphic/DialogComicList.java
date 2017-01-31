package fr.feasil.comicDownloader.lite.graphic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
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
	
	private JTree tree;
	private JButton btnTelecharger;
	
	private ListComicLite liste;
	
	private boolean isCanceled = true;
	private List<ComicLite> comicsSelected;
	
	public DialogComicList(ListComicLite liste)
	{
		super();
		setTitle("Liste des comics - " + liste.getSite());
		
		this.liste = liste;
		
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
		
		TreeComicModel model = new TreeComicModel(liste);
		tree.setModel(model);
		
//		tree.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent evt) {
//				if ( evt.getClickCount() == 2 )
//					actionTelecharger();
//			}
//		});
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
//				else if ( last == EnumTreeComicLeaf.TOME ) {
//					btnTelecharger.setEnabled(true);
//					if ( tree.getSelectionPaths().length == 1 )
//						btnTelecharger.setText("Télécharger le tome");
//					else
//						btnTelecharger.setText("Télécharger les tomes");
//				}
				else
				{
					btnTelecharger.setEnabled(false);
					btnTelecharger.setText("Télécharger");
				}
			}
		});
		
		
		tree.setRootVisible(false);
		
		
		
		
		btnTelecharger = new JButton("Télécharger");
		btnTelecharger.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionTelecharger();
			}
		});
		btnTelecharger.setEnabled(false);
		
	}
	
	private void addComponents() 
	{
		setLayout(new BorderLayout());
		
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
		this.add(btnTelecharger, BorderLayout.SOUTH);
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
//		if ( type != null )
		if ( type == EnumTreeComicLeaf.COMIC )
		{
			comicsSelected = new ArrayList<ComicLite>();
			switch (type) {
			case COMIC:
				for ( TreePath p : tree.getSelectionPaths() )
					comicsSelected.add(((TreeComicLeaf)p.getLastPathComponent()).getComic());
				break;
//			case TOME:
//				for ( TreePath p : tree.getSelectionPaths() )
//					tomesSelected.add(((TreeComicLeaf)p.getLastPathComponent()).getTome());
//				break;
				
			default:
				break;
			}
			
			isCanceled = false;
			dispose();
		}
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
