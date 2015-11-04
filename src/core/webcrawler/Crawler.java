package core.webcrawler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import core.stocks.TSXCompanies;

public class Crawler {

	/**
	 * Starts the crawler, searching the Yahoo Finance page for the stock history of the
	 * companies on the S&P TSX 60 list. Writes the history to text files currently (could be
	 * changed).
	 *
	 */
	public void crawl() {
		
		File resultFolder = new File("Results");
		if (!resultFolder.exists()) {
			resultFolder.mkdir();
		}

		// Hide folder for Windows/Linux
		Boolean hidden;
		try {
			hidden = (Boolean) Files.getAttribute(resultFolder.toPath(), "dos:hidden", LinkOption.NOFOLLOW_LINKS);
			if (hidden != null && !hidden) {
			    Files.setAttribute(resultFolder.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Check the result folder to see if the crawler was used before
		File[] results = resultFolder.listFiles();
		
		// If they exist, check that we have 60 and that we got data > 24 hours ago
		if (results.length != 0 && results.length == TSXCompanies.COMPANIES.length) {

			Long lastChanged = resultFolder.lastModified();
			Date today = new Date();
			Long todayMilli = today.getTime();
			
			// Was crawled less than 24 hours ago, skip this step.
			if (todayMilli - lastChanged <= 86400000) {
				System.out.println("Crawler used less than 24 hours ago, " +
								   "will use currently stored data.");
				return;
			}
		}

		// Iterate through the list of S&P TSX 60 Index companies
		for (String company : TSXCompanies.COMPANIES) {

			File historyData = new File(resultFolder, company + ".txt");

			URL url;
			try {
				url = new URL("http://ichart.yahoo.com/table.csv?s=" + company.toUpperCase() + ".TO&a=0&b=1&c=1950&d=4&e=1&f=2015&g=d&ignore=.csv");
				FileUtils.copyURLToFile(url, historyData);
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}	
	}	
}
