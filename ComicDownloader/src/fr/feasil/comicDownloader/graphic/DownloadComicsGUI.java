package fr.feasil.comicDownloader.graphic;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.luugiathuy.apps.downloadmanager.DownloadManager;
import com.luugiathuy.apps.downloadmanager.DownloadTableModel;
import com.luugiathuy.apps.downloadmanager.Downloader;
import com.luugiathuy.apps.downloadmanager.ProgressRenderer;

import fr.feasil.comicDownloader.Page;
import fr.feasil.comicDownloader.Tome;
import fr.feasil.comicDownloader.lite.ComicLite;
import fr.feasil.comicDownloader.lite.ListComicLite;
import fr.feasil.comicDownloader.lite.graphic.DialogComicList;
import fr.feasil.comicDownloader.webComic.WebComic;

public class DownloadComicsGUI extends javax.swing.JFrame implements Observer{

	private static final long serialVersionUID = 8489399426552541643L;
	
	private final static File FOLDER_OUT = new File(System.getProperty("user.home") + "/Downloads/ComicDownloader/");
	
	private DownloadTableModel mTableModel;
	
	private Downloader mSelectedDownloader;
	
	private boolean mIsClearing;
	
	private List<Tome> tomes = null;
	
	/** Creates new form DownloadManagerGUI */
    public DownloadComicsGUI() {
    	if ( !FOLDER_OUT.exists() )
    		FOLDER_OUT.mkdirs();
    	
    	mTableModel = new DownloadTableModel();
        initComponents();
        initialize();
    }
    
    private void initialize() {
    	// Set up table
    	jtbDownload.getSelectionModel().addListSelectionListener(new
                ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });
    	
