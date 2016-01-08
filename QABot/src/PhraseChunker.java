import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PhraseChunker {
	List<String> words = new LinkedList<String>();
	List<String> tags = new LinkedList<String>();
	
	public void phraseChuncker(List<String> wordList, List<String> tagList) {
		Iterator<String> wordIterator = wordList.iterator();
		Iterator<String> tagIterator = tagList.iterator();
		String currentTag = "";
		String currentWord = "";

		if(tagIterator.hasNext()) {
			currentTag = tagIterator.next();
			currentWord = wordIterator.next();
		}
		else {
			return;
		}
		
		while (true) {
			if(currentWord==null)
				break;
			
			if (!(currentTag.equals("NN") ||  currentTag.equals("JJ") || currentTag.equals("NNP")
					|| currentWord.equals("more") || currentWord.equals("most") || currentWord.equals("$"))) {
				words.add(currentWord);
				tags.add(currentTag);
				
				if(tagIterator.hasNext()) {
					currentTag = tagIterator.next();
					currentWord = wordIterator.next();
				}
				else
					break;
			} 
			else {
				if (currentWord.equals("more") || currentWord.equals("most")) {
					String prevWord = currentWord;
					String prevTag = currentTag;
					int flag=0;
					if (tagIterator.hasNext()) {
						flag=1;
						if((currentTag = tagIterator.next()).equals("JJ") || currentTag.equals("JJR") || currentTag.equals("JJS")) {
							currentWord = wordIterator.next();
							words.add(prevWord + " " + currentWord);
							if (prevWord.equals("more"))
								tags.add("JJR");
							else
								tags.add("JJS");
							if(tagIterator.hasNext()) {
								currentTag = tagIterator.next();
								currentWord = wordIterator.next();
							}
							else
								break;
						}
						else {
							words.add(prevWord);
							tags.add(prevTag);
							if (wordIterator.hasNext())
								currentWord = wordIterator.next();
							else {
								currentWord=null;
								currentTag=null;
							}
						}
					}
					else {
						if(flag==0) {
							words.add(prevWord);
							tags.add(prevTag);
						}
						break;
					}
				}
				if (currentWord.equals("$")) {
					String prevWord = currentWord;
					String prevTag = currentTag;
					int flag=0;
					if (tagIterator.hasNext()) {
						flag=1;
						if((currentWord = wordIterator.next()).equals("c")) {
							currentTag = tagIterator.next();
							words.add(prevWord + currentWord);
							tags.add("NN");
							if(tagIterator.hasNext()) {
								currentTag = tagIterator.next();
								currentWord = wordIterator.next();
							}
							else
								break;
						}
						else {
							words.add(prevWord);
							tags.add(prevTag);
							if (tagIterator.hasNext())
								currentTag = tagIterator.next();
							else {
								currentWord=null;
								currentTag=null;
							}
						}
					}
					else {
						if(flag==0) {
							words.add(prevWord);
							tags.add(prevTag);
						}
						break;
					}
				}
				if (currentWord!=null && currentTag.equals("JJ")) {
					String finalTag = "JJ";
					String prevWord = currentWord;
					String memory = prevWord;
					int end=0;
					int noMatch=0;
					while (true) {
						if (tagIterator.hasNext()) {
							if((currentTag = tagIterator.next()).equals("JJ") || currentTag.equals("CD")) {
								currentWord = wordIterator.next();
								memory += " " + currentWord;
							}
							else if (currentTag.equals("NN") || currentTag.equals("NNS") || currentTag.equals("NNP") || currentTag.equals("NNPS")) {
								currentWord = wordIterator.next();
								memory += " " + currentWord;
								finalTag = currentTag;
							}
							else {
								noMatch=1;
							}
						}
						else {
							end=1;
						}
						if(end==1) {
							words.add(memory);
							tags.add(finalTag);
							currentWord=null;
							currentTag=null;
							break;
						}
						else if(noMatch==1) {
							words.add(memory);
							tags.add(finalTag);
							currentWord = wordIterator.next();
							break;
						}
					}
				}
				
				
				
				if (currentWord!=null && currentTag.equals("NN")) {
					String finalTag = "NN";
					String prevWord = currentWord;
					String memory = prevWord;
					int end=0;
					int noMatch=0;
					while (true) {
						if (tagIterator.hasNext()) {
							if((currentTag = tagIterator.next()).equals("NN") || currentTag.equals("CD")) {
								currentWord = wordIterator.next();
								memory += " " + currentWord;
							}
							else if (currentTag.equals("NNS") || currentTag.equals("NNP") || currentTag.equals("NNPS")) {
								currentWord = wordIterator.next();
								memory += " " + currentWord;
								finalTag = currentTag;
							}
							else {
								noMatch=1;
							}
						}
						else {
							end=1;
						}
						if(end==1) {
							words.add(memory);
							tags.add(finalTag);
							currentWord=null;
							currentTag=null;
							break;
						}
						else if(noMatch==1)
						{
							words.add(memory);
							tags.add(finalTag);
							currentWord = wordIterator.next();
							break;
						}
					}
				}
				if (currentWord!=null && currentTag.equals("NNP")) {
					String finalTag = "NNP";
					String prevWord = currentWord;
					String memory = prevWord;
					int end=0;
					int noMatch=0;
					while (true) {
						if (tagIterator.hasNext()) {
							if((currentTag = tagIterator.next()).equals("NNP") || currentTag.equals("CD")) {
								currentWord = wordIterator.next();
								memory += " " + currentWord;
							}
							else if (currentTag.equals("NN") || currentTag.equals("NNS") || currentTag.equals("NNPS")) {
								currentWord = wordIterator.next();
								memory += " " + currentWord;
								finalTag = currentTag;
							}
							else {
								noMatch=1;
							}
						}
						else {
							end=1;
						}
						if(end==1) {
							words.add(memory);
							tags.add(finalTag);
							currentWord=null;
							currentTag=null;
							break;
						}
						else if(noMatch==1) {
							words.add(memory);
							tags.add(finalTag);
							currentWord = wordIterator.next();
							break;
						}
					}
				}
			}
		}
		
	}
	
	public void TrimNounTags() {
		int n = tags.size();
		for(int i=0; i<n; i++) {
			if(tags.get(i).startsWith("NN")) {
				tags.set(i, "NN");
			}
		}
	}
	
	public List<String> getWordListAfterPhraseChunking() {
		return words;
	}
	
	public List<String> getTagListAfterPhraseChunking() {
		return tags;
	}

}
