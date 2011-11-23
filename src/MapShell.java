
import java.util.ArrayList;
import java.util.Iterator;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class MapShell {
	public Rectangle	mapShellBounds;
	public Shell		mapShell;
	
	DemoParser 		dp;
	ArrayList<Integer> ignoreList;
	String customString;
	boolean useCustomString;
	Display 		display;
	PaintListener	cursor;
	Preferences 	prefs	= Preferences.userNodeForPackage(this.getClass());
	
	float 	boundsXScale	= 1.0F,
			boundsYScale	= 1.0F,
			playerZPos		= 1.0F;
	
	MapShell(Display parent) {
		display		= parent;
		mapShell 	= new Shell(display, SWT.MIN | SWT.CLOSE | SWT.RESIZE);
		mapShell.setText(RadarConsts.PROGRAM_NAME +" "+ RadarConsts.VERSION);
		mapShell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				saveWindowPosition();
			}
		});
		mapShell.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				renderMap();
			}
		});
		
		mapShell.setBackground(parent.getSystemColor(SWT.COLOR_BLACK));
	}
	public void setBoundaryScalers() {
		int	demoWidth	= 1, demoHeight	= 1;
		
		mapShellBounds	= mapShell.getBounds();
		
		if (dp != null) {
			demoWidth 	= (dp.maxX - dp.minX) + (RadarConsts.WINDOW_BUFFER * 2);
			demoHeight	= (dp.maxY - dp.minY) + (RadarConsts.WINDOW_BUFFER * 2);
		}
		boundsXScale = (float) mapShellBounds.width	/ demoWidth;
		boundsYScale = (float) mapShellBounds.height / demoHeight;
	}
	
	public void setIcons(Image[] icons) {
		mapShell.setImages(icons);
	}
	
	public void setTitle(String title, boolean append) {
		String text;
		if (append == true) {
			text = RadarConsts.PROGRAM_NAME +" "+ RadarConsts.VERSION + " - " +title;
		} else {
			text = title;
		}
		mapShell.setText(text);
	}

	public void clearMap(boolean performRedraw) {
		Listener[]	painters;
		
		painters 	= mapShell.getListeners(SWT.Paint);
		
		for (int i = 0; i < painters.length; i++)
			mapShell.removeListener(SWT.Paint, painters[i]);
		
		if (performRedraw)
			mapShell.redraw();
	}
	
	public void renderMap(DemoParser demo, ArrayList<Integer> checboxList, String customString, boolean useCustomString) {
		dp = demo;
		ignoreList = checboxList;
		this.customString = customString;
		this.useCustomString = useCustomString;
		
		
		renderMap();
	}
	
	public void renderMap() {
		Entity 			entity;
		PaintListener 	playerIcon = null;
		
		if (dp == null)
			return;
		
		setBoundaryScalers( );
		
		clearMap(false);
		
		playerZPos	= dp.getEntity(dp.playerObjectID).posZAxis;
		
		for (int i = dp.entityKeys.length - 1; i >= 0; i--) {
			entity = dp.entityList.get(dp.entityKeys[i]);
			if (entity.entityName.equals("")) {
				continue;
			}
			
			if(isEntityIgnorable(entity.entityType)){
				//But don't skip them if they matches the custom text.,
				java.util.regex.Pattern p = java.util.regex.Pattern.compile(customString.toLowerCase());
				Matcher m = p.matcher(entity.entityName.toLowerCase());
				if(!useCustomString || customString.equals("")){
					continue;
				}else if(!m.find()){
					continue;
				}
			}
			
			if (entity.entityType != Entity.TYPE_PLAYER) {
				mapShell.addPaintListener(paintGraph(entity));
			} else {
				playerIcon = paintGraph(entity);
			}
		}
		if (playerIcon != null) {
			mapShell.addPaintListener(playerIcon);
		}

		mapShell.redraw();
	}
	
	public boolean isEntityIgnorable(Integer entityType){
		
		Iterator <Integer> itr = ignoreList.iterator();
	    while (itr.hasNext()) {
	      Integer element = itr.next();
	      if (element.intValue() == entityType.intValue()){
	    	 return true; 
	      }
	    }
		
		return false;
	}
	
	
	public PaintListener paintGraph(final Entity entity) {
		return new PaintListener() {
			public void paintControl(PaintEvent event) {
				int x, y, bgColorCode;
				float xF, yF;

				if (entity.entityType == Entity.TYPE_PLAYER) {
					bgColorCode = SWT.COLOR_GREEN;
				} else if (entity.entityType == Entity.TYPE_TEAMMATE) {
					bgColorCode = SWT.COLOR_DARK_CYAN;
				} else if (entity.entityType == Entity.TYPE_GLOWIE) {
					bgColorCode = SWT.COLOR_YELLOW;
				} else if (entity.entityType == Entity.TYPE_DOOR) {
					bgColorCode = SWT.COLOR_BLUE;
				} else if (entity.entityType == Entity.TYPE_HOSTAGE) {
					bgColorCode = SWT.COLOR_RED;
				} else if (entity.entityType == Entity.TYPE_CAPTIVE) {
					bgColorCode = SWT.COLOR_RED;
				} else if (entity.entityType == Entity.TYPE_ANTIGLOWIE) {
					bgColorCode = SWT.COLOR_DARK_YELLOW;
				} else if (entity.entityType == Entity.TYPE_QUANTUM) {
					bgColorCode = SWT.COLOR_DARK_MAGENTA;
				} else if (entity.entityType == Entity.TYPE_DESTRUCT) {
					bgColorCode	= SWT.COLOR_DARK_YELLOW;
				} else if (entity.entityType == Entity.TYPE_HENCHMAN) {
					bgColorCode	= SWT.COLOR_DARK_GREEN;
				} else if (entity.entityType == Entity.TYPE_NPC_IGNORE) {
					bgColorCode	= SWT.COLOR_DARK_GRAY;
				} else {
					bgColorCode = SWT.COLOR_WHITE;
				}

				event.gc.setBackground(display.getSystemColor(bgColorCode));
				event.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				
				xF = RadarConsts.WINDOW_BUFFER + dp.maxX - entity.posXAxis;
				yF = entity.posYAxis - (dp.minY - RadarConsts.WINDOW_BUFFER);

				x = (int) (xF * boundsXScale);
				y = (int) (yF * boundsYScale);

				if (entity.posZAxis > (playerZPos + 3)) {
					//Triangle pointing up
					int[] pointArray = {	x,	(y-RadarConsts.POINT_RADIUS), 
							  (x-RadarConsts.POINT_RADIUS),	(y+RadarConsts.POINT_RADIUS),
							  (x+RadarConsts.POINT_RADIUS), (y+RadarConsts.POINT_RADIUS) };
					event.gc.fillPolygon(pointArray);
				} else if (entity.posZAxis < (playerZPos - 3)) {
					//triangle pointing down
					int[] pointArray = {		x, 	(y+RadarConsts.POINT_RADIUS), 
								  (x-RadarConsts.POINT_RADIUS),	(y-RadarConsts.POINT_RADIUS),
						  	   	  (x+RadarConsts.POINT_RADIUS), 	(y-RadarConsts.POINT_RADIUS) };
					event.gc.fillPolygon(pointArray);
				} else {
					event.gc.fillOval((x-RadarConsts.POINT_RADIUS),	(y-RadarConsts.POINT_RADIUS), 
									  (RadarConsts.POINT_RADIUS*2), (RadarConsts.POINT_RADIUS*2));
				}
			}
		};
	}
	
	public void paintReticle(final TableItem item) {		
		if (cursor != null) {
			mapShell.removePaintListener(cursor);
		}
		cursor = new PaintListener() {
			public void paintControl(PaintEvent pe) {
				int x = 0,
					y = 0;
				float	xF	= 0.0F,
						yF	= 0.0F;
				
				Entity entity = dp.entityList.get( new Integer(item.getText(0)));
				
				xF = RadarConsts.WINDOW_BUFFER + dp.maxX - entity.posXAxis;
				yF = entity.posYAxis - (dp.minY - RadarConsts.WINDOW_BUFFER);
				
				x = (int) (xF * boundsXScale);
				y = (int) (yF * boundsYScale);

				pe.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				
				pe.gc.drawRectangle( x-(RadarConsts.POINT_RADIUS*2), y-(RadarConsts.POINT_RADIUS*2), (RadarConsts.POINT_RADIUS*4)-1, (RadarConsts.POINT_RADIUS*4) );
				
				pe.gc.drawLine( x-(RadarConsts.POINT_RADIUS*3), y, x+(RadarConsts.POINT_RADIUS*3)-1, y );
				pe.gc.drawLine( x, y-(RadarConsts.POINT_RADIUS*3), x, y+(RadarConsts.POINT_RADIUS*3)-1 );
			}
		};
		mapShell.addPaintListener(cursor);
		mapShell.redraw();
	}
	
	
	public void positionWindow() {
		positionWindow(false);
	}
	
	public void positionWindow(boolean resetToDefault) {
		String mapWindow;
		
		mapShellBounds	= mapShell.getBounds();
		mapWindow		= prefs.get(RadarConsts.PREF_KEY_MAP_WINDOW, "");
		
		if (mapWindow.equals("") || resetToDefault) {
			Rectangle	dispBounds, tableBounds;
			
			dispBounds	= ((Display.getCurrent()).getPrimaryMonitor()).getClientArea();
			tableBounds	= display.getShells()[1].getBounds();
			
			mapShellBounds.width 	= dispBounds.width - tableBounds.width;
			mapShellBounds.height	= dispBounds.height;
			mapShellBounds.x		= dispBounds.x + tableBounds.width;
			mapShellBounds.y		= dispBounds.y;
		} else {
			String[] tokens = mapWindow.split(",");
			
			mapShellBounds = new Rectangle( 	new Integer(tokens[0]),//X value 
												new Integer(tokens[1]),//Y value
												new Integer(tokens[2]),//Width
												new Integer(tokens[3]) //Height
											);
		}
		mapShell.setBounds(mapShellBounds);
	}
	
	public void saveWindowPosition(){
		String prefLine;
		
		mapShellBounds	= mapShell.getBounds();
		
		prefLine	=	"" + 
						mapShellBounds.x +","+ 
						mapShellBounds.y +","+ 
						mapShellBounds.width +","+ 
						mapShellBounds.height;
		
		prefs.put(RadarConsts.PREF_KEY_MAP_WINDOW, prefLine);
	}
	
	//Pass-through handlers for the map's shell 
	public boolean getMinimized() {
		return mapShell.getMinimized();
	}
	public void setMinimized(boolean minimized) {
		mapShell.setMinimized(minimized);
	}
	public boolean isDisposed() {
		return mapShell.isDisposed();
	}
	public void dispose() {
		mapShell.dispose();
	}
	public boolean forceFocus() {
		return mapShell.forceFocus();
	}
	public void open() {
		mapShell.open();
	}
	public void redraw() {
		mapShell.redraw();
	}
	
	public Display getDisplay(){
		return display;
	}
	
	
}
