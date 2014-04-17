package com.brandonlayton.dlTubeXL;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


@SuppressWarnings("serial")
public class YoutubeMusicListCreator extends JFrame
{
	private int WIDTH  = 400;
	private int HEIGHT = 800;
	
	private JTextField 	searchField;
	private JButton	  	searchButton;
	private JButton		clearSearchButton;
	private JButton		dlButton;
	private TubeList	searchResults;
	private JTextArea 	selectedArea;
	private JScrollPane searchScroll;
	private JScrollPane selectedAreaScroll;
	
	private static String baseQueryURL = "https://www.youtube.com/results?search_query=";

	//private Vector<TubeListElement> selected = new Vector<TubeListElement>();
	
	private int concurrentThreads = 0;
	private int maxThreads 		  = 1;
	
	public YoutubeMusicListCreator()
	{
		this.setTitle("Music List Creator");
		this.setLayout(new GridBagLayout());
		this.setSize(WIDTH,HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel topPanel = new JPanel();
		searchField = new JTextField("",30);
		searchButton= new JButton("Search");
		searchButton.addActionListener(new SearchListener());
		clearSearchButton  = new JButton("Clear Search");
		clearSearchButton.addActionListener(new SearchClearListener());
		topPanel.add(searchField, getConstraints(0,0,2,1,GridBagConstraints.WEST,GridBagConstraints.BOTH));
		topPanel.add(searchButton,getConstraints(2,0,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH));
		topPanel.add(clearSearchButton,getConstraints(3,0,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH));
		
		searchResults = new TubeList();
		searchResults.addMouseListener(new ListListener());
		searchResults.setVisibleRowCount(3);
		searchScroll  = new JScrollPane(searchResults);
		
		selectedArea = new JTextArea("");
		selectedArea.setRows(10);
		selectedAreaScroll = new JScrollPane(selectedArea);
		
		dlButton = new JButton("Download List");
		dlButton.addActionListener(new DlListener());
		
		this.add(topPanel	  , getConstraints(0,0,1,0.1,GridBagConstraints.WEST,GridBagConstraints.BOTH));
		this.add(searchScroll, getConstraints(0,1,1,0.4,GridBagConstraints.WEST,GridBagConstraints.BOTH));
		this.add(selectedArea , getConstraints(0,5,1,0.4,GridBagConstraints.WEST,GridBagConstraints.BOTH));
		this.add(dlButton	  , getConstraints(0,7,0.5,0.1,GridBagConstraints.WEST,GridBagConstraints.BOTH));
		
		this.pack();
	}
	
	private void Search(String query)
	{
		try {
			System.out.println(query);
			Document doc = Jsoup.connect(baseQueryURL + query).get();
			Element searchResults = doc.select("#search-results").first();
			Elements results = searchResults.select(".result-item-padding");
			for(Element result : results)
			{
				Element thumb = result.select("span.yt-thumb-clip").first();
				Element img   = thumb.select("img").first();
				String url = img.attr("data-thumb");
				if(url.startsWith("//"))
					url = "http:" + url;
				
				String title = "";
				String wURL  = "";
				Element titleElement = result.select(".yt-lockup-content").first()
											 .select("h3").first()
											 .select("a").first();
				title = titleElement.text();
				wURL  = "http://www.youtube.com"+titleElement.attr("href");
				
				this.searchResults.addElement(
						new TubeListElement(url,title,wURL)
						);
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"ERROR SEARCHING!");
		}
		this.pack();
	}
	
	private class SearchListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if(searchField.getText().length() > 0)
			{
				Search(searchField.getText().replace(" ", "+"));
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Input a Search Query please");
			}
		}
	}
	
	private class SearchClearListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			searchResults.clear();
		}
	}
	
	private class ListListener implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent e) 
		{
			TubeList tube = (TubeList)e.getSource();
			if(e.getClickCount() == 2)
			{
				int index = tube.locationToIndex(e.getPoint());
				TubeListElement clicked = (TubeListElement)tube.getElement(index);
				selectedArea.setText(selectedArea.getText() + clicked.getURL() + "\n");
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) { }
		@Override
		public void mouseExited(MouseEvent e) { }
		@Override
		public void mousePressed(MouseEvent e) { }
		@Override
		public void mouseReleased(MouseEvent e) { }
		
	}
	
	private class DlListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			Download();
		}
	}
	
	private void Download()
	{
		String[] list = this.selectedArea.getText().split("\n");
		ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
		for(int i = 0 ; i < list.length; i++)
		{
			Runnable dlT = new DownloadThread(list[i]); 
			executor.execute(dlT);
		}
		executor.shutdown();
		while(!executor.isTerminated())
		{
			
		}
		System.out.println("FINISHED");
	}
	
	private class DownloadThread implements Runnable
	{
		private Runtime rt;
		String url;
		
		public DownloadThread(String url)
		{
			this.url = url; 
			rt = Runtime.getRuntime();
		}
		
		public void run()
		{
			log("Started");
			concurrentThreads++;
			String lameExe = Main.class.getClassLoader().getResource("dependencies/lame.exe").getPath();
			String youtubeDlExe = Main.class.getClassLoader().getResource("dependencies/youtube-dl.exe").getPath();
			String ffmpegExe = Main.class.getClassLoader().getResource("dependencies/ffmpeg/bin/ffmpeg.exe").getPath();
			log(lameExe);
			log(youtubeDlExe);
			log(ffmpegExe);
			BufferedReader in = null;
			try {
				Process getTitle = rt.exec(youtubeDlExe + " --get-title " + url);
				in = new BufferedReader(new InputStreamReader(getTitle.getInputStream()));
				getTitle.waitFor();
				String output = in.readLine();
				String title = output.split("\n")[0];
				
				log("Title: " + title);
				
				Process dl = rt.exec(youtubeDlExe + " " + url);
				in = new BufferedReader(new InputStreamReader(dl.getInputStream()));
				dl.waitFor();
				
				log("OUTPUT----");
				output = in.readLine();
				while(!output.contains("Destination: "))
				{
					log(output);
					output = in.readLine();
					if(output.contains("has already been downloaded"))
					{
						log(title + " has already been downloaded!");
						return;
					}
				}
				String dlPath = output.split("Destination: ")[1].trim();
				log("DL PATH: " + dlPath);
				String path = "";
				try {
					String ffmpegCmd = ffmpegExe + " -i \"" + dlPath + "\" \"" + path + title + ".wav\"" ;
					log(ffmpegCmd);
					Process ffmpeg = rt.exec(ffmpegCmd);
					ffmpeg.waitFor();
					try {
						String lameCmd = lameExe + " \"" +  path + title + ".wav" + "\" \"" + path + title + ".mp3\"";
						log(lameCmd);
						Process lame = rt.exec(lameCmd);
						in = new BufferedReader(new InputStreamReader(lame.getInputStream()));
						while ((output = in.readLine()) != null) {
							System.out.println(output);
						}
						lame.waitFor();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(in != null)
			{
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			concurrentThreads--;
			log("Ended");
		}
		
		private void log(String logging)
		{
			System.out.println(Thread.currentThread().getName() + "--" + logging);
		}
	}

	/****
	 * Creates GridBagConstraint based off input. Makes code more clean when building
	 * the GUI
	 ****/
	private GridBagConstraints getConstraints(int    gridx  ,    int gridy,
			                                  double weightx, double weighty,
											  int    anchor , int fill)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = weightx;
		c.weighty = weighty;
		c.gridx   	= gridx;
		c.gridy   	= gridy;
		c.anchor  	= anchor;
		c.fill 	  	= fill;
		return c;
	}
}
