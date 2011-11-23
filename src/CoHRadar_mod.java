

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;



public class CoHRadar_mod {
	
	//Window Sizes and Scalers
	Rectangle mainDisplayBounds;
	float 	boundsXScale	= 1.0F,
			boundsYScale	= 1.0F,
			playerZPos		= 1.0F;
	
	//Icon Holders
	Image RADAR16_ICON, RADAR32_ICON , RADAR128_ICON;
	
	//Global UI Elements
	Display		display;
	Control[]	tableShellControls;
	MapShell 	mapHandler;
	TableShell	tableHandler;
	Label 		statusBar;
	
	//Are these really needed at the class level?
	DemoParser 		dp;
	PaintListener	cursor;
	
	public static void main(String[] args) {
		new CoHRadar_mod();
	}
		
	public CoHRadar_mod() {
		Image[] radarIcons;
		
		Display.setAppName(RadarConsts.PROGRAM_NAME); //Set for the Mac Version
		display				= new Display();
		mainDisplayBounds	= display.getPrimaryMonitor().getClientArea();
		
		RADAR16_ICON	= new Image(display, getClass().getResourceAsStream("radar16.png"));
		RADAR32_ICON	= new Image(display, getClass().getResourceAsStream("radar32.png"));
		RADAR128_ICON	= new Image(display, getClass().getResourceAsStream("radar128.png"));
		radarIcons	 	= new Image[] { RADAR16_ICON, RADAR32_ICON, RADAR128_ICON };
		
		//Create the Shells and UI
		mapHandler 	 	= new MapShell(display);
		tableHandler	= new TableShell(mapHandler);
		
		mapHandler.setIcons(radarIcons);
		tableHandler.setIcons(radarIcons);
		
		tableHandler.initUI();
		mapHandler.positionWindow();
		
		makeTrayIcon();
		
		mapHandler.open();
		tableHandler.open();
		
		//If either of the windows are closed, skip over the loop and kill the display
		while ( !tableHandler.isDisposed() && !mapHandler.isDisposed() ) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();		
	}
	
	public void makeTrayIcon() {
		final TrayItem item;
		final Tray systemTray = display.getSystemTray();
		
		if (systemTray != null) {
			item = new TrayItem (systemTray, SWT.NONE);
			
			item.setImage(RADAR16_ICON);
			item.setToolTipText(RadarConsts.PROGRAM_NAME);
			
			//Focus the program if you left-click the icon 
			item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					//have to focus the map first as the table is a child of that, 
					//then focus the table for input there
					mapHandler.forceFocus();
					tableHandler.forceFocus();
				}
			} );
			
			//Create the pop-up menu when you right-click
			item.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					Menu 		trayMenu;
					MenuItem 	trayMenuItem;
					Shell 		eventShell;
					
					eventShell	= new Shell(event.display);
					trayMenu	= new Menu(eventShell, SWT.POP_UP);
					
					trayMenuItem	= new MenuItem(trayMenu, SWT.PUSH);
					trayMenuItem.setText("Reset Window Position");
					trayMenuItem.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							if (mapHandler.getMinimized() == true)
								mapHandler.setMinimized(false);
							
							tableHandler.positionWindow(true);
							mapHandler.positionWindow(true);
							
							mapHandler.forceFocus();
							tableHandler.forceFocus();
						}
					});
					
					//Create "Refresh" Menu option
					trayMenuItem	= new MenuItem(trayMenu, SWT.PUSH);
					trayMenuItem.setText("Refresh Map");
					trayMenuItem.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							if (mapHandler.getMinimized() == true)
								mapHandler.setMinimized(false);
							tableHandler.handleParseButton();
						}
					});
					
					//Create "Exit" Menu option
					trayMenuItem	= new MenuItem(trayMenu, SWT.PUSH);
					trayMenuItem.setText("Exit");
					trayMenuItem.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							mapHandler.dispose();
						}
					});
					
					trayMenuItem	= null; //lose the reference
					
					trayMenu.setVisible(true);
				}
			});
		}	
	}
	
}
