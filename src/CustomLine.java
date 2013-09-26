import java.awt.Color;

import java.awt.geom.GeneralPath;

public class CustomLine {
	private GeneralPath path;	// used to draw line
	private Color color;		
	private int curId = 0;	// assigns id to current line
	static int id = 0;		// keeps track of all assigned ids
	
	CustomLine() {}
	
	CustomLine(Color c) {
		this.path = new GeneralPath();
		this.color = c;
		this.curId = id++;
	}
	
	void addPoint(int x, int y) {
		path.lineTo( (double) x, (double) y);
	}
	
	void addInitPoint(int x, int y) {
		path.moveTo( (double) x, (double) y);
	} 
	
	GeneralPath getPath() {
		return path;
	}
	
	Color getColor() {
		return color;
	}
	
	void setPath(GeneralPath copy) {
		this.path = (GeneralPath) copy.clone();
	}
	
	void setColor(Color c) {
		this.color = c;
	}
	
	int getId() {
		return this.curId;
	}
	
	void setId(int id) {
		this.curId = id;
	}
}
