import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class BootStrapping {
	
	public static List<IndicativeExtractorPattern> loadDataSet(String DatasetPath) throws Exception
	{
		List<IndicativeExtractorPattern> IEPatterns = new ArrayList<IndicativeExtractorPattern>();
		BufferedReader in = new BufferedReader(new FileReader(DatasetPath));
		String str = null;
		System.out.println("Loading the data set and pre-processing it.........");
		long startTime = System.nanoTime();
		while((str = in.readLine())!=null) {
			IEPatterns.add(new IndicativeExtractorPattern(str));
		}
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("Successfully Done in "+ (duration/1000000000) +"secs...!!!");
		
		return IEPatterns;
	}
	
	public static int checkComparatorPairWithComparators(List<Pair<String,String>> comparators,Pair<String,String> comparatorPair) {
		int n = comparators.size();
		for(int i = 0; i < n; i++)
		{
			if(comparatorPair.equals(comparators.get(i)))
				return i;	
		}
		return -1;
	}
	
	
	
	public static void PatternAndComparatorExtraction() throws Exception {
		List<IndicativeExtractorPattern> data = loadDataSet("dataset/Questions.txt");
		List<IndicativeExtractorPattern> patterns = new ArrayList<IndicativeExtractorPattern>();
		patterns.add(new IndicativeExtractorPattern("$c vs $c"));
		int patternIdx = 0;
		List<Pair<String,String>> comparators = new ArrayList<Pair<String,String>>();

		for(int i = 0; i < data.size(); i++) {
			Pair<String,String> p = data.get(i).compareQuestionWithPattern(patterns.get(patternIdx));
			if(p.x != null) {
				int match = checkComparatorPairWithComparators(comparators,p);
				if(match != -1) {
					p.occurrence = comparators.get(match).occurrence + 1;
					comparators.set(match, p);
				}
				else {
					comparators.add(p);
				}
				data.remove(i);
				i--;
			}
		}
		
		/*for(int i=0;i<comparators.size();i++)
			System.out.println(comparators.get(i).toString()); */
		
		patternIdx = 1;
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("dataset/Patterns.txt", true));
		for(int i = 0; i < comparators.size(); i++) {
			Pair<String,String> p = comparators.get(i);
			for(int j = 0; j < data.size(); j++) {
				IndicativeExtractorPattern iepTemp = data.get(j).GetQuestionUsingComparators(p);
				if(iepTemp != null) {
					p.occurrence++;
					comparators.set(i, p);
					patterns.add(iepTemp);
					bw.write(iepTemp.toString() + "\n");
					data.remove(j);
					j--;
				}
			}
			
			for(int k = patternIdx; k < patterns.size(); k++) {
				for(int x = 0; x < data.size(); x++) {
					Pair<String,String> pMatch = data.get(x).compareQuestionWithPattern(patterns.get(k));
					if(pMatch.x != null) {
						int index = checkComparatorPairWithComparators(comparators,pMatch);
						if(index != -1) {
							pMatch.occurrence = comparators.get(index).occurrence + 1;
							comparators.set(index, pMatch);
						}
						else {
							comparators.add(pMatch);
						}
						data.remove(x);
						x--;
					}
				}
			}
			
			patternIdx = patterns.size();
		} 
		
		for(int i = 0; i < patterns.size(); i++) {
			System.out.println(patterns.get(i).toString());
		}
		
		for(int i = 0; i < comparators.size(); i++) {
			System.out.println(comparators.get(i).toString());
		}
		
		bw.close();
		
		System.out.println("BootStrapping Done!!!!!");
	} 
}
