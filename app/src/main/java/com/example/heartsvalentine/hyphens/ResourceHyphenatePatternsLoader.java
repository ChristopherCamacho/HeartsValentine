package com.example.heartsvalentine.hyphens;
import com.example.heartsvalentine.MainActivity;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResourceHyphenatePatternsLoader {
	private final String hpl;
	private final List<String> patterns = new ArrayList<>();
	private final List<String> exceptions = new ArrayList<>();
	public ResourceHyphenatePatternsLoader(String hpl) {
		this.hpl = hpl;
	}
		
	public void load() {
		
		try {
			String hyphenFileFolder = MainActivity.getHyphenFileFolder();
			if (hyphenFileFolder == null) {
				throw new Exception("Hyphen folder not created.");
			}

			FileInputStream inputStream = new FileInputStream(hyphenFileFolder + hpl);
			//	File file = new File(System.getenv(getEnvString())); Does not work for larger files like hyph-en-gb.tex!
			Scanner scanner = new Scanner(inputStream);
			boolean readPatterns = false;
			boolean readHyphenation = false;
			boolean reset = false;

			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				
				if (data.length() > 0 && data.charAt(0) != '%') {			
					if (data.contains("%")) {
						data = data.substring(0, data.indexOf('%'));
					}
					data = data.trim();
							
					if (data.equals("\\patterns{")) {
						readPatterns = true;
						continue;
					}
					else if (data.equals("\\hyphenation{")) {
						readHyphenation = true;	
						continue;
					}
					else if (data.contains("}")) {
						data = data.substring(0, data.indexOf('}'));
						data = data.trim();
						reset = true;
					}
					// The file hyph-fr.tex has lots of lines with an apostrophe outside of a comment.
					// This seems to mean line should be ignored.
					else if (data.contains("'")) {
						continue;
					}
					
					if (data.contains(" ")) {
						String[] dataSplit = data.split(" ");
						
						for (String str : dataSplit) {
							addElement(str, readPatterns, readHyphenation);
						}
					}
					else {
						addElement(data, readPatterns, readHyphenation);
					}

					if (reset) {
						readPatterns = false;
						readHyphenation = false;
						reset = false;
					}	
				}
			}
			scanner.close();
	    } catch (Exception e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
		}
	}
	
	public List<String> getPatterns() {
		return patterns;
	}
	
	public List<String> getExceptions() {
		return exceptions;
	}
	
	private void addElement(String tag, boolean readPatterns, boolean readHyphenation) {

		if (readPatterns) {
			patterns.add(tag);
		}
		else if (readHyphenation) {
			exceptions.add(tag);
		}		
	}
}
