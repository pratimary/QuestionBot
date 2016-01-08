import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Driver {
	private static BootStrapping bootStrapping = new BootStrapping();
	private static List<Pair<String, String>> comparators = new ArrayList<Pair<String, String>>();
	private static ArrayList<String> compPairs = new ArrayList<String>();
	private static SemanticHandler semanticHandler = new SemanticHandler();
	
	public static void learnPatterns() {
		try {
			bootStrapping.PatternAndComparatorExtraction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void patternMethod() throws Exception {
		System.out.println("PATTERN METHOD:");
		System.out.println();
		System.out.println("COMPARATIVE ENTITY MINING:");
		System.out.println();
		
		List<IndicativeExtractorPattern> dataSet = bootStrapping.loadDataSet("dataset/Questions.txt");
		List<IndicativeExtractorPattern> patternSet = bootStrapping.loadDataSet("dataset/Patterns.txt");
		BufferedReader br = new BufferedReader(new FileReader("dataset/Labels.txt"));
		List<Pair<String,String>> comparatorSet = new ArrayList<Pair<String,String>>();
		
		double truePositive = 0;
		double trueNegative = 0;
		double falsePositive = 0;
		double falseNegative = 0;
		
		String label = "";
		int isComp = 0;
		int compCount = 0;
		int loop = 0;
		
		for(int i=0;i<dataSet.size();i++)
		{
			IndicativeExtractorPattern curQn = dataSet.get(i);
			label = br.readLine();
			if(label.equals("C"))
			{
				isComp=1;
				compCount++;
			}
			for(int j=0;j<patternSet.size();j++)
			{
				IndicativeExtractorPattern currentPattern = patternSet.get(j);
				Pair<String,String> p = curQn.compareQuestionWithPattern(currentPattern);
				if(p.x!=null)
				{
					if(isComp == 1)
						truePositive++;
					else if(isComp == 0)
						falseNegative++;
					comparatorSet.add(p);
					loop=1;
					break;
				}
			}
			if(loop!=1) {
				if(isComp==1)
					trueNegative++;
				else
					falsePositive++;
			}
			isComp=0;
			loop=0;
		}
		double precision = truePositive / (truePositive+falseNegative);
		double recall = (truePositive/(truePositive+trueNegative));
		double FScore = ((2*precision*recall)/(precision+recall));
		
		System.out.println("*************************************");
		System.out.println("Pattern Method Evaluation Results:");
		System.out.println("Number of Comparative Questions found manually in the corpus: "+compCount);
		System.out.println();
		System.out.println("Total No. of Questions: "+ dataSet.size());
		System.out.println("Total Questions Identified as Comparative: "+ (int)(truePositive + falseNegative));
		System.out.println("Irrelevant Questions identified as Comparative: "+ (int)falseNegative);
		System.out.println("Comparative Questions identified Correctly: "+ (int)truePositive);
		System.out.println("Comparative Questions not Identified: "+ (int)trueNegative);
		System.out.println();
		System.out.println("Precision:\t" + precision*100 + "%");
		System.out.println("Recall:\t\t" + recall*100 + "%");
		System.out.println("F-Score:\t" + FScore*100 + "%");
		System.out.println("**************************************");
	}
	
	public static void heuristicsMethod() throws Exception
	{
		System.out.println("HEURISTICS METHOD:");
		System.out.println();
		System.out.println("COMPARATIVE ENTITY MINING:");
		System.out.println();
		
		double relevantP = 0;
		double irrelevant = 0;
		double relevantN = 0;
		
		List<IndicativeExtractorPattern> indicativeExtractorPattern = new ArrayList<IndicativeExtractorPattern>();
		BufferedReader brQuestions = new BufferedReader(new FileReader("dataset/Questions.txt"));
		BufferedReader brLabels = new BufferedReader(new FileReader("dataset/Labels.txt"));
		
		String currentQuestion = null;
		String currentLabel = null;
		
		int questionCount = 0;
		int compCount = 0;
		int comp = 0;
		int isComp = 0;
		while((currentQuestion=brQuestions.readLine()) != null) {
				currentLabel = brLabels.readLine();
				if(currentLabel.equals("C")) {
					isComp=1;
					compCount++;
				}
				indicativeExtractorPattern.add(new IndicativeExtractorPattern(currentQuestion));
				if(indicativeExtractorPattern.get(questionCount).comparators[0] != null) {
					if(isComp == 1) {
						relevantP++;
					}
					else {
						irrelevant++;
					}
					comp++;
				}
				else {
					if(isComp == 1){
						relevantN++;
					}
				}
				isComp = 0;
				questionCount++;
		}
		
		brQuestions.close();
		brLabels.close();
		
		double precision = (relevantP / (relevantP + irrelevant))*100;
		double recall = (relevantP / (relevantP + relevantN))*100;
		double FScore = (2 * precision * recall) / (precision + recall);
		
		System.out.println("**************************************");
		System.out.println("Heuristics Method Evaluation Results:");
		System.out.println("Actual number of Comparative Questions found manually in the question corpus :" + compCount);
		System.out.println();

		System.out.println("Total No. of Questions: " + questionCount);
		System.out.println("Total Questions Identified as Comparative: " + comp);
		System.out.println("Comparative Questions identified Correctly:"+(int)relevantP);
		System.out.println("Comparative Questions not Identified:"+(int)relevantN);
		System.out.println("Irrelevant Questions identified as Comparative:"+(int)irrelevant);
		System.out.println();
		System.out.println("Precision: "+precision + "%");
		System.out.println("Recall: "+recall + "%");
		System.out.println("F-Score: "+FScore + "%");
		System.out.println("**************************************");
	}
	
	public static void extractComparators() throws Exception {
		System.out.println("*******************************************");
		System.out.println("Comparator pair extraction initiated.......");
		BufferedWriter bwP = new BufferedWriter(new FileWriter("dataset/Comparator_P.txt", true));
		BufferedWriter bwH = new BufferedWriter(new FileWriter("dataset/comparator_H.txt", true));
		
		List<IndicativeExtractorPattern> dataSet = bootStrapping.loadDataSet("dataset/Questions.txt");
		List<IndicativeExtractorPattern> patternSet = bootStrapping.loadDataSet("dataset/Patterns.txt");
		
		comparators = new ArrayList<Pair<String,String>>();
		
		for(int i = 0; i < dataSet.size(); i++) {
			IndicativeExtractorPattern iepQ = dataSet.get(i);
			Pair<String, String> p = new Pair<String, String>(iepQ.comparators[0], iepQ.comparators[1]);
			if(p.x != null) {
				int match = bootStrapping.checkComparatorPairWithComparators(comparators, p);
				if(match!=-1) {
					p.occurrence = comparators.get(match).occurrence + 1;
					comparators.set(match, p);
				}
				else {
					comparators.add(p);
				}
			}
		}
		
		for(Pair<String, String> p : comparators) {
			bwH.write(p.x + "_" + p.y+ "_" + p.occurrence + "\n");
		}
		
		List<Pair<String, String>> comparatorsFromPattern = new ArrayList<Pair<String, String>>();
		
		for(int i = 0; i < dataSet.size(); i++) {
			IndicativeExtractorPattern iepQ = dataSet.get(i);
			for(int j=0;j<patternSet.size();j++)
			{
				IndicativeExtractorPattern iepPattern = patternSet.get(j);
				Pair<String,String> pFromPattern = iepQ.compareQuestionWithPattern(iepPattern);
				if(iepQ.pattern==true) {
					int match = bootStrapping.checkComparatorPairWithComparators(comparatorsFromPattern, pFromPattern);
					if(match!=-1) {
						pFromPattern.occurrence = comparators.get(match).occurrence + 1;
						comparatorsFromPattern.set(match, pFromPattern);
					}
					else {
						comparatorsFromPattern.add(pFromPattern);
					}
					break;
				}
			}
		}
		
		for(Pair<String,String> p : comparatorsFromPattern) {
			bwP.write(p.x + "_" + p.y + "_" + p.occurrence + "\n"); 
		}
		
		bwP.close();
		bwH.close();
		System.out.println("Comparator Pair Extraction successfull........");
		System.out.println("**********************************************");
	}
	
	public static ArrayList<String> loadComparators() throws IOException {
		ArrayList<String> compPairList = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new FileReader("dataset/comparator_H.txt"));
		String str = null;
		System.out.println("Loading the data set.........");

		while((str = in.readLine())!=null) {
			String compPair = "";
			String[] sList = str.split("_");
			compPair = sList[0] + "_" + sList[1];
			compPairList.add(compPair);
		}
		return compPairList;
	}
	
	public static ArrayList<String> recommendAlternatives(ArrayList<String> inputComparator) {
		
		ArrayList<String> alternatives = new ArrayList<String>();
		List<String> compList = new ArrayList<String>();
		
		
		
		if(inputComparator.get(0) == null || inputComparator.get(1) == null)
			return alternatives;
		
		for(String p : compPairs) {
			String[] sList = p.split("_");
			if(inputComparator.get(0).equalsIgnoreCase(sList[0]) || inputComparator.get(1).equalsIgnoreCase(sList[0])) {
				alternatives.add(sList[1]);
			}
			if(inputComparator.get(0).equalsIgnoreCase(sList[1]) || inputComparator.get(1).equalsIgnoreCase(sList[1])) {
				alternatives.add(sList[0]);
			}
		}
		
		return alternatives;
	}
	
	
	
	public static void userInputHelper(List<IndicativeExtractorPattern> patterns, BufferedReader br) throws Exception {
		System.out.println("Enter your query:");
		String input = br.readLine();
		IndicativeExtractorPattern ipIEP = new IndicativeExtractorPattern(input);
		ArrayList<String> inputComparator = new ArrayList<String>();
		
		int flag = 0;
		for(int j = 0; j < patterns.size(); j++) {
			IndicativeExtractorPattern currentPattern = patterns.get(j);
			Pair<String,String> temp = ipIEP.compareQuestionWithPattern(currentPattern);
			if((ipIEP.pattern==true || ipIEP.comparators[0]!=null)) {
				inputComparator.add(ipIEP.comparators[0]);
				inputComparator.add(ipIEP.comparators[1]);
				System.out.println("ENTITIES FROM USER INPUT: "+ipIEP.comparators[0]+","+ipIEP.comparators[1]+"\n");
				
				Set<String> common = new HashSet<String>();
				common = semanticHandler.commonDomain(ipIEP.comparators[0], ipIEP.comparators[1]);
				if(common.size() == 0) {
					System.out.println("There is no semantic similarity between between the "
							+ "entities " + ipIEP.comparators[0] + " " + ipIEP.comparators[1]);
					break;
				}
				ArrayList<String> result1 = recommendAlternatives(inputComparator);
				if(result1!=null)
					System.out.println(result1);								
				flag=1;
				break;
			}
		}
		if(flag==0)
			System.out.println("Not a Comparative Question");
	}
	
	public static void userInput() throws Exception {
		System.out.println("Loading the patterns...... Please Wait....");
		System.out.println();
		List<IndicativeExtractorPattern> patterns = bootStrapping.loadDataSet("dataset/Patterns.txt");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		System.out.println();
		System.out.println("Pattern loading Complete....!!!!");
		System.out.println();
		compPairs = loadComparators();
		System.out.println();
		String flag="y";
		while(flag.equalsIgnoreCase("y"))
		{
			userInputHelper(patterns,br);
			System.out.println("Continue? (Y/N)");
			flag=br.readLine();
		}
	}
	
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		/*System.out.println("***************************************************************************");
		System.out.println("Initiating the BootStrapping process to learn the question patterns........");
		learnPatterns();
		System.out.println("Pattern learning done..........");
		System.out.println("***************************************************************************");
		
		patternMethod();
		heuristicsMethod();
		
		extractComparators(); */
		
		userInput();
		System.out.println("Thank you!!!!");
		
	}

}
