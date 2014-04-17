package com.brandonlayton.dlTubeXL;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainGUI extends JFrame implements ActionListener
{

	JPanel dlPanel;
	JButton download;
	JTextField vid;
	
	ArrayList<Download> downloads = new ArrayList<Download>();
	
	public MainGUI()
	{
		this.setSize(500,600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("myMusik");
		
		this.setLayout(new BorderLayout());
		
		download = new JButton("Download");
		vid = new JTextField("Youtube URL");
		
		download.addActionListener(this);
		
		dlPanel = new JPanel(new BorderLayout());
		dlPanel.add(vid,BorderLayout.NORTH);
		dlPanel.add(download,BorderLayout.SOUTH);
		
		this.add(dlPanel);
		//this.pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == download)
		{
			String id = vid.getText();
			if(id.equals(""))
			{
				
				return;
			}
			downloads.add(new Download(id));
		}
	}

}
