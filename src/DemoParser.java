
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;

public class DemoParser {
	public static final String 	CMD_YOU 			= "Player",
								CMD_TEAMMATE		= "COSTUME",
								CMD_MODEL			= "NPC",
								CMD_POSITION 		= "POS", 
								CMD_CREATEOBJ 		= "NEW", 
								CMD_DESTROY 		= "DEL",
								CMD_EFFECT			= "FX",
								CMD_HITPOINTS		= "HP",
								CMD_ANIMATION		= "MOV",
								OBJ_CAMERA			= "CAM",
								OBJ_SKY				= "SKYFILE";
	public static final String 	FX_GLOWIE			= "GENERIC/MISSIONOBJECTIVE.FX",
								FX_DESRUCT_OBJ		= "GENERIC/IMDESTRUCTIBLE/IMDESTRUCTIBLE.FX",
								FX_TRANSPARENT		= "OBJECTIVEOBJECTFX/GHOSTED.FX",
								FX_NICTIGUN			= "WEAPONS/NICTUSHUNTERRIFLE.FX",
								MOV_HOSTAGE			= "FEARSTUN",
								MOV_HOSTAGE_2		= "FEAR_A",
								MOV_NPC_IGNORE		= "FEAR_RUNCYCLE",
								MOV_NPC_IGNORE_2	= "F_RUNCYCLE",
								MOV_CAPTURE			= "SPELLCASTFEAR",
								MOV_CAPTURE_2		= "CAPTURED_PENELOPE_CYCLE",
								NPC_CYST			= "Shadow_Crystal", //NPC command
								NPC_NICTUS			= "Warshade_Extraction", //Unbound Nictus, Greater unbound nictus
								NPC_VOID			= "Nictus_Hunter",
								NPC_DWARF			= "Kheldian_NPC_Dwarf",
								NPC_NOVA			= "Kheldian_NPC_Nova",
								NPC_HENCHMEN		= "Mastermind_",
								NPC_HENCHMEN_2		= "Necromancy_",
								NAME_QUANTUM		= "Quantum",
								HP_DEAD				= "0.00";

	public HashMap<Integer, Entity> entityList;
	public Object[] entityKeys;
	public String playerObjectID;
	public int	minX = Integer.MAX_VALUE, //setting these to the opposite so that we can find the appropriate min and max for this demo 
				maxX = Integer.MIN_VALUE,
				minY = Integer.MAX_VALUE,
				maxY = Integer.MIN_VALUE;
	
