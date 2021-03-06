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

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class DownloadManager {
	
	// The unique instance of this class
	private static DownloadManager sInstance = null;
	
	// Constant variables
	private static final int DEFAULT_NUM_CONN_PER_DOWNLOAD = 8;
	private static final int NUM_PARALLEL_DOWNLOAD = 8;
	public static final String DEFAULT_OUTPUT_FOLDER = "";
	
	// Member variables
	private int mNumConnPerDownload;
	private ArrayList<Downloader> mDownloadList;
	
	private Vector<Downloader> mDownloadNow;
	private Vector<Downloader> mDownloading;
	
	
	private Proxy proxy = null;
	
	/** Protected constructor */
	protected DownloadManager() {
		mNumConnPerDownload = DEFAULT_NUM_CONN_PER_DOWNLOAD;
		mDownloadList = new ArrayList<Downloader>();
		mDownloadNow = new Vector<Downloader>();
		mDownloading = new Vector<Downloader>();
		
		new Thread(){
			public void run() {
				int nbEnCours;
				while ( true )
				{
					try {
						nbEnCours = 0;
						for ( Downloader d : mDownloading )
						{
							if ( d.getProgress() == 100 || d.getState() != Downloader.DOWNLOADING )
							{
								mDownloading.remove(d);
								mDownloadNow.remove(d);
							}
							else
								nbEnCours++;
						}
						
						for ( Downloader d : mDownloadNow )
						{
							if ( nbEnCours >= NUM_PARALLEL_DOWNLOAD )
								break;
							
							if ( !mDownloading.contains(d) )
							{
								mDownloading.add(d);
								new Thread(d).start();
								nbEnCours++;
							}
						}
						
						Thread.sleep(500);
					}
					catch (Throwable t) { }
				}
			}
		}.start();
	}
	
	/**
	 * Get the max. number of connections per download
	 */
	public int getNumConnPerDownload() {
		return mNumConnPerDownload;
	}
	
	/**
	 * Set the max number of connections per download
	 */
	public void SetNumConnPerDownload(int value) {
		mNumConnPerDownload = value;
	}
	
	/**
	 * Get the downloader object in the list
	 * @param index
	 * @return
	 */
	public Downloader getDownload(int index) {
		return mDownloadList.get(index);
	}
	
	public void removeDownload(int index) {
		mDownloadList.remove(index);
	}
	
	/**
	 * Get the download list
	 * @return
	 */
	public ArrayList<Downloader> getDownloadList() {
		return mDownloadList;
	}
	
	/**
	 * Renvoie l'avancement global en %
	 */
	public int getAvancementGlobal() {
		return (int) ((getCompletedDownloadCount()*100.)/mDownloadList.size());
	}
	public int getCompletedDownloadCount() {
		int avancement = 0;
		for ( Downloader d : mDownloadList )
			if ( d.getState() == Downloader.COMPLETED )
				avancement++;
		return avancement;
	}
	
	
	public Downloader createDownload(URL verifiedURL, String outputFolder) {
		HttpDownloader fd = new HttpDownloader(verifiedURL, outputFolder, mNumConnPerDownload);
		mDownloadList.add(fd);
		
		return fd;
	}
	
	
	
	protected void downloadNow(Downloader d) {
		if ( !mDownloadNow.contains(d) )
			mDownloadNow.add(d);
	}
	
	
	public void setProxy(String urlProxy, int portProxy, final String userProxy, final String passwordProxy) {
		this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(urlProxy, portProxy));;
		//System.out.println("DEBUG :: setProxy : " + urlProxy + "  " + portProxy + "  " + userProxy + "  " + passwordProxy);
		Authenticator authenticator = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return (new PasswordAuthentication(userProxy, passwordProxy.toCharArray()));
			}
		};
		Authenticator.setDefault(authenticator);
	}
	public Proxy getProxy() {
		return proxy;
	}
	public boolean useProxy() {
		return proxy != null;
	}
	
	
	
	
	/**
	 * Get the unique instance of this class
	 * @return the instance of this class
	 */
	public static DownloadManager getInstance() {
		if (sInstance == null)
			sInstance = new DownloadManager();
		
		return sInstance;
	}
	
	/**
	 * Verify whether an URL is valid
	 * @param fileURL
	 * @return the verified URL, null if invalid
	 */
	public static URL verifyURL(String fileURL) {
		// Only allow HTTP URLs.
        if (!fileURL.toLowerCase().startsWith("http://") && !fileURL.toLowerCase().startsWith("https://"))
            return null;
        
        // Verify format of URL.
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(fileURL);
        } catch (Exception e) {
            return null;
        }
        
        // Make sure URL specifies a file.
        //if (verifiedUrl.getFile().length() < 2)
        //    return null;
        
        return verifiedUrl;
	}
}