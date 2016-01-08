
public class Feature {

	public String feature_name;
	public float feature_count;
	public float positive_cnt;
	public float negative_cnt;
	Feature(String name)
	{
		feature_name=name;
		feature_count=0;
		positive_cnt=0;
		negative_cnt=0;
	}
	public float getPositiveLikelihood(float total_c)
	{
		return positive_cnt/total_c;
	}
	
	public float getNegativeLikelihood(float total)
	{
		return negative_cnt/total;
		
	}
	
	public String toString()
	{
		
		return feature_name;
		
	}
}
