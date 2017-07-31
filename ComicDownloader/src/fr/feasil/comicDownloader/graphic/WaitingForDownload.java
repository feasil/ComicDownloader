package fr.feasil.comicDownloader.graphic;

import java.awt.BorderLayout;
import java.awt.Component;
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

public class WaitingForDownload extends JDialog implements Observer {

	private static final long serialVersionUID = 1L;
	
	private JProgressBar progressBar;
	private String prefixValue = null;
	
	public WaitingForDownload(JFrame parent, SwingWorker<?, ?> mySwingWorker, final Observable obs) {
		super(parent, "Dialog", true);
		
		init(parent, mySwingWorker, obs);
	}
	public WaitingForDownload(JDialog parent, SwingWorker<?, ?> mySwingWorker, final Observable obs) {
		super(parent, "Dialog", true);
		
		init(parent, mySwingWorker, obs);
	}
	
	private void init(Component parent, SwingWorker<?, ?> mySwingWorker, final Observable obs) 
	{
		mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("state")) {
					if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
						if ( obs != null )
							obs.deleteObserver(WaitingForDownload.this);
						dispose();
					}
				}
			}
		});
		
		if ( obs != null )
			obs.addObserver(this);
		
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		//setPreferredSize(new Dimension(200, 25));
		
		progressBar = new JProgressBar(){
			private static final long serialVersionUID = 1L;
			@Override
			public String getString() {
				if ( prefixValue == null )
					return super.getString();
				
				return prefixValue + getValue();
			}
		};
		
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
				progressBar.repaint();
			}
			else if ( "prefixe".equals(o[0]) )
			{
				prefixValue = (String) o[1];
				//progressBar.repaint();
			}
		}
		else if ( arg1 instanceof Object[] && ((Object[]) arg1).length == 1 )
		{
			Object[] o = (Object[]) arg1;
			if ( "infinite".equals(o[0]) )
			{
				progressBar.setIndeterminate(true);
				progressBar.setStringPainted(false);
			}
		}
		
	}
	
}