	@SuppressWarnings("unchecked")
	DemoParser(String fileName) {
		int totalTokens;
		float posXAxis, posYAxis, posZAxis;
		
		BufferedReader br;
		Entity entity;
		String demoLine, timing, objID, cmd, entityName;
		StringTokenizer st;
		Object[] keyList;
		
		entityList = new HashMap<Integer, Entity>();
		
		if (fileName == null)
			return;
		
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("Unable to read file: "+ fileName);
			return;
		} 
		try {
			while( ( demoLine = br.readLine( )) != null ) {
				st = new StringTokenizer(demoLine);
				totalTokens = st.countTokens();
				
				if (totalTokens < 3) {
					System.out.println("Malformed Line: "+ demoLine);
					continue;
				}
				
				timing	= st.nextToken(); //From the Demo Format guides, the first line is a timing code
				objID	= st.nextToken(); //The ObjectID is the second token
				cmd		= st.nextToken(); //Demo Command
				
				try { //any object IDs that we care about will be numbers.  use this to filter them out
					Integer.parseInt(objID);
				} catch (NumberFormatException e) {
					continue;
				}
				
				entity = getEntity( objID );
				if ( entity.entityName.equals("Dr") && entity.entityType == Entity.TYPE_NONE )
					entity.setEntityType(Entity.TYPE_DOOR);
	
				if ( cmd.equals(CMD_CREATEOBJ) ) {
					entityName = getEntityName(st);
					
					entity.setEntityName(entityName);
				} else if ( cmd.equals(CMD_POSITION) ) {
					if (totalTokens < 6) { 
						System.out.println("Bad Coordinates: "+demoLine);
						continue;
					}
					posXAxis = Float.parseFloat(st.nextToken());
					posZAxis = Float.parseFloat(st.nextToken());
					posYAxis = Float.parseFloat(st.nextToken());
					
					if (posXAxis > maxX) maxX = (int) posXAxis;
					if (posXAxis < minX) minX = (int) posXAxis;
					if (posYAxis > maxY) maxY = (int) posYAxis;
					if (posYAxis < minY) minY = (int) posYAxis;
					
					entity.setCoordinates(posXAxis, posZAxis, posYAxis);
				} else if ( cmd.equals( CMD_YOU ) ) {
					
					entity.setEntityType( Entity.TYPE_PLAYER );
					playerObjectID = objID;
					
				} else if (cmd.equals( CMD_EFFECT )) {
					//Read the attributes, looking for possible glowies
					if (demoLine.indexOf(FX_GLOWIE) != -1) { //String wasn't found it is -1
						entity.setEntityType(Entity.TYPE_GLOWIE);
					} else if (demoLine.indexOf(FX_TRANSPARENT) != -1 ) {
						entity.setEntityType(Entity.TYPE_ANTIGLOWIE);
					} else if (demoLine.indexOf(FX_NICTIGUN) != -1) {
						entity.setEntityType(Entity.TYPE_QUANTUM);
					} else if (demoLine.indexOf(FX_DESRUCT_OBJ) != -1) {
						entity.setEntityType(Entity.TYPE_DESTRUCT);
					}
					
				} else if (cmd.equals( CMD_HITPOINTS )) {
					if (totalTokens < 4) {
						System.out.println("Bad Hitpoints Line: "+demoLine);
						continue;
					}
					if (st.nextToken().equals( HP_DEAD ))
						entity.makeDead();
				} else if (cmd.equals(CMD_TEAMMATE)) {
					if (entity.entityType == Entity.TYPE_NONE)
						entity.setEntityType(Entity.TYPE_TEAMMATE);
				} else if (cmd.equals(CMD_ANIMATION)) {
					if (totalTokens < 4) {
						System.out.println("Bad Animations Line: "+demoLine);
						continue;
					}
					if (demoLine.indexOf(MOV_HOSTAGE) != -1)
						entity.setEntityType(Entity.TYPE_HOSTAGE);
					else if (demoLine.indexOf(MOV_HOSTAGE_2) != -1)
						entity.setEntityType(Entity.TYPE_HOSTAGE);
					else if (demoLine.indexOf(MOV_CAPTURE) != -1)
						entity.setEntityType(Entity.TYPE_CAPTIVE);
					else if (demoLine.indexOf(MOV_CAPTURE_2) != -1)
						entity.setEntityType(Entity.TYPE_CAPTIVE);
					else if (demoLine.indexOf(MOV_NPC_IGNORE) != -1)
						entity.setEntityType(Entity.TYPE_NPC_IGNORE);
					else if (demoLine.indexOf(MOV_NPC_IGNORE_2) != -1)
						entity.setEntityType(Entity.TYPE_NPC_IGNORE);
						
				} else if (cmd.equals(CMD_MODEL)) {
					if (demoLine.indexOf(NPC_CYST) != -1)
						entity.setEntityType(Entity.TYPE_QUANTUM);
					else if (demoLine.indexOf(NPC_NICTUS) != -1)
						entity.setEntityType(Entity.TYPE_QUANTUM);
					else if (demoLine.indexOf(NPC_VOID) != -1)
						entity.setEntityType(Entity.TYPE_QUANTUM);
					else if (demoLine.indexOf(NPC_DWARF) != -1)
						entity.setEntityType(Entity.TYPE_QUANTUM);
					else if (demoLine.indexOf(NPC_NOVA) != -1)
						entity.setEntityType(Entity.TYPE_QUANTUM);
					else if (demoLine.indexOf(NPC_HENCHMEN) != -1)
						entity.setEntityType(Entity.TYPE_HENCHMAN);
					else if (demoLine.indexOf(NPC_HENCHMEN_2) != -1)
						entity.setEntityType(Entity.TYPE_HENCHMAN);
				}

				entityList.put(entity.objectID, entity);
			}
		} catch (IOException e) {
			System.out.println("Error reading line");
		}
		
		//Loop through the list looking for Quantums... I wish I knew a better way
		keyList = entityList.keySet().toArray();
		for (int i = 0; i < keyList.length; i++) {
			entity = entityList.get(keyList[i]);
			if (entity.entityType != Entity.TYPE_NONE) {
				continue;
			}
			if (entity.entityName.indexOf(NAME_QUANTUM) != -1) {
				entity.setEntityType(Entity.TYPE_QUANTUM);
			}
		}
		
		entityKeys = entityList.keySet().toArray();
		
		Arrays.sort(entityKeys, new Comparator() {
			public int compare(Object o1, Object o2) {
				Entity e1 = entityList.get(o1);
				Entity e2 = entityList.get(o2);
				
				int compareType = new Integer(e1.entityType).compareTo(new Integer(e2.entityType));

				if (compareType == 0) {
					int compareName = e1.entityName.compareTo(e2.entityName);
					if (compareName == 0) {
						return (e1.compareTo(e2));
					} else {
						return compareName;
					}
				} else {
					return compareType;
				}
			}
		});
	}
	
	public String getEntityName( StringTokenizer st) {
		String output = ""; //initialize Exported line
		
		while( st.hasMoreTokens() ) {
			output = output.concat(" " + st.nextToken());
		}
		
		if ( output.startsWith(" \"") && output.endsWith("\"") )
			output = output.substring(2, (output.length() - 1) );

		output = output.trim();	
		return output;
	}
	
	
	public Entity getEntity( String objectID ) {
		Entity output = entityList.get(Integer.parseInt(objectID));
		
		if (output == null) 
			output = new Entity(Integer.parseInt(objectID));
		
		return output;	
	}
}
