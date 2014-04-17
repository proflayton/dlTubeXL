package com.brandonlayton.dlTubeXL;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Download implements Runnable
{
	private AtomicBoolean stop;
	private String youtubeURL;
	
	private static String baseURL = "http://www.youtube-mp3.org/";
	
	public Download(String youtubeURL)
	{
		this.youtubeURL = youtubeURL;
		
		this.run();
	}


	public void stop()
	{
		this.stop.set(true);
	}
	
	@Override
	public void run() 
	{
		BufferedInputStream in = null;
		FileOutputStream out = null;
		try {
			final WebClient webClient = new WebClient(BrowserVersion.CHROME);
			final HtmlPage page = webClient.getPage(baseURL);
			//System.out.println(page.asText());
			final HtmlForm form = page.getForms().get(0);
			form.getInputByName("").setValueAttribute(this.youtubeURL);
			final HtmlPage secondPage = form.getButtonByName("").click();
			
			System.out.println(secondPage.asText());
			
			/*
			URL dlURL = new URL("");
			in = new BufferedInputStream(dlURL.openStream());
			String filePath = "C:\\myMusik\\"+""+".mp3";
			out = new FileOutputStream(filePath);
			
			final byte[] data = new byte[1024];
			int count;
			while((count = in.read(data,0,1024)) != -1)
			{
				if(stop.get() == true)
				{
					out.close();
					in.close();
					Files.deleteIfExists(FileSystems.getDefault().getPath(filePath));
					return;
				}
				out.write(data,0,count);
			}
			*/
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		try {
			if(in != null)
				in.close();
			if(out != null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
