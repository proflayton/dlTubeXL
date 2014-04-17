package com.brandonlayton.dlTubeXL;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Vector;

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
	private int HEIGHT = 400;

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
	
	public YoutubeMusicListCreator()
	{
		this.setTitle("Music List Creator");
		this.setLayout(new BorderLayout());
		this.setSize(WIDTH,HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel topPanel = new JPanel();
		searchField = new JTextField("",30);
		searchButton= new JButton("Search");
		searchButton.addActionListener(new SearchListener());
		clearSearchButton  = new JButton("Clear Search");
		clearSearchButton.addActionListener(new SearchClearListener());
		topPanel.add(searchField, getConstraints(0,0,2,1,GridBagConstraints.WEST));
		topPanel.add(searchButton,getConstraints(2,0,1,1,GridBagConstraints.WEST));
		topPanel.add(clearSearchButton,getConstraints(3,0,1,1,GridBagConstraints.WEST));
		
		searchResults = new TubeList();
		searchResults.addMouseListener(new ListListener());
		searchScroll  = new JScrollPane(searchResults);
		
		selectedArea = new JTextArea("");
		selectedArea.setRows(10);
		selectedAreaScroll = new JScrollPane(selectedArea);
		
		dlButton = new JButton("Download List");
		dlButton.addActionListener(new DlListener());
		
		this.add(topPanel	 ,BorderLayout.NORTH );
		
		JPanel centerArea = new JPanel(new BorderLayout());
		centerArea.add(searchScroll,BorderLayout.CENTER);
		centerArea.add(selectedAreaScroll,BorderLayout.EAST);
		this.add(centerArea,BorderLayout.CENTER);
		this.add(dlButton,BorderLayout.SOUTH);
		
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
			
		}
	}

	/****
	 * Creates GridBagConstraint based off input. Makes code more clean when building
	 * the GUI
	 ****/
	private GridBagConstraints getConstraints(int    gridx  ,    int gridy,
			                                  double weightx, double weighty,
											  int    anchor)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = weightx;
		c.weighty = weighty;
		c.gridx   = gridx;
		c.gridy   = gridy;
		c.anchor  = anchor;
		return c;
	}
}
