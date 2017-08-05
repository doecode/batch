package gov.osti.pojo;

import java.util.ArrayList;

public class StaticLists {

	private ArrayList<LabelValuePair> countries;
	private ArrayList<LabelValuePair> sponsorOrgs;
	private ArrayList<LabelValuePair> researchOrgs;
	private ArrayList<LabelValuePair> affiliations;
	
	
	public ArrayList<LabelValuePair> getCountries() {
		return countries;
	}
	public void setCountries(ArrayList<LabelValuePair> countries) {
		this.countries = countries;
	}
	public ArrayList<LabelValuePair> getSponsorOrgs() {
		return sponsorOrgs;
	}
	public void setSponsorOrgs(ArrayList<LabelValuePair> sponsorOrgs) {
		this.sponsorOrgs = sponsorOrgs;
	}
	public ArrayList<LabelValuePair> getResearchOrgs() {
		return researchOrgs;
	}
	public void setResearchOrgs(ArrayList<LabelValuePair> researchOrgs) {
		this.researchOrgs = researchOrgs;
	}
	public ArrayList<LabelValuePair> getAffiliations() {
		return affiliations;
	}
	public void setAffiliations(ArrayList<LabelValuePair> affiliations) {
		this.affiliations = affiliations;
	}
	
	
	
}
