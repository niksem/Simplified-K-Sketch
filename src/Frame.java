import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

public class Frame extends JPanel{
	private int prevX = 0;
	private int prevY = 0;
	private int newX = 0; 
	private int newY = 0;
	
	private Timer tim;
	private final Controller controller;
	private Model model;
	private ArrayList<CustomLine> custom;
	private GeneralPath select;
	
	private static final long serialVersionUID = 1L;
	
	// combined view and controller
	Frame(Model m, Controller c){
		this.model = m;
		this.controller = c;
		
		this.addMouseListener(new MouseAdapter() {
			
			public void mousePressed(MouseEvent event){	
				prevX = event.getX();
				prevY = event.getY();
				
				model.handleClick(prevX, prevY, controller.getCurrentColor());
				repaint();
			}
			public void mouseReleased(MouseEvent event){
				prevX = 0;
				prevY = 0;
				newY = 0;
				newX = 0;
				model.handleRelease();
				repaint();
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent event){
				newX = event.getX();
				newY = event.getY();
				
				model.handleDrag(prevX, prevY, newX, newY);
				prevX = newX;
				prevY = newY;
				
				repaint();
			}
		});
		
		ActionListener listener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				model.playAnimation();
				repaint();
			}
		};
		tim = new Timer(8, listener);
		tim.start();
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        custom = this.model.getCurrentScreenObjects();
        
        g2.setColor(Color.BLACK);
        select = model.getSelectionPath();
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        g2.draw(select);
        g2.setColor(new Color(204,255,204));
        g2.fill(model.getSelectionPath());
        
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
    
        for(int i = 0; i < custom.size(); i++){
        	g2.setColor(custom.get(i).getColor());
        	g2.draw(custom.get(i).getPath());
        }
        
        controller.getSlider().setMaximum(model.getScreenShots().size() - 1);
        controller.getSlider().setValue(model.getCurrentScreen());	
    }
	
	private AnimatedGifEncoder encoder;
	
	void createGif(){
		this.model.updateState(this.controller.getButtonA().getToolTipText());
		encoder = new AnimatedGifEncoder();
		encoder.start("YourCustomAnimation.gif");
		encoder.setDelay(8);
		encoder.setQuality(20); // lowest possible while increase in speed noticeable
		// add frames
		for(int i = 0; i < this.model.getScreenShots().size() - 1; i++){
			 this.model.setCurrentScreen(i);
			 BufferedImage bi = new BufferedImage(
			            this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			 this.paint(bi.getGraphics());
			 encoder.addFrame(bi);
		}
		encoder.finish();
	}
	
}
