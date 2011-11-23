
public class Entity {
	public static final String[] TYPE_LIST = {	"You", 						//key 0
												"Player / Custom Critter",	//key 1
												"Glowie",					//key 2
												"Glowie - Destroyable",		//key 3
												"Glowie - Invisible",		//key 4
												"Sacrifice",				//key 5
												"NPC Fear",					//key 6
												"Nictus",					//key 7
												"Door",						//key 8
												"Puddle",					//key 9
												"NPC Ignore",				//key 10
												"MasterMind Henchman"		//key 11
											};
	
	public static final int	TYPE_PLAYER		= 0, 					//"You"
							TYPE_TEAMMATE	= 1, 					//"Player / Custom Critter"
							TYPE_GLOWIE 	= 2, 					//"Glowie"
							TYPE_DESTRUCT	= 3, 					//"Glowie - Destroyable"
							TYPE_ANTIGLOWIE	= 4, 					//"Glowie - Invisible"
							TYPE_CAPTIVE	= 5, 					//"Sacrifice"
							TYPE_HOSTAGE	= 6, 					//"NPC Fear"
							TYPE_QUANTUM	= 7, 					//"Nictus"
							TYPE_DOOR		= 8, 					//"Door"
							TYPE_PUDDLE		= 9, 					//"Puddle"
							TYPE_NPC_IGNORE = 10,					//"Just an NPC running araound
							TYPE_HENCHMAN	= 11,					//Mastermind henchmen
							TYPE_OTHER		= 12,					//All other types
							TYPE_NONE		= 201;//Integer.MAX_VALUE;

	public int objectID, entityType;
	public String entityName;
	public boolean entityIsDead = false;
	
	public float 	posXAxis = 0.0F, 
					posYAxis = 0.0F,
					posZAxis = 0.0F;
	
	Entity(Integer objectID) {
		this.objectID = objectID;
		this.entityName = "";
		this.entityType = TYPE_NONE;		
	}
	Entity(Integer objectID, String entityName) {
		this.objectID	= objectID;
		this.entityName = entityName;
	}
	public void setEntityName(String newEntityName) {
		this.entityName = newEntityName;
	}
	public void setCoordinates(float posXAxis, float posZAxis, float posYAxis) {
		this.posXAxis = posXAxis;
		this.posZAxis = posZAxis;
		this.posYAxis = posYAxis;
	}
	public void setEntityType(int newEntityType) {
		this.entityType = newEntityType;
	}
	public void makeDead() {
		entityIsDead = true;
	}
	public int compareTo(Entity compEntity) {
		if (this.objectID < compEntity.objectID) {
			return -1;
		} else if (this.objectID == compEntity.objectID) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public String getEntityTypeText() {
		if (entityType != TYPE_NONE)
			return TYPE_LIST[entityType];
		else
			return "";
	}
	
	public String toString() {
		return objectID +"-"+ entityName +"\n"+ posXAxis +", "+ posZAxis +", "+ posYAxis ;
	}
}
