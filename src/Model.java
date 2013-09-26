import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.File;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Model {

	public enum State{
		Draw, Erase, Select
	}
	
	private ArrayList<ScreenShot> screenShots;
	private int currentScreen;
	private State state;
	private GeneralPath selectionPath;
	private boolean selectedRegionExists;
	private boolean playAnimation;
	private int playCounter;
    DocumentBuilder docBuilder;
    DocumentBuilderFactory docFactory;
	
	Model(){
		screenShots = new ArrayList<ScreenShot>();
		screenShots.add(new ScreenShot());
		selectionPath = new GeneralPath();
		this.selectionPath.setWindingRule(GeneralPath.WIND_EVEN_ODD);
		currentScreen = -1;
		addScreenShot();
		state = State.Draw; 
		selectedRegionExists = false;
		selectionPath.reset();
		playAnimation = false;
		playCounter = 0;
		docFactory = DocumentBuilderFactory.newInstance();
    	try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
	}
	
	ArrayList<ScreenShot> getScreenShots(){
		return this.screenShots;
	}
	
	int getCurrentScreen(){
		return this.currentScreen;
	}
	
	void setCurrentScreen(int i){
		this.currentScreen = i;
	}
	
	GeneralPath getSelectionPath(){
		return this.selectionPath;
	}
	
	void createXML(){
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("animation");
		doc.appendChild(rootElement);
		Element size = doc.createElement("Size");
		String sizeStr = Controller.frameWidth + "," + Controller.frameHeight;
		size.appendChild(doc.createTextNode(sizeStr));
		rootElement.appendChild(size);
		Element frame;//= doc.createElement("Frame");
		for(int i = 0; i < screenShots.size() - 1; i++){
			frame = doc.createElement("Frame");			
			for(int j = 0; j < screenShots.get(i).getObjects().size(); j++){
				Element line = doc.createElement("Line");

				PathIterator p = screenShots.get(i).getObjects().get(j).getPath().getPathIterator(null);
				Element xcoords = doc.createElement("xcoords");
				Element ycoords = doc.createElement("ycoords");
				String datax = "";
				String datay = "";
				while(!p.isDone()){
					
					double[] coordinates = new double[6];
					int type = p.currentSegment(coordinates);
					switch(type){
						case PathIterator.SEG_LINETO: case PathIterator.SEG_MOVETO:
							datax += String.valueOf((long)(coordinates[0])) + ",";
							datay += String.valueOf((long)(coordinates[1])) + ",";
							break;
						default:
							break;
					}
					p.next();
				}
				datax = datax.substring(0, datax.length() - 1); // removing last comma
				datay = datay.substring(0, datay.length() - 1);
				
				xcoords.appendChild(doc.createTextNode(datax));
				ycoords.appendChild(doc.createTextNode(datay));
				line.appendChild(xcoords);
				line.appendChild(ycoords);
				Element color = doc.createElement("Color");
				color.appendChild(doc.createTextNode(String.valueOf(screenShots.get(i).getObjects().get(j).getColor().getRGB())));
				line.appendChild(color);
				frame.appendChild(line);
			}
			rootElement.appendChild(frame);
		}
					
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		DOMSource source = new DOMSource(doc);
		
		File f = new File("animation.xml");
		
		for(int i = 0; f.exists(); i++){
			f = new File("animation" + i + ".xml");
		}

		StreamResult result = new StreamResult(f);
 
		try {
			transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	void checkErase(int x, int y){
		ArrayList<CustomLine> objects = screenShots.get(currentScreen).getObjects();
		for(int i = 0; i < objects.size(); i++){
			int id = -1;
			// rectangle is to add some lee-way
			if(objects.get(i).getPath().contains(new Point(x, y)) || objects.get(i).getPath().intersects(new Rectangle(x, y, 2,2))){
				id = (objects.get(i).getId());
				objects.remove(i);
				i--;
			}
			if(id != -1){ // delete object for all subsequent screens
				for(int j = currentScreen + 1; j < screenShots.size(); j++){
					ArrayList<CustomLine> localObjects = screenShots.get(j).getObjects();
					for(int k = 0; k < localObjects.size(); k++){
						if(localObjects.get(k).getId() == id){
							localObjects.remove(k);
							break;
						}
					}
				}
			}
		}
		if(currentScreen >= screenShots.size() - 1){
			currentScreen = screenShots.size() - 2;
			if(currentScreen == -1){
				addScreenShot();
			}
		}
	}
	
	void handleClick(int x, int y, Color c){
		switch(state){
			case Draw:
				createLine(c);
				addInitialPoint(x, y);
				break;
			case Erase:
				checkErase(x, y);
				break;
			default: // Select
				if(!selectedRegionExists){
					selectionPath.moveTo(x, y);
				} else { // check if click is inside; if it is, OK - making animation; if not, start selecting new region
					if(selectionPath.contains(new Point(x, y))){ // works!!
						// animate
					} else {
						selectionPath.reset();
						selectionPath.moveTo(x, y);
						selectedRegionExists = false;
					}
				}
				break;
		}
	}
	
	void setPlayAnimation(boolean bool){
		this.playAnimation = bool;
	}
	
	boolean withinSelectedPath(CustomLine c){
		PathIterator p = c.getPath().getPathIterator(null);
		while(!p.isDone()){
			double[] coordinates = new double[6];
			int type = p.currentSegment(coordinates);
			switch(type){
				case PathIterator.SEG_LINETO: case PathIterator.SEG_MOVETO:
					if(!selectionPath.contains(new Point((int)coordinates[0], (int)coordinates[1]))){
						return false;
					}
					break;
				default:
					break;
			}
			p.next();
		}
		return true;
	}
	
	void setCurrentValue(int curScreen){
		this.currentScreen = curScreen;
	}
	
	// need to fix this
	void handleDrag(int x, int y, int newX, int newY){
		switch(state){
			case Draw:
				addPoint(x, y);
				break;
			case Erase:
				checkErase(x, y);
				break;
			default: // Select
				if(!selectedRegionExists){
					selectionPath.lineTo((double) x, (double)y);
				} else {
					AffineTransform at = new AffineTransform();
					at.translate(newX - x, newY - y);
					selectionPath.transform(at);
					
					ArrayList<CustomLine> objects = screenShots.get(currentScreen).getObjects();
				
					if(currentScreen >= screenShots.size() - 2){
						addScreenShot();
						// have to copy all objects to new screenshot
						for(int i = 0; i < objects.size(); i++){
							CustomLine c = new CustomLine();
							c.setPath(objects.get(i).getPath());
							c.setColor(objects.get(i).getColor());
							c.setId(objects.get(i).getId());
							if(withinSelectedPath(objects.get(i))){
								c.getPath().transform(at);
							}
							screenShots.get(currentScreen).insertShape(c);
						}	
					} else { 
						for(int i = 0; i < objects.size(); i++){
							if(withinSelectedPath(objects.get(i))){
								CustomLine c = new CustomLine();
								c.setPath(objects.get(i).getPath());
								c.setColor(objects.get(i).getColor());
								c.setId(objects.get(i).getId());
								c.getPath().transform(at);
								
								for(int j = screenShots.size() - 2; j > currentScreen; j--) {
									for(int k = 0; k <  screenShots.get(j).getObjects().size(); k++){
										if(screenShots.get(j).getObjects().get(k).getId() == c.getId()){
											screenShots.get(j).getObjects().remove(k);
											break;
										}
									}
								}
							
								for(int j = currentScreen + 1; j < screenShots.size() - 1; j++){
									screenShots.get(j).insertShape(c);
								}
							}
						}
						currentScreen++;
					}
				}
				break;
		}
	}

	void addScreenShotWithPrevs(){
		screenShots.add(currentScreen+1, new ScreenShot());
		ArrayList<CustomLine> objects = screenShots.get(currentScreen).getObjects();
		for(int i = 0; i < objects.size(); i++){
			screenShots.get(currentScreen+1).getObjects().add(objects.get(i));
		}
		currentScreen++;
	}
	
	void handleRelease(){
		switch(state){
		case Draw:
			if(currentScreen < screenShots.size() - 2){ // then it's definitely not on the latest one
				CustomLine c = screenShots.get(currentScreen).getObjects().get(screenShots.get(currentScreen).getObjects().size() - 1);
				CustomLine copy = new CustomLine();
				copy.setPath(c.getPath());
				copy.setColor(c.getColor());
				copy.setId(c.getId());
				for(int i = currentScreen + 1; i < screenShots.size(); i++){
					screenShots.get(i).getObjects().add(copy);
				}
			}
			break;
		case Erase:
			break;
		default:
			if(!selectedRegionExists){
				selectedRegionExists = true;
				selectionPath.closePath();
			}
			break;
		}
	}
	
	void addScreenShot(){
		screenShots.add(++currentScreen, new ScreenShot());
	}
	
	void addInitialPoint(int x, int y){
		screenShots.get(currentScreen).addInitialPoint(x, y);
	}
	
	void updateState(String buttonName){
		State newState;
		if(buttonName == "Draw"){
			newState = State.Draw;
			selectionPath.reset();
			selectedRegionExists = false;
		} else if (buttonName == "Erase"){
			newState = State.Erase;
			selectionPath.reset();
			selectedRegionExists = false;
		} else { // select - default
			newState = State.Select;
		}
		setState(newState);
	}
	
	void createLine(Color c){
		screenShots.get(currentScreen).createLine(c);
	}
	
	void addPoint(int x, int y){
		screenShots.get(currentScreen).addPoint(x, y);
	}
	
	ArrayList<CustomLine> getCurrentScreenObjects(){
		if(currentScreen >= screenShots.size() - 1){
			currentScreen = screenShots.size() - 2;
		}
		return screenShots.get(currentScreen).getObjects();
	}
	
	void setState(State s){
		this.state = s;
	}
	
	void playAnimation(){
		if (playAnimation && playCounter >= screenShots.size() - 2){
			playAnimation = false;
			playCounter = 0;
			currentScreen = screenShots.size() - 2;
		} else if(playAnimation){
			currentScreen = playCounter++;
		}
	}
	
	void setPlayCounter(int p){
		playCounter = p;
	}
}
