package com.brandonlayton.dlTubeXL;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TubeListElement extends JPanel
{
	ImageIcon image;
	String title;
	String url;
	
	public TubeListElement(String imageURL, String title, String url) throws MalformedURLException, IOException
	{
		try
		{
			this.image = new ImageIcon(ImageIO.read(new URL(imageURL)));
		}
		catch(Exception e)
		{
			this.image = new ImageIcon(ImageIO.read(Main.class.getClassLoader().getResource("default.jpg")));
		}
		this.title = title;
		this.url   = url;
		
		this.setLayout(new BorderLayout());
		this.add(new JLabel(title,JLabel.CENTER),BorderLayout.NORTH);
		
		JLabel imgLabel = new JLabel(image);
		imgLabel.setMinimumSize(new Dimension(50,50));
		this.add(imgLabel,BorderLayout.CENTER);
	}
	
	public String getURL()
	{
		return this.url;
	}
}