    	// Allow only one row at a time to be selected.
    	jtbDownload.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	
    	// Set up ProgressBar as renderer for progress column.
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true); // show progress text
        jtbDownload.setDefaultRenderer(JProgressBar.class, renderer);
        
        // Set table's row height large enough to fit JProgressBar.
        jtbDownload.setRowHeight(
                (int) renderer.getPreferredSize().getHeight());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {

        jtxURL = new javax.swing.JTextField();
        jbnAdd = new javax.swing.JButton();
        jbnListComics = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtbDownload = new javax.swing.JTable();
        jbnPause = new javax.swing.JButton();
        jbnZip = new javax.swing.JButton();
        jlblAvancement = new javax.swing.JLabel();
        jbnCancel = new javax.swing.JButton();
        jbnExit = new javax.swing.JButton();
        jbnResume = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Download Manager");
        setResizable(false);

        jbnAdd.setText("Add Download");
        jbnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	jbnAddActionPerformed(evt);
            }
        });
        
        jbnListComics.setText("List comics");
        jbnListComics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	jbnListComicsActionPerformed(evt);
            }
        });

        jtbDownload.setModel(mTableModel);
        jScrollPane1.setViewportView(jtbDownload);
        mTableModel.addTableModelListener(new TableModelListener() {
        	//Pour scroller au fur et � mesure du download
			@Override
			public void tableChanged(final TableModelEvent e) {
				if ( e.getFirstRow() == e.getLastRow() )
				{
					Downloader d = DownloadManager.getInstance().getDownloadList().get(e.getFirstRow());
					if ( d.getState() == Downloader.DOWNLOADING )
					{
						SwingUtilities.invokeLater(new Runnable() {
			                public void run() {
			                    int viewRow = jtbDownload.convertRowIndexToView(e.getFirstRow());
			                    jtbDownload.scrollRectToVisible(jtbDownload.getCellRect(viewRow, 0, true));    
			                }
			            });
					}
					else if ( d.getState() == Downloader.COMPLETED)
					{
						jlblAvancement.setText("Download : " + DownloadManager.getInstance().getAvancementGlobal() + "% (" + DownloadManager.getInstance().getCompletedDownloadCount() + "/" + DownloadManager.getInstance().getDownloadList().size() + ")");
					}
				}
			}
		});

        jbnPause.setText("Pause");
        jbnPause.setEnabled(false);
        jbnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbnPauseActionPerformed(evt);
            }
        });

        jbnZip.setText("Zipper");
        jbnZip.setEnabled(true);
        jbnZip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbnZipActionPerformed(evt);
            }
        });
        jlblAvancement.setText("Download : ");

        jbnCancel.setText("Cancel");
        jbnCancel.setEnabled(false);
        jbnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbnCancelActionPerformed(evt);
            }
        });

        jbnExit.setText("Exit");
        jbnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbnExitActionPerformed(evt);
            }
        });

        jbnResume.setText("Resume");
        jbnResume.setEnabled(false);
        jbnResume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbnResumeActionPerformed(evt);
            }
        });
        

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbnPause, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jbnResume, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jbnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jbnZip, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jlblAvancement)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177, Short.MAX_VALUE)
                        .addComponent(jbnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jtxURL, javax.swing.GroupLayout.DEFAULT_SIZE, /*654*/600, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbnAdd)
                        .addComponent(jbnListComics))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbnCancel, jbnExit, jbnPause, jbnZip, jbnResume});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbnAdd)
                    .addComponent(jbnListComics))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbnPause)
                    .addComponent(jbnResume)
                    .addComponent(jbnCancel)
                    .addComponent(jbnZip)
                    .addComponent(jlblAvancement)
                    .addComponent(jbnExit))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbnPauseActionPerformed
    	mSelectedDownloader.pause();
        updateButtons();
    }//GEN-LAST:event_jbnPauseActionPerformed

    private void jbnResumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbnResumeActionPerformed
    	mSelectedDownloader.resume();
        updateButtons();
    }//GEN-LAST:event_jbnResumeActionPerformed

    private void jbnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbnCancelActionPerformed
    	mSelectedDownloader.cancel();
        updateButtons();
    }//GEN-LAST:event_jbnCancelActionPerformed

    private void jbnZipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbnRemoveActionPerformed
    	/*mIsClearing = true;
    	int index = jtbDownload.getSelectedRow();
    	DownloadManager.getInstance().removeDownload(index);
    	mTableModel.clearDownload(index);
        mIsClearing = false;
        mSelectedDownloader = null;
        updateButtons();*/
    	
    	if( tomes != null )
    	{
    		boolean hasToZip = true;
    		for ( Tome t : tomes )
    			for ( Page p : t.getPages() )
    				if ( p.getDownloader().getProgress() < 100 )
    				{
    					hasToZip = false;
    					break;
    					//JOptionPane.showMessageDialog(this, "Les pages ne sont pas encore toutes t�l�charg�es.", "Erreur", JOptionPane.ERROR_MESSAGE);
    					//return;
    				}
    		
    		if ( !hasToZip && 
    				JOptionPane.showConfirmDialog(this, "Les pages ne sont pas encore toutes t�l�charg�es, voulez-vous zipper quand m�me ?", "Attention", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION )
    				return;
    		
    		hasToZip = false;
    		for ( Tome t : tomes )
    		{
    			if ( t.getFolder().exists() )
    			{
	    			ZipOutputStream out = null;
	    			try {
		    			int BUFFER = 2048;
		    			byte data[] = new byte[BUFFER];
		    			FileOutputStream dest= new FileOutputStream(t.getFolder().getAbsolutePath() + ".cbz");
		    			BufferedOutputStream buff = new BufferedOutputStream(dest);
		    			out = new ZipOutputStream(buff);
		    			out.setMethod(ZipOutputStream.DEFLATED);
		    			out.setLevel(9);
		    			
		    			for(int i=0; i<t.getFolder().listFiles().length; i++) {
		    			    FileInputStream fi = new FileInputStream(t.getFolder().listFiles()[i]);
		    			    BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
		    			    ZipEntry entry= new ZipEntry(t.getFolder().listFiles()[i].getName());
		    			    out.putNextEntry(entry);
		    				
		    			    int count;
		    			    while((count = buffi.read(data, 0, BUFFER)) != -1) {
		    			        out.write(data, 0, count);
		    			    }
		    				
		    			    out.closeEntry();
		    				
		    			    buffi.close();
		    			}
		    			out.close();
		    			hasToZip = true;
	    			} catch (Exception e) {
	    				try { out.close(); } catch (IOException e1) { }
	    				JOptionPane.showMessageDialog(this, "Le tome " + t.getFolderName() + " n'a pas pu �tre zipp�.", "Erreur", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
    			}
    			else
    				JOptionPane.showMessageDialog(this, "Le tome " + t.getFolderName() + " n'a pas pu �tre zipp�, le dossier n'existe plus.", "Erreur", JOptionPane.ERROR_MESSAGE);
    		}
    		
    		if ( hasToZip )
    		{
	    		JOptionPane.showMessageDialog(this, "Action termin�e !", "Information", JOptionPane.INFORMATION_MESSAGE);
	    		
	    		try {
					Desktop.getDesktop().open(FOLDER_OUT);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }//GEN-LAST:event_jbnRemoveActionPerformed

    private void jbnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbnExitActionPerformed
        setVisible(false);
        System.exit(0);
    }//GEN-LAST:event_jbnExitActionPerformed

    private void jbnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbnAddActionPerformed
    	
    	jtxURL.setEnabled(false);
		jbnAdd.setEnabled(false);
		jbnListComics.setEnabled(false);
		
    	URL verifiedUrl = DownloadManager.verifyURL(jtxURL.getText());
        if (verifiedUrl != null) {
        	
        	final WebComic webComic = WebComic.getWebComic(verifiedUrl.toString());
        	
        	if ( webComic != null )
        	{
        		addComicToDownload(webComic);
        	}
        	else
        	{
        		jtxURL.setEnabled(true);
        		jbnAdd.setEnabled(true);
        		jbnListComics.setEnabled(true);
        		JOptionPane.showMessageDialog(DownloadComicsGUI.this, "Ce site n'est pas g�r�", "Error", JOptionPane.ERROR_MESSAGE);
        	}
        } else {
        	jtxURL.setEnabled(true);
        	jbnAdd.setEnabled(true);
        	jbnListComics.setEnabled(true);
            JOptionPane.showMessageDialog(DownloadComicsGUI.this, "Invalid Download URL", "Error", JOptionPane.ERROR_MESSAGE);
        }
    	
    }//GEN-LAST:event_jbnAddActionPerformed

    private void jbnListComicsActionPerformed(java.awt.event.ActionEvent evt) 
    {
    	URL verifiedUrl = DownloadManager.verifyURL(jtxURL.getText());
        if (verifiedUrl != null) {
	    	
	    	ListComicLite liste = WebComic.getListWebComics(verifiedUrl.toString());
	    	
	    	if ( liste != null )
	    	{
		    	DialogComicList dialogComics = new DialogComicList(liste);
		    	dialogComics.setModal(true);
		    	dialogComics.setLocationRelativeTo(this);
		    	dialogComics.setVisible(true);
	
		    	if ( !dialogComics.isCanceled() )
		    	{
		    		jtxURL.setEnabled(false);
		    		jbnAdd.setEnabled(false);
		    		jbnListComics.setEnabled(false);
		    		
		    		for ( ComicLite comic : dialogComics.getComicsLite() )
		    		{
			        	verifiedUrl = DownloadManager.verifyURL(comic.getTomesLite().get(0).getUrl());
			            if (verifiedUrl != null) {
			            	
			            	final WebComic webComic = WebComic.getWebComic(verifiedUrl.toString());
			            	
			            	if ( webComic != null )
			            	{
			            		addComicToDownload(webComic);
			            	}
			            	else
			            	{
			            		JOptionPane.showMessageDialog(DownloadComicsGUI.this, comic.getTitreCategory() + " -- Ce site n'est pas g�r� (" + comic.getTomesLite().get(0).getUrl() + ")", "Error", JOptionPane.ERROR_MESSAGE);
			            	}
			            } else {
			                JOptionPane.showMessageDialog(DownloadComicsGUI.this, comic.getTitreCategory() + " -- Invalid Download URL (" + comic.getTomesLite().get(0).getUrl() + ")", "Error", JOptionPane.ERROR_MESSAGE);
			            }
		    		}
		    		
		    	}
	    	} 
	    	else {
	        	JOptionPane.showMessageDialog(DownloadComicsGUI.this, "Ce site n'est pas g�r�", "Error", JOptionPane.ERROR_MESSAGE);
	        }
        }
        else {
	        JOptionPane.showMessageDialog(DownloadComicsGUI.this, "Invalid Download URL", "Error", JOptionPane.ERROR_MESSAGE);
	    }
    }
    
    
    private void addComicToDownload(final WebComic webComic)
    {
    	final SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>(){
    		@Override
    		protected Void doInBackground() {
				//DownloadManager.getInstance().SetNumConnPerDownload(1);
    			
    			List<Tome> tomesTmp;
    			try {
    				tomesTmp = webComic.getTomes();
    			} catch (IOException e)
    			{
    				jtxURL.setEnabled(true);
            		jbnAdd.setEnabled(true);
            		jbnListComics.setEnabled(true);
            		JOptionPane.showMessageDialog(DownloadComicsGUI.this, "Erreur lors de la r�cup�ration des tomes : \n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            		return null;
    			}
    			
    			if ( tomes == null )
					tomes = tomesTmp;
				else
					tomes.addAll(tomesTmp);
    			
        		Downloader download;
        		File folder;
        		URL pageUrl;
        		for ( Tome tome : tomesTmp )
        		{
        			folder = new File(FOLDER_OUT.getAbsolutePath() + File.separator + tome.getFolderName());
    				if ( folder.exists() )
    					folder.delete();
    				folder.mkdir();
    				tome.setFolder(folder);
    				int i = 1;
    				for ( Page page : tome.getPages() )
    				{
    					pageUrl = DownloadManager.verifyURL(page.getUrl());
			        	download = DownloadManager.getInstance().createDownload(pageUrl, folder.getAbsolutePath() + "/" + String.format("%03d", i) + "_");
			        	mTableModel.addNewDownload(download);
			        	
			        	page.setDownloader(download);
			        	i++;
    				}
        		}
        		
		        return null;
    		}
    	};
    	
    	new Thread(){
    		@Override
    		public void run() {
    			new WaintingForDownload(DownloadComicsGUI.this, mySwingWorker, webComic);
    		}
    	}.start();
    	mySwingWorker.execute();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 // Called when table row selection changes.
    private void tableSelectionChanged() {
    	// unregister from receiving notifications from the last selected download.
        if (mSelectedDownloader != null)
        	mSelectedDownloader.deleteObserver(DownloadComicsGUI.this);
        
        // If not in the middle of clearing a download, set the selected download and register to
        // receive notifications from it.
        if (!mIsClearing) {
        	int index = jtbDownload.getSelectedRow();
        	if (index != -1) {
	        	mSelectedDownloader = DownloadManager.getInstance().getDownload(jtbDownload.getSelectedRow());
	        	mSelectedDownloader.addObserver(DownloadComicsGUI.this);
        	} else
        		mSelectedDownloader = null;
            updateButtons();
        }
    }
    
    @Override
	public void update(Observable o, Object arg) {
    	// Update buttons if the selected download has changed.
        if (mSelectedDownloader != null && mSelectedDownloader.equals(o))
            updateButtons();
	}
    
    /**
     * Update buttons' state
     */
    private void updateButtons() {
        if (mSelectedDownloader != null) {
            int state = mSelectedDownloader.getState();
            switch (state) {
                case Downloader.DOWNLOADING:
                    jbnPause.setEnabled(true);
                    jbnResume.setEnabled(false);
                    jbnCancel.setEnabled(true);
                    //jbnZip.setEnabled(false);
                    break;
                case Downloader.PAUSED:
                	jbnPause.setEnabled(false);
                	jbnResume.setEnabled(true);
                	jbnCancel.setEnabled(true);
                	//jbnZip.setEnabled(false);
                    break;
                case Downloader.ERROR:
                	jbnPause.setEnabled(false);
                	jbnResume.setEnabled(true);
                	jbnCancel.setEnabled(false);
                	//jbnZip.setEnabled(true);
                    break;
                default: // COMPLETE or CANCELLED
                	jbnPause.setEnabled(false);
                	jbnResume.setEnabled(false);
                	jbnCancel.setEnabled(false);
                	//jbnZip.setEnabled(true);
            }
        } else {
            // No download is selected in table.
        	jbnPause.setEnabled(false);
        	jbnResume.setEnabled(false);
        	jbnCancel.setEnabled(false);
        	//jbnZip.setEnabled(false);
        }
    }
    
    /**
    * @param args the command line arguments 
    * arg[0] = URLProxy
    * arg[1] = PortProxy
    * arg[2] = userProxy
    * arg[3] = passwordProxy
    * 
    * Si pas de proxy : pas d'argument
    */
    public static void main(final String args[]) {
    	// set to user's look and feel
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
    	
    	
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	if ( args.length == 4 )
            		DownloadManager.getInstance().setProxy(args[0], Integer.parseInt(args[1]), args[2], args[3]);
            	
                new DownloadComicsGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbnAdd;
    private javax.swing.JButton jbnListComics;
    private javax.swing.JButton jbnCancel;
    private javax.swing.JButton jbnExit;
    private javax.swing.JButton jbnPause;
    private javax.swing.JButton jbnZip;
    private javax.swing.JLabel jlblAvancement;
    private javax.swing.JButton jbnResume;
    private javax.swing.JTable jtbDownload;
    private javax.swing.JTextField jtxURL;
    // End of variables declaration//GEN-END:variables
}