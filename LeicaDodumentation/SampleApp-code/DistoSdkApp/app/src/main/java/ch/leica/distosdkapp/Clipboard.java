package ch.leica.distosdkapp;

/**
 * This class is used to hold and transfer informationActivityData object between activities.
 */
public enum Clipboard {//Creates a singleton class
	INSTANCE;

	public InformationActivityData informationActivityData;

	public InformationActivityData getInformationActivityData() {
		if (informationActivityData == null){
			informationActivityData = new InformationActivityData(null, null,null);
		}
		return informationActivityData;
	}

	public void setInformationActivityData(InformationActivityData informationActivityData) {
		this.informationActivityData = informationActivityData;
	}
}