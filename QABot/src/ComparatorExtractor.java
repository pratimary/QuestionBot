import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/****
 * 
 * Algorithm - Weakly supervised method for comparator mining
 *
 */
public class ComparatorExtractor {
	private List<Integer> NounIndexSet = new ArrayList<Integer>();
	private List<Integer> CCTagIndexSet = new ArrayList<Integer>();
	private List<String> words = new ArrayList<String>();
	private List<String> tags = new ArrayList<String>();
	private Set<Integer> additionalComparators = new HashSet<Integer>();
	private String[] comparators = new String[2];
	private boolean whType = false;
	private boolean jjrType = false;
	private boolean ruleBased = false;
	private boolean diffType = false;
	
	public ComparatorExtractor(List<String> w, List<String> t) {
		words = w;
		tags = t;
	}

	public void ExtractComparators() {
		String prevTag = "";
		String prevWord = "";
		String currentTag = "";
		String currentWord = "";
		Iterator<String> wordIterator = words.iterator();
		Iterator<String> tagIterator = tags.iterator();
		
		int ccIdx = -1;
		int jjrIdx = -1;
		int idx = 0;
		
		while(tagIterator.hasNext()) {
			currentTag = tagIterator.next();
			currentWord = wordIterator.next();
			if(currentTag.equals(",") && prevTag.startsWith("NN")) {
				additionalComparators.add(idx-1);
			}
			if(currentTag.equals("CC") && prevTag.startsWith("NN")) {
				additionalComparators.add(idx-1);
			}
			else if(currentTag.equals("IN") && !currentWord.equals("for") && (prevTag.startsWith("JJR") || prevTag.equals("RBR"))) {
				if(NounIndexSet.size()>0) {
					jjrType = true;
					jjrIdx = idx-1;
				}
			}
			
			if(currentTag.startsWith("W") || currentWord.equals("?"))
				whType = true;
			else if(currentWord.matches(".*diff.*") || currentWord.matches(".*comp.*") )
				diffType = true;
			else if(currentTag.startsWith("NN") && !currentWord.matches(".*thing.*"))
				NounIndexSet.add(idx);
			else if(currentTag.equals("CC")) {
				if(NounIndexSet.size()>0)
					CCTagIndexSet.add(idx);
			}
			prevTag = currentTag;
			prevWord = currentWord;
			idx++;
		}
		if(NounIndexSet.size()>1 && CCTagIndexSet.size()>0) {
			Iterator<Integer> CCTagSetIterator = CCTagIndexSet.iterator();
			int flag = 0;
			while(CCTagSetIterator.hasNext()) {
				Iterator<Integer> NNi = NounIndexSet.iterator();
				int[] i = {NNi.next(),NNi.next()};
				ccIdx = CCTagSetIterator.next();
				while(true) {
					if(ccIdx>i[0] && ccIdx<i[1]) {
						if(words.get(ccIdx).equals("versus")) {
							if(flag!=1) {
								flag = 1;
								comparators[0] = words.get(i[0]);
								comparators[1] = words.get(i[1]);
							}
							else {
								additionalComparators.add(words.indexOf(comparators[0]));
								additionalComparators.add(words.indexOf(comparators[1]));
								comparators[0] = words.get(i[0]);
								comparators[1] = words.get(i[1]);
							}
							break;
						}
						else if(diffType==true) {
							if(flag!=1) {
								flag = 1;
								comparators[0] = words.get(i[0]);
								comparators[1] = words.get(i[1]);
							}
							else {
								additionalComparators.add(words.indexOf(comparators[0]));
								additionalComparators.add(words.indexOf(comparators[1]));
							}
							break;
						}
						else if(((ccIdx-i[0]<=3 && i[1]-ccIdx<=2 && whType==true) || tags.size()<=6) && words.get(ccIdx).equals("or")) {
							if(flag!=1) {
								flag = 1;
								comparators[0] = words.get(i[0]);
								comparators[1] = words.get(i[1]);
							}
							else {
								additionalComparators.add(words.indexOf(comparators[0]));
								additionalComparators.add(words.indexOf(comparators[1]));
							}
							break;
						}
						else
							break;
					}
					else if(NNi.hasNext()) {
						i[0] = i[1];
						i[1] = NNi.next();
					}
					else
						break;
				}
			}
		}
		if(NounIndexSet.size()>1 && jjrType==true && comparators[0]==null) {
			Iterator<Integer> NNIterator = NounIndexSet.iterator();
			int[] i = {NNIterator.next(),NNIterator.next()};
			
			while(true) {
				if(jjrIdx>i[0] && jjrIdx<i[1]) {
					comparators[0] = words.get(i[0]);
					comparators[1] = words.get(i[1]);
					break;
				}
				else if(NNIterator.hasNext()) {
					i[0] = i[1];
					i[1] = NNIterator.next();
				}
				else
					break;
			}
		}
		if(comparators[1]!=null && comparators[0]!=null) {
			ruleBased = true;
		}
	}
	
	public String[] getComparators() {
		return comparators;
	}
	
	public Set<String> getAdditionalComparators() {
		Set<String> overall = new HashSet<String>();
		for(Integer i : additionalComparators) {
			overall.add(words.get(i));
		}
		
		if(comparators[0]!=null) overall.add(comparators[0]);
		if(comparators[1]!=null) overall.add(comparators[1]);
		
		return overall;
	}
}
