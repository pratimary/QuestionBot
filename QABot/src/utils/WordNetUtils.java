package utils;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetUtils {
	private static WordNetDatabase wordNetDatabase;
	
	public WordNetUtils() {
		System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
		wordNetDatabase = WordNetDatabase.getFileInstance();
	}
	
	public void getSynsets(String word) {
		Synset[] synsets = wordNetDatabase.getSynsets(word);
		for(Synset synset : synsets) {
			String[] wordForms = synset.getWordForms();
			for(String w : wordForms) {
				System.out.println(w);
			}
		}
	}

}
