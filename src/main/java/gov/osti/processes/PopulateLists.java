package gov.osti.processes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.osti.pojo.AuthoritiesObject;
import gov.osti.pojo.LabelValuePair;
import gov.osti.pojo.StaticLists;

import javax.servlet.http.HttpServletResponse;

public class PopulateLists {
	
    private static final ObjectMapper mapper = new ObjectMapper();
	
	
	public static void main(String[] args) {
		try {
			new PopulateLists(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected PopulateLists(String[] args) throws IOException {
	String staticsFile = args[0];
    StaticLists staticLists = new StaticLists();
    AuthoritiesObject[] countries = mapper.readValue(fetch("https://www.osti.gov/elink/api/authorities/simple/countries-list"), AuthoritiesObject[].class);
    AuthoritiesObject[] sponsorOrgs = mapper.readValue(fetch("https://www.osti.gov/elink/api/authorities/sponsor/sponsor-org-list"), AuthoritiesObject[].class);
    AuthoritiesObject[] researchOrgs = mapper.readValue(fetch("https://www.osti.gov/elink/api/authorities/research/orig-research-org-list"), AuthoritiesObject[].class);
    String[] affiliations = mapper.readValue(fetch("https://www.osti.gov/elink/api/authorities/affiliations/affiliations-list"), String[].class);
    staticLists.setCountries(convertToLabelValuePairs(countries));
    staticLists.setSponsorOrgs(convertToLabelValuePairs(sponsorOrgs));
    staticLists.setResearchOrgs(convertToLabelValuePairs(researchOrgs));
    staticLists.setAffiliations(convertToLabelValuePairs(affiliations));
	
    mapper.writeValue(new File(staticsFile), staticLists);
	}
	
	private ArrayList<LabelValuePair> convertToLabelValuePairs(AuthoritiesObject[] arr) {
		ArrayList<LabelValuePair> lvpArr = new ArrayList<>();	
		for (AuthoritiesObject ao : arr) {
			lvpArr.add(new LabelValuePair(ao.getName(), ao.getName()));
		}		
		return lvpArr;		
	}
	
	private ArrayList<LabelValuePair> convertToLabelValuePairs(String[] arr) {
		ArrayList<LabelValuePair> lvpArr = new ArrayList<>();		
		for (String aff : arr) {
			lvpArr.add(new LabelValuePair(aff, aff));
		}		
		return lvpArr;	
	}
	
    /**
     * Retrieve just the String content from a given HttpGet request.
     * 
     * @param get the URL to determine where to GET from
     * @return String contents of the results
     * @throws IOException on IO errors
     */
    protected String fetch(String url) throws IOException {
    	HttpGet get = new HttpGet(url);
        // set some reasonable default timeouts
        RequestConfig rc = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
        // create an HTTP client to request through
        CloseableHttpClient hc = 
                HttpClientBuilder
                .create()
                .setDefaultRequestConfig(rc)
                .build();
        
        try {
            // only return if response is OK
            HttpResponse response = hc.execute(get);
            return ( HttpServletResponse.SC_OK==response.getStatusLine().getStatusCode()) ?
                    EntityUtils.toString(response.getEntity()) :
                    "";
        } finally {
            hc.close();
        }
    }

}
