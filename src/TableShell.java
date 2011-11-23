
import java.util.ArrayList;
import java.util.Iterator;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.io.File;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class TableShell {
	public Shell tableShell;
	public Rectangle tableShellBounds;
	
	Control[] 	tableObjects;
	DemoParser 	dp;
	Label 		statusBar;
	MapShell 	handler;
	Table		entityTable;
	ArrayList<Integer> checkboxList;
	Button showDoors, showNone, showTeammate,showGlowie, showHenchmen,
		   showHostage,showNictus,showIgnores, showOthers, showCustom, liveUpdate;
	Text customText;
	String customString="";	
	
	final Display display;
	final int time = 1000;
	
	
	//this is the timer that parses the record demo file 
	final Runnable timer = new Runnable () {
		public void run () {
		    handleParseButton();
		    display.timerExec(time, this);
		}
	};
	
	
	Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	
	TableShell(MapShell parent) {
		//Constructor:  Create the basic shell
		handler = parent;
		display = handler.getDisplay();
		tableShell	= new Shell(handler.mapShell, SWT.TITLE | SWT.CLOSE | SWT.RESIZE);
		tableShell.setText(RadarConsts.PROGRAM_NAME +" "+ RadarConsts.VERSION);
		tableShell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				saveWindowPosition();
			}
		});
		checkboxList = new ArrayList<Integer>();
	}
	
	public void setIcons(Image[] icons) {
		tableShell.setImages(icons);
	}
	
	public void initUI() {
		Button		selectFile, parseDemo;
		GridData	gridCell;
		GridLayout 	grid;
		TableColumn col;
		Text 		fileName;
		
		
		grid = new GridLayout();
		grid.numColumns	= 6;
		tableShell.setLayout(grid);
		
		//Display element for the file selected
		gridCell			= new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gridCell.widthHint	= 300;
		fileName 			= new Text(tableShell, SWT.SINGLE);
		fileName.setText(prefs.get(RadarConsts.PREF_KEY_FILENAME, ""));
		fileName.setLayoutData(gridCell);
		
		//File Selection button
		selectFile	= new Button(tableShell, SWT.PUSH);
		selectFile.setText("Load");
		selectFile.setLayoutData(gridCell);
		selectFile.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleFileSelectButton();
			}
		});
		

		// Parse/read button
		parseDemo 	= new Button(tableShell, SWT.PUSH);
		parseDemo.setText("Parse");
		parseDemo.setLayoutData(gridCell);
		parseDemo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleParseButton();
			}
		});
		
		gridCell	= new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		//show doors cehckbox
		showDoors = new Button(tableShell, SWT.CHECK);
		showDoors.setText("Doors");
		showDoors.setSelection(true);
		showDoors.setLayoutData(gridCell);
		
		showDoors.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleCheckButtonList(Entity.TYPE_DOOR);
			}
		});
		
		showTeammate = new Button(tableShell, SWT.CHECK);
		showTeammate.setText("Teammate");
		showTeammate.setSelection(true);
		showTeammate.setLayoutData(gridCell);
		showTeammate.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleCheckButtonList(Entity.TYPE_TEAMMATE);
			}
		});
		
		showGlowie = new Button(tableShell, SWT.CHECK);
		showGlowie.setText("Glowies");
		showGlowie.setSelection(true);
		showGlowie.setLayoutData(gridCell);
		showGlowie.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleCheckButtonList(Entity.TYPE_GLOWIE);
				handleCheckButtonList(Entity.TYPE_DESTRUCT);
				handleCheckButtonList(Entity.TYPE_ANTIGLOWIE);
			}
		});		
		
		showHenchmen = new Button(tableShell, SWT.CHECK);
		showHenchmen.setText("Henchmen");
		showHenchmen.setSelection(true);
		showHenchmen.setLayoutData(gridCell);
		showHenchmen.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleCheckButtonList(Entity.TYPE_HENCHMAN);
			}
		});

		showHostage = new Button(tableShell, SWT.CHECK);
		showHostage.setText("Captives");
		showHostage.setSelection(true);
		showHostage.setLayoutData(gridCell);
		showHostage.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleCheckButtonList(Entity.TYPE_HOSTAGE);
				handleCheckButtonList(Entity.TYPE_CAPTIVE);
			}
		});
		
		showNictus = new Button(tableShell, SWT.CHECK);
		showNictus.setText("Nictus");
		showNictus.setSelection(true);
		showNictus.setLayoutData(gridCell);
		showNictus.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleCheckButtonList(Entity.TYPE_QUANTUM);
			}
		});

		//show ignores checkbox
		showIgnores = new Button(tableShell, SWT.CHECK);
		showIgnores.setText("NPC Ignore");
		showIgnores.setSelection(true);
		showIgnores.setLayoutData(gridCell);
		showIgnores.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleCheckButtonList(Entity.TYPE_NPC_IGNORE);
			}
		});

		//show others checkbox
		showOthers = new Button(tableShell, SWT.CHECK);
		showOthers.setText("Others");
		showOthers.setSelection(true);
		showOthers.setLayoutData(gridCell);
		showOthers.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleCheckButtonList(Entity.TYPE_NONE);
			}
		});
		
		showNone = new Button(tableShell, SWT.CHECK);
		showNone.setText("Toggle all");
		showNone.setSelection(true);
		showNone.setLayoutData(gridCell);
		showNone.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				showNone.setSelection(showNone.getSelection());
				if(showNone.getSelection()){
					showDoors.setSelection(true);
					showNone.setSelection(true);
					showTeammate.setSelection(true);
					showGlowie.setSelection(true); 
					showHenchmen.setSelection(true);
					showHostage.setSelection(true);
					showNictus.setSelection(true);
					showIgnores.setSelection(true);	
					showOthers.setSelection(true);
					checkboxList.removeAll(checkboxList);
				}else{
					checkboxList.removeAll(checkboxList);
					checkboxList.add(Entity.TYPE_DOOR);
					checkboxList.add(Entity.TYPE_TEAMMATE);
					checkboxList.add(Entity.TYPE_GLOWIE);
					checkboxList.add(Entity.TYPE_DESTRUCT);
					checkboxList.add(Entity.TYPE_ANTIGLOWIE);
					checkboxList.add(Entity.TYPE_HENCHMAN);
					checkboxList.add(Entity.TYPE_HOSTAGE);
					checkboxList.add(Entity.TYPE_CAPTIVE);
					checkboxList.add(Entity.TYPE_QUANTUM);
					checkboxList.add(Entity.TYPE_NPC_IGNORE);
					checkboxList.add(Entity.TYPE_NONE);
					showDoors.setSelection(false);
					showNone.setSelection(false);
					showTeammate.setSelection(false);
					showGlowie.setSelection(false); 
					showHenchmen.setSelection(false);
					showHostage.setSelection(false);
					showNictus.setSelection(false);
					showIgnores.setSelection(false);	
					showOthers.setSelection(false);
				}
				handleParseButton();
			}
		});
				
		gridCell			= new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		customText = new Text(tableShell, SWT.SINGLE);
		customText.setText("");
		customText.setLayoutData(gridCell);
		customText.addListener(SWT.KeyUp, new Listener(){
			public void handleEvent(Event event) {
				customString = customText.getText();
				if(showCustom.getSelection()){
					handleParseButton();
					customText.forceFocus();
				}
			}
		});

		gridCell = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		showCustom = new Button(tableShell, SWT.CHECK);
		showCustom.setText("Show custom");
		showCustom.setSelection(true);
		showCustom.setLayoutData(gridCell);
		showCustom.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (showCustom.getSelection()){
					customString = customText.getText();
				}else{
					customString = "";
				}
				handleParseButton();
			}
		});	
		
		gridCell			= new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1);
		gridCell.heightHint	= 220;
					
		entityTable = new Table(tableShell, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL );
		entityTable.setLinesVisible(true);
		entityTable.setHeaderVisible(true);
		entityTable.setLayoutData(gridCell);
		entityTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handler.paintReticle((TableItem) event.item);
			}
		});
		
		//Make the column headers for the table
		col = new TableColumn(entityTable, SWT.FILL);
		col.setText("ID");
		col.setResizable(true);
		col.setWidth(30);

		col = new TableColumn(entityTable, SWT.FILL);
		col.setText("Name");
		col.setResizable(true);
		col.setWidth(150);

		col = new TableColumn(entityTable, SWT.FILL);
		col.setText("Type");
		col.setResizable(true);
		col.setWidth(70);

		col = new TableColumn(entityTable, SWT.FILL);
		col.setText("X");
		col.setResizable(true);
		col.setWidth(25);
		
		col = new TableColumn(entityTable, SWT.FILL);
		col.setText("Y");
		col.setResizable(true);
		col.setWidth(25);
		
		col = new TableColumn(entityTable, SWT.FILL);
		col.setText("Z");
		col.setResizable(true);
		col.setWidth(25);
		
		resetColumns();
		
		gridCell	= new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);

		statusBar	= new Label(tableShell,SWT.NONE);
		statusBar.setLayoutData(gridCell);
		statusBar.setText("File not parsed.");
		
		gridCell	= new GridData(SWT.FILL, SWT.RIGHT, true, false, 2, 1);
		
		liveUpdate = new Button(tableShell, SWT.CHECK);
		liveUpdate.setText("Live update");
		liveUpdate.setSelection(false);
		liveUpdate.setLayoutData(gridCell);
		liveUpdate.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (liveUpdate.getSelection()){
					display.timerExec (time, timer);
				}else{
					display.timerExec (-1, timer);
				}
			}
		});
				
		
		tableShell.pack();
		
		positionWindow();
		
		tableObjects = tableShell.getChildren();
	}
	
	public void setEntityCount(int count) {
		//Set the entity count in the status bar.
		String statusText;
		statusText = "& entities found.";
		
		statusText = statusText.replace("&", new Integer(count).toString());
		statusBar.setText(statusText);
	}
	
	public void resetColumns() {
		TableColumn[] columns = entityTable.getColumns();
		for (int i = 0; i < columns.length; i++) {
			if (i != 1) {
				columns[i].pack();
			}
		}
		
	}
	
	public void clearTable(boolean performRedraw) {
		entityTable.removeAll();
		
		setEntityCount(0);
		
		if (performRedraw)
			tableShell.redraw();
	}
	
	public void populateTableRows() {
		Entity entity;
		TableItem row;
		
		clearTable(false);
		
		int counter = 0;
		
		//Loop through the records and add them to the table
		for (int i = 0; i < dp.entityKeys.length; i++) {
			entity = dp.entityList.get(dp.entityKeys[i]);
			
			if (entity.entityName.equals("")) {
				continue;
			}

			//Skipping entities types that is not checked
			if(isEntityIgnorable(entity.entityType)){
				
				//But don't skip them if they matches the custom text.,
				java.util.regex.Pattern p = java.util.regex.Pattern.compile(getCustomString().toLowerCase());
				Matcher m = p.matcher(entity.entityName.toLowerCase());
				if(!showCustom.getSelection() || getCustomString().equals("")){
					continue;
				}else if(!m.find()){
					continue;
				}
			}

			row = new TableItem (entityTable, SWT.NONE);
			row.setText(0, new Integer(entity.objectID).toString() );
			row.setText(1, entity.entityName );
			row.setText(2, entity.getEntityTypeText() );
			row.setText(3, new Integer((int) entity.posXAxis).toString() );
			row.setText(4, new Integer((int) entity.posYAxis).toString() );
			row.setText(5, new Integer((int) entity.posZAxis).toString() );
			
			//Separate counter in case there are any skipped records
			counter++;
		}
		
		setEntityCount(counter);
		resetColumns();
		tableShell.redraw();
	}

	public void handleFileSelectButton() {
		FileDialog dialog;
		String filename;
		
		dialog = new FileDialog(tableShell, SWT.OPEN);
		dialog.setFilterExtensions( new String[] {"*.cohdemo"} );
		dialog.setFilterNames(new String[] {"CoH Demo Files"} );
		dialog.setText("Select your demofile");

		filename = dialog.open();

		//the file dialog returns a NULL reference if cancel was clicked
		if (filename == null) {
			return;
		}
		prefs.put(RadarConsts.PREF_KEY_FILENAME, filename);
		
		((Text) tableObjects[0]).setText(filename);
		((Text) tableObjects[0]).setSelection(filename.length());
		
		tableShell.forceFocus();
	}	
	
	
	public boolean isEntityIgnorable(Integer entityType){
		
		Iterator <Integer> itr = checkboxList.iterator();
	    while (itr.hasNext()) {
	      Integer element = itr.next();
	      if (element.intValue() == entityType.intValue()){
	    	 return true; 
	      }
	    }
		
		return false;
	}
	
	public void handleCheckButtonList(Integer entitytype){
		if(isEntityIgnorable(entitytype)){
			checkboxList.remove(checkboxList.indexOf(entitytype));
		}else{
			checkboxList.add(entitytype);
		}
		//populateTableRows();
		handleParseButton();
		}
	
	public void handleParseButton() {
		String filename;
		
		filename = ((Text)tableObjects[0]).getText();
		
		if (filename.equals("")) {
			dispErrorMsg("No File Entered");
			return; 
		}

		dp = new DemoParser(filename);
		
		if (dp.playerObjectID != null) { 
			populateTableRows();
			handler.renderMap(dp, checkboxList, getCustomString(), showCustom.getSelection());
		} else {
			clearTable(true);
			handler.clearMap(true);
		}
		
		handler.setTitle(filename.substring(filename.lastIndexOf(File.separator)+1, filename.length()), true);
		handler.forceFocus();
		tableShell.forceFocus();
	}
	
	public void positionWindow() {
		positionWindow(false);
	}
	public void positionWindow(boolean resetToDefault) {
		Rectangle	bounds;
		String tableWindow;
		
		resetColumns();
		
		tableShell.pack();
		
		tableShellBounds	= tableShell.getBounds();
		tableWindow			= prefs.get(RadarConsts.PREF_KEY_TABLE_WINDOW, "");
		
		if (tableWindow.equals("") || resetToDefault) {
			bounds	= ((Display.getCurrent()).getPrimaryMonitor()).getClientArea();

			tableShellBounds.x	= bounds.x;
			tableShellBounds.y	= bounds.y;
		} else {
			String[] tokens = tableWindow.split(",");
			
			tableShellBounds = new Rectangle( 	new Integer(tokens[0]),//X value 
												new Integer(tokens[1]),//Y value
												new Integer(tokens[2]),//Width
												new Integer(tokens[3]) //Height
											);
		}
		tableShell.setBounds(tableShellBounds);
	}
	
	public void saveWindowPosition(){
		String prefLine;
		
		tableShellBounds = tableShell.getBounds();
		
		prefLine	=	"" + 
						tableShellBounds.x +","+
						tableShellBounds.y +","+ 
						tableShellBounds.width +","+ 
						tableShellBounds.height;
		
		prefs.put(RadarConsts.PREF_KEY_TABLE_WINDOW, prefLine);
	}

	public void dispErrorMsg(String text) {
		MessageBox msg = new MessageBox(tableShell,SWT.OK|SWT.ICON_ERROR);
		msg.setMessage(text);
		msg.setText("Error");
		msg.open();		
	}
	
	//Pass-through handlers for the table's shell 
	public boolean forceFocus() {
		return tableShell.forceFocus();
	}
	public boolean isDisposed() {
		return tableShell.isDisposed();
	}
	public void dispose() {
		tableShell.dispose();
	}
	public void open(){
		tableShell.open();
	}
	
	public String getCustomString(){
		return customString;
	}

}
