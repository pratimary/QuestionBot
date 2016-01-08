import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class IndicativeExtractorPattern {
	static MaxentTagger tagger = new MaxentTagger("tagger/english-left3words-distsim.tagger");
	List<String> words = new LinkedList<String>();
	List<String> tags = new LinkedList<String>();
	PhraseChunker phraseChunker = new PhraseChunker();
	Set<Integer> additionalComparators = new HashSet<Integer>();
	String[] comparators = new String[2];
	ComparatorExtractor comparatorExtractor;
	Set<String> totalComparatorSet = new HashSet<String>();
	
	boolean pattern = false;
	boolean heuristics = false;
	boolean wh = false;
	boolean vs = false;
	boolean JJR = false;
	
	public IndicativeExtractorPattern(String input) {
		comparators[0] = null;
		comparators[1] = null;
		if(input != null)
		{
			processString(input);
		}
	}
	
	public IndicativeExtractorPattern GetQuestionUsingComparators(Pair<String,String> pair)
	{
		IndicativeExtractorPattern result = this;
		int[] idx = {-1, -1};
		int x = 0;
		String firstMatch = "";
		for(int i = 0;i < words.size(); i++) {
			if(words.get(i).equals(pair.x) || words.get(i).equals(pair.y)) {
				idx[x++] = i;
				if(x == 2) break;
				
				if(words.get(i).equals(pair.x) && !firstMatch.equals(""))
					firstMatch = pair.x;
				else if(words.get(i).equals(pair.y) && !firstMatch.equals(""))
					firstMatch = pair.y;
			}
		}
		if(idx[0] != -1 && idx[1] != -1) {
			result.words.set(idx[0], "$c");
			result.words.set(idx[1], "$c");
			return result;
		}
		return null;
	}
	
	public void processString(String input) {
		String str = tagger.tagString(input.replace("?", " ? "));
		StringTokenizer tokenizer = new StringTokenizer(str);
		List<String> wordList = new LinkedList<String>();
		List<String> tagList = new LinkedList<String>();
		while (tokenizer.hasMoreTokens()) {
			String[] token = tokenizer.nextToken().split("_");
			if(token[0].equalsIgnoreCase("vrs") || token[0].equalsIgnoreCase("vs") || token[0].equalsIgnoreCase("vs.") || token[0].equalsIgnoreCase("versus"))
			{
				wordList.add("versus");
				tagList.add("CC");
				vs=true;
			}
			else if(token[1].equals("FW"))
			{
				wordList.add(token[0].toLowerCase());
				tagList.add("NN");
			}
			else
			{
				wordList.add(token[0].toLowerCase());
				tagList.add(token[1]);
			}
		}
		phraseChunker.phraseChuncker(wordList, tagList);
		phraseChunker.TrimNounTags();
		words = phraseChunker.getWordListAfterPhraseChunking();
		tags = phraseChunker.getTagListAfterPhraseChunking();
		
		comparatorExtractor = new ComparatorExtractor(words, tags);
		comparatorExtractor.ExtractComparators();
		
		comparators = comparatorExtractor.getComparators();
		//System.out.println(comparators[0] + " and " + comparators[1]);
		//totalComparatorSet = comparatorExtractor.getAdditionalComparators();
		//System.out.println(wordList);
		//System.out.println(tagList);
	}
	
	public Pair<String,String> compareQuestionWithPattern(IndicativeExtractorPattern iep)
	{
		String result[] = new String[2];
		
		int aIdx = 0;
		int bIdx = 0;
		
		int aLen = words.size();
		int bLen = iep.words.size();
		
		int prevA = 0;
		int prevFlag = 0;
		int flag = 0;
		
		String aWord = "";
		String aTag = "";
		String bWord = "";
		String bTag = "";
		
		//System.out.println(aLen + " " + bLen);
		
		while(aLen>=bLen && aIdx<aLen && bIdx<bLen) {
			aWord = words.get(aIdx);
			aTag = tags.get(aIdx);
			bWord = iep.words.get(bIdx);
			bTag = iep.tags.get(bIdx);
			if((aTag.equals(bTag) && !aTag.equals("CC")) || aWord.equals(bWord)) {
				if(prevFlag==0) {
					prevA = aIdx + 1;
					prevFlag = 1;
				}
				if(bWord.equals("$c") && aTag.startsWith("NN")) {
					result[flag] = aWord;
					flag++;
				}
				bIdx++;
			}
			else {
				if(prevFlag!=0) {
					prevFlag = 0;
					aIdx = prevA;
					bIdx = 0;
					flag = 0;
				}
			}
			aIdx++;
		}
		//System.out.println(result[0] + " " + result[1]);
		if(result[1]==null) {
			pattern = false;
			result[0] = result[1] = null;
		}
		else {
			pattern=true;
		}
		Pair<String, String> p = new Pair<String, String>(result[0], result[1]);
		//System.out.println(p.x + " " + p.y);
		return p;
	}
	
	public String toString() {
		String op = "";
		for(int i = 0; i < this.words.size(); i++)
			op += this.words.get(i) + " ";
		
		return op.trim();
	}
	
	public static void main(String args[]) throws Exception {
		IndicativeExtractorPattern iep = new IndicativeExtractorPattern("");
		IndicativeExtractorPattern iep1 = new IndicativeExtractorPattern("difference between $c and $c for gaming ?");
		//System.out.println(iep.words);
		//System.out.println(iep.tags);
		System.out.println(iep.comparators[0] + " and " + iep.comparators[1]);
		//System.out.println(iep1.words);
		//System.out.println(iep1.tags);
		
		//System.out.println(iep.compareQuestionWithPattern(iep1));
	}

}
