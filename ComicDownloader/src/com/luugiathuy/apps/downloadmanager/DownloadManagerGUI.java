/**
Copyright (c) 2011-present - Luu Gia Thuy
Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:
The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/

package com.luugiathuy.apps.downloadmanager;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DownloadManagerGUI extends javax.swing.JFrame implements Observer{

	private static final long serialVersionUID = 8489399426552541643L;
	
	private DownloadTableModel mTableModel;
	
	private Downloader mSelectedDownloader;
	
	private boolean mIsClearing;
	
	/** Creates new form DownloadManagerGUI */
    public DownloadManagerGUI() {
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
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtxURL = new javax.swing.JTextField();
        jbnAdd = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtbDownload = new javax.swing.JTable();
        jbnPause = new javax.swing.JButton();
        jbnRemove = new javax.swing.JButton();
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

        jtbDownload.setModel(mTableModel);
        jScrollPane1.setViewportView(jtbDownload);

        jbnPause.setText("Pause");
        jbnPause.setEnabled(false);
        jbnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbnPauseActionPerformed(evt);
            }
        });

        jbnRemove.setText("Remove");
        jbnRemove.setEnabled(false);
        jbnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbnRemoveActionPerformed(evt);
            }
        });

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
                        .addComponent(jbnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177, Short.MAX_VALUE)
                        .addComponent(jbnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jtxURL, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbnAdd))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbnCancel, jbnExit, jbnPause, jbnRemove, jbnResume});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbnAdd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbnPause)
                    .addComponent(jbnResume)
                    .addComponent(jbnCancel)
                    .addComponent(jbnRemove)
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

    private void jbnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbnRemoveActionPerformed
    	mIsClearing = true;
    	int index = jtbDownload.getSelectedRow();
    	DownloadManager.getInstance().removeDownload(index);
    	mTableModel.clearDownload(index);
        mIsClearing = false;
        mSelectedDownloader = null;
        updateButtons();
    }//GEN-LAST:event_jbnRemoveActionPerformed

    private void jbnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbnExitActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbnExitActionPerformed

    private void jbnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbnAddActionPerformed
    	URL verifiedUrl = DownloadManager.verifyURL(jtxURL.getText());
        if (verifiedUrl != null) {
        	Downloader download = DownloadManager.getInstance().createDownload(verifiedUrl, 
        			DownloadManager.DEFAULT_OUTPUT_FOLDER);
        	mTableModel.addNewDownload(download);
        	jtxURL.setText(""); // reset add text field
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid Download URL", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jbnAddActionPerformed

 // Called when table row selection changes.
    private void tableSelectionChanged() {
    	// unregister from receiving notifications from the last selected download.
        if (mSelectedDownloader != null)
        	mSelectedDownloader.deleteObserver(DownloadManagerGUI.this);
        
        // If not in the middle of clearing a download, set the selected download and register to
        // receive notifications from it.
        if (!mIsClearing) {
        	int index = jtbDownload.getSelectedRow();
        	if (index != -1) {
	        	mSelectedDownloader = DownloadManager.getInstance().getDownload(jtbDownload.getSelectedRow());
	        	mSelectedDownloader.addObserver(DownloadManagerGUI.this);
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
                    jbnRemove.setEnabled(false);
                    break;
                case Downloader.PAUSED:
                	jbnPause.setEnabled(false);
                	jbnResume.setEnabled(true);
                	jbnCancel.setEnabled(true);
                	jbnRemove.setEnabled(false);
                    break;
                case Downloader.ERROR:
                	jbnPause.setEnabled(false);
                	jbnResume.setEnabled(true);
                	jbnCancel.setEnabled(false);
                	jbnRemove.setEnabled(true);
                    break;
                default: // COMPLETE or CANCELLED
                	jbnPause.setEnabled(false);
                	jbnResume.setEnabled(false);
                	jbnCancel.setEnabled(false);
                	jbnRemove.setEnabled(true);
            }
        } else {
            // No download is selected in table.
        	jbnPause.setEnabled(false);
        	jbnResume.setEnabled(false);
        	jbnCancel.setEnabled(false);
        	jbnRemove.setEnabled(false);
        }
    }
    
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
    	// set to user's look and feel
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DownloadManagerGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbnAdd;
    private javax.swing.JButton jbnCancel;
    private javax.swing.JButton jbnExit;
    private javax.swing.JButton jbnPause;
    private javax.swing.JButton jbnRemove;
    private javax.swing.JButton jbnResume;
    private javax.swing.JTable jtbDownload;
    private javax.swing.JTextField jtxURL;
    // End of variables declaration//GEN-END:variables
}