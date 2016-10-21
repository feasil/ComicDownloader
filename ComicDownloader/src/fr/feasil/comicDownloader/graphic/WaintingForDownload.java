package fr.feasil.comicDownloader.graphic;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import fr.feasil.comicDownloader.webComic.WebComic;

public class WaintingForDownload extends JDialog implements Observer {

	private static final long serialVersionUID = 1L;
	
	private JProgressBar progressBar;

	public WaintingForDownload(JFrame parent, SwingWorker<Void, Void> mySwingWorker, final WebComic webComic) {
		super(parent, "Dialog", true);
		
		mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("state")) {
					if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
						webComic.deleteObserver(WaintingForDownload.this);
						dispose();
					}
				}
			}
		});
		
		webComic.addObserver(this);
		
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		//setPreferredSize(new Dimension(200, 25));
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progressBar, BorderLayout.CENTER);
		panel.add(new JLabel("Please wait..."), BorderLayout.PAGE_START);
		add(panel);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
		
		
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if ( arg1 instanceof Object[] && ((Object[]) arg1).length == 2 )
		{
			Object[] o = (Object[]) arg1;
			if ( "total".equals(o[0]) )
			{
				progressBar.setIndeterminate(false);
				progressBar.setMaximum((Integer) o[1]);
				progressBar.setStringPainted(true);
			}
			else if ( "avancement".equals(o[0]) )
			{
				progressBar.setValue((Integer) o[1]);
			}
		}
	}
	
}
