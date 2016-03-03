package data;

import java.util.Hashtable;

public class FrameLists {

	private Hashtable<String, Hashtable<String, FrameList>> framelists;
	
	public FrameLists() {
		this.framelists = new Hashtable<String, Hashtable<String,FrameList>>();
	}
	
	/**
	 * 
	 * @param entityName
	 * @param entityFrameList
	 */
	public void addEntityFrameLists(String entityName, Hashtable<String, FrameList> entityFrameLists) {
		this.framelists.put(entityName, entityFrameLists);
	}

	public Hashtable<String, Hashtable<String, FrameList>> getAllFrameLists() {
		return framelists;
	}

	public void setAllFrameLists(Hashtable<String, Hashtable<String, FrameList>> frameLists) {
		this.framelists = frameLists;
	}
	
	public Hashtable<String, FrameList> getFrameLists(String frameListName) {
//		System.out.println("=============================");
//		for(String key : framelists.keySet()){
//			System.out.println("Key " + key);
//			for(String key2 : framelists.get(key).keySet()){
//				System.out.println("   -Key2 " + key2 + " - Animation " + framelists.get(key).get(key2).getId() );
//			}
//		}
//		System.out.println("=============================");
		return framelists.get(frameListName);				
	}
	
}
