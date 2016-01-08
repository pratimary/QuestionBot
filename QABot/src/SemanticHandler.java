import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.stanford.nlp.util.Sets;

public class SemanticHandler {
	
	Set<String> getDomain(String query) throws Exception {
		
		String service_url = "https://www.googleapis.com/freebase/v1/search";
		String httpURL = service_url + "?query=" + URLEncoder.encode(query, "UTF-8");// 

		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(httpURL));

		JsonParser parser = new JsonParser();
		JsonObject json_data = (JsonObject) parser.parse(EntityUtils.toString(response.getEntity()));
		System.out.println(json_data);
		JsonArray results = (JsonArray) json_data.get("result");

		Set<String> op=new HashSet<String>();
		
		if (results != null) {
			for (Object obj : results) {
				Object res = ((JsonObject) obj).get("notable");
				if (res != null) {
					Object t = ((JsonObject) res).get("name");
					op.add(t.toString().replace("\"", ""));
				}
			}
		}
		return op;
	}
	
	Set<String> commonDomain(String str1, String str2) throws Exception
	{
		Set<String> domain1 = getDomain(str1);
		//System.out.println(domain1);
		Set<String> domain2 = getDomain(str2);
		//System.out.println(domain2);
		Set<String> common = Sets.intersection(domain1, domain2);
		return common;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new SemanticHandler().commonDomain("Dell", "HP"));
	}

}
