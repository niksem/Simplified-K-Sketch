import java.awt.*;
import java.util.*;

public class ScreenShot {
	private ArrayList<CustomLine> objects;
	
	ScreenShot(){
		objects = new ArrayList<CustomLine>();
	}
	
	void createLine(Color c){
		objects.add(new CustomLine(c));
	}
	void addInitialPoint(int x, int y){
		objects.get(objects.size() - 1).addInitPoint(x, y);
	}
	
	void addPoint(int x, int y){
		objects.get(objects.size() - 1).addPoint(x, y);
	}
	
	ArrayList<CustomLine> getObjects(){
		return objects;
	}
	
	void insertShape(CustomLine insert){
		objects.add(insert);
    }
}
