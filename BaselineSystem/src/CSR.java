
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSR {
    public static List preprocess(String TaggedQuestion,String filename)
    {
    	PrintWriter tagseq=null;
    	try {
			tagseq = new PrintWriter(new BufferedWriter(new FileWriter("dataset\\"+filename,true)));
			List tag_seq= new ArrayList();
			String seq="";
			String[] words= TaggedQuestion.split(" ");
			for(String word:words)
			{
				String[] tags=word.split("_");
				tag_seq.add(tags[1]);
				seq=seq+tags[1]+" ";
			}
			tagseq.println(seq);
			return tag_seq;

			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally{
    		tagseq.close();
    	}
		return null;
    	
    	
    }
    public static String preprocess(String question)
    {
    	PosTagger pt = new PosTagger(question);
		String TaggedText = pt.PosTag();
		String seq="";
		String[] words= TaggedText.split(" ");
		for(String word:words)
		{
			String[] tags=word.split("_");
			seq=seq+tags[1]+" ";
		}
		return seq.trim();
    }
	public static void filter(List keywords,List<String> taggedtext)
	{
		int i = 0;
		while (i < taggedtext.size()) {
			if(keywords.contains(taggedtext.get(i)))
					{
				      createNgram(taggedtext,i,3);
					}
			i++;
		}
		
	}
	
	public static void createNgram(List<String> taggedtext,int matchIndex,int N) 
	{
		String seq="";
		PrintWriter tagseq=null;
		//building left side
		if(matchIndex < N)
		{
			for(int i=0;i<=matchIndex;i++)
			{
				seq=seq+taggedtext.get(i)+" ";
			}
		}
		else
		{
			for(int i=matchIndex-N;i<=matchIndex;i++)
			{
				seq=seq+taggedtext.get(i)+" ";
			}
		}
		
		//building right side
		if(matchIndex> (taggedtext.size()-N-1))
		{
			for(int i=matchIndex+1;i<=taggedtext.size()-1;i++)
			{
				seq=seq+taggedtext.get(i)+" ";
			}
		}
		else
		{
			for(int i=matchIndex+1;i<=matchIndex+N;i++)
			{
				seq=seq+taggedtext.get(i)+" ";
			}
		}
		
		try {
			tagseq = new PrintWriter(new BufferedWriter(new FileWriter("dataset\\derivedPatterns.txt",true)));
			tagseq.println(seq.trim());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
    		tagseq.close();
    	}
		
	   System.out.println(seq.trim());
	}
	
	public static List<Feature> createDataTable(Map<String,String> dictionary) throws IOException
	{
		
		BufferedReader patterns=null;
		BufferedReader TS_tags=null;
		BufferedReader TS_ans=null;
    	try {
    		patterns = new BufferedReader(new FileReader("dataset\\derivedPatterns.txt"));
    		TS_tags = new BufferedReader(new FileReader("dataset\\trainingset_tags.txt"));
    		TS_ans = new BufferedReader(new FileReader("dataset\\trainingset_ans.txt"));
			List<Feature> pattern_seq= new ArrayList();
			List<String> tagseq= new ArrayList();
			List<String> ans= new ArrayList();
			String line;
			int i=0;
			while((line = TS_tags.readLine()) != null)
            {
				line=line.replace(".", "");
				tagseq.add(line.trim());
				
            }
			while((line = TS_ans.readLine()) != null)
            {
				
				ans.add(line.trim());
				
            }
			while((line = patterns.readLine()) != null)
            {
				line=line.replace(".", "");
				pattern_seq.add(new Feature(line.trim()));
				//System.out.println(line);
            }
			System.out.println(tagseq.size());
			System.out.println(ans.size());
			
			for (String s:tagseq)
			{
			   //System.out.println(entry.getKey() + "/" + entry.getValue());
			    
			    for(Feature f : pattern_seq)
			    {
			    	//f.feature_name = f.feature_name.replace(" ", ".*");
			    	//if((f.feature_name).contains("VBZ NNP JJR IN NNP"))
			    		//System.out.println(f.feature_name);
			    	String key=s;
			    	String feature=f.feature_name;
			    	String cls=ans.get(i);
			    	if(key.contains(feature))
			    	{
			    		//System.out.println("match:"+feature);
			    		f.feature_count++;
			    		if(ans.get(i).equals("N"))
			    		{
			    			//System.out.println("N");
			    			
			    			f.negative_cnt++;
			    			//System.out.println("------------");
			    			//System.out.println(f.feature_name);
			    			//System.out.println(entry.getKey());
			    			//System.out.println(ans.get(i)+f.negative_cnt);
			    		}
			    		else
			    		{
			    			//System.out.println('C');
			    			f.positive_cnt++;
			    			//System.out.println(f.feature_name);
			    			//System.out.println(entry.getKey());
			    			//System.out.println(ans.get(i)+f.positive_cnt);
			    		}
			    		
			    	}
			    	else
			    	{
			    		//System.out.println("no match");
			    	}
			    	
			    	
			    }
			    i++;
			}
			
			return pattern_seq;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally{
    		patterns.close();
    	}
		return null;
	}
	
	public static void NB_result(List<Feature> featureTable,float total_c,float total_n,String input)
	{
		String Questiontags=preprocess(input.trim());
		
		float P_c=total_c/(total_c+total_n);
		float P_n=total_n/(total_c+total_n);
		float P_input_c=P_c;
		float P_input_n=P_n;
		boolean hasPattern=false;
		for(Feature f:featureTable)
		{
			//System.out.println(f.feature_name);
			//System.out.println(f.positive_cnt);
			//System.out.println(f.negative_cnt);
			String ftr=f.feature_name.replace(".", "").trim();
			Questiontags=Questiontags.replace(".","").trim();
			//System.out.println(Questiontags);
			if(Questiontags.contains(ftr))
			{
				System.out.println(total_n);
				System.out.println(total_c);
				hasPattern=true;
				
				P_input_c*=f.getPositiveLikelihood(total_c);
				P_input_n*=f.getNegativeLikelihood(total_n);
				//System.out.println(P_input_c);
				//System.out.println(f.getPositiveLikelihood(total_c));
			}
		}
		
		if(hasPattern)
		{
			if(P_input_c>P_input_n)
				System.out.println(input+":"+"Comparative");
			else
				System.out.println(input+":"+"Non-Comparative");
		}
		else
			System.out.println(input+":"+"Non-Comparative");
		
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(new FileReader("dataset\\Questions.txt"));
		BufferedReader trainingset = new BufferedReader(new FileReader("dataset\\trainingset.txt"));
		BufferedReader trainingset_ans = new BufferedReader(new FileReader("dataset\\trainingset_ans.txt"));
		BufferedReader keys = new BufferedReader(new FileReader("dataset\\KeywordTag.txt"));
		BufferedReader training_tags = new BufferedReader(new FileReader("dataset\\trainingset_tags.txt"));
		
		PrintWriter ngrams = new PrintWriter("dataset\\ngrams.txt");
		Map<String, String> dictionary = new HashMap<String, String>();
		PosTagger pt;
		List keywords= new ArrayList();
		List trainingsetAns=new ArrayList();
		String line;
		String key;
		float total_C=0;
		float total_N=0;
		boolean do_processing=false;
		
		while((line =trainingset_ans.readLine())!=null)
		{
			trainingsetAns.add(line.trim());
		}
		
		total_C=Collections.frequency(trainingsetAns, "C");
		total_N=Collections.frequency(trainingsetAns, "N");
		System.out.println(total_N);
		System.out.println(total_C);
		if(do_processing)
		{
			while((key=keys.readLine())!= null)
			{
				keywords.add(key);
			}
			
			while((line = in.readLine()) != null)
			{
			    //System.out.println(line);
			    pt = new PosTagger(line);
				String TaggedText = pt.PosTag();
				ngrams.println(TaggedText);
				List tagseq=preprocess(TaggedText,"tagseq.txt");
				
				if(tagseq!=null)
				{
					filter(keywords,tagseq);
				}
				
			}
			
			while((line = trainingset.readLine()) != null)
			{
			    //System.out.println(line);
			    pt = new PosTagger(line);
				String TaggedText = pt.PosTag();
				ngrams.println(TaggedText);
				List<String> tagseq=preprocess(TaggedText,"trainingset_tags.txt");
				String seq="";
				for(int i=0;i<tagseq.size();i++)
				{
					seq=seq+tagseq.get(i)+" ";
					dictionary.put(seq.trim(),(String) trainingsetAns.get(i));
				}
				
				
			}
			
			
		}
		else
		{
			
			
			int i=0;
			while((line =training_tags.readLine())!=null)
			{
				
				dictionary.put(line.trim(),(String) trainingsetAns.get(i));
				
				
				i++;
			}
			
		}
		
		NB_result(createDataTable(dictionary),total_C,total_N,"is Ram better than Seeta?");
		
		
	}

}
