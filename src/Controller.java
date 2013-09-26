import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller extends JComponent {
	
	private static final long serialVersionUID = 1L;
	
	private Model model;
	
	private JFrame frame;
	private Container content;
	private Dimension screen;
	
	static int frameWidth = 0;
	static int frameHeight = 0;
	
	private JPanel topPanel;
	private Frame centerPanel;
	private JPanel bottomPanel;
	private JPanel controlsPanel;
	private JPanel infoPanel;
	private JToolBar toolBar;
	private JButton buttonA;
	private JButton buttonB;
	private JButton buttonC;
	
	private JButton buttonPlay;
	private JButton buttonPause;
	private JButton buttonPlus;
	
	private JButton buttonGif;	
	private JButton buttonXML;	
	
	private ImageIcon icon;
	private JColorChooser colorChooser;
	private AbstractColorChooserPanel[] panels;
    private JLabel label;
  
    private JSlider slider; 

    Controller() {
    	model = new Model();
    	
    	//String path = Controller.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    	//System.out.println("Path: " + path);
    	// setting up panels
    	topPanel = new JPanel(new BorderLayout());
	    centerPanel = new Frame(model, this);
	    bottomPanel = new JPanel(new BorderLayout());
    	controlsPanel = new JPanel();
    	infoPanel = new JPanel();
    	
		// setting up slider
		slider = new JSlider();
		slider.setMinimum(0);
		slider.setValue(0);
		slider.setMaximum(0);
		
		slider.setPaintLabels(true);
		int tickSpacing = 25;
	    slider.setMajorTickSpacing(tickSpacing);

	    slider.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent evt) {
	          JSlider slider = (JSlider) evt.getSource();
	          if (slider.getValueIsAdjusting()) {
	        	  if(slider.getValue() < model.getScreenShots().size() - 1){
	        		  model.setCurrentValue(slider.getValue());
	        		  model.setPlayCounter(slider.getValue());
	        	  }
	          }
	        }
	    });
	    
		// setting up layout and frame
		frame = new JFrame ();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  
	    // setting up toolbar, content
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
	    content = frame.getContentPane();
	    
	    // getting screen information
	    screen = Toolkit.getDefaultToolkit().getScreenSize();
	    frameWidth = (int)screen.getWidth() * 4/5;
	  	frameHeight = (int)screen.getHeight() * 4/5;
	  			
		// making sure overall window has BorderLayout
		content.setLayout(new BorderLayout());
	
		label = new JLabel("Mode: Draw");

		icon = new ImageIcon("src/resources/draw.png");
		buttonA = new JButton(icon);
		buttonA.setToolTipText("Draw");
		
		buttonA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
				model.setPlayAnimation(false);
				model.updateState(buttonA.getToolTipText());
				label.setText("Mode: " + buttonA.getToolTipText());
				frame.repaint();
            }
		});
	
		toolBar.add(buttonA);
		
		icon = new ImageIcon("src/resources/erase.png");
		buttonB= new JButton(icon);
		buttonB.setToolTipText("Erase");
		
		buttonB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
				model.setPlayAnimation(false);
				model.updateState(buttonB.getToolTipText());
				label.setText("Mode: " + buttonB.getToolTipText());
				frame.repaint();
            }
		});
		
		toolBar.add(buttonB);
		
		icon = new ImageIcon("src/resources/select.png");
		buttonC = new JButton(icon);
		buttonC.setToolTipText("Select");
		buttonC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
				model.setPlayAnimation(false);
				model.updateState(buttonC.getToolTipText());
				label.setText("Mode: " + buttonC.getToolTipText());
				frame.repaint();
            }
		});
		
		toolBar.add(buttonC);
	
		icon = new ImageIcon("src/resources/gif.png");
		buttonGif = new JButton(icon);
		buttonGif.setToolTipText("Create a gif from current animation");
		buttonGif.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
	        {
				centerPanel.createGif();
	        }
		});
		
		toolBar.add(buttonGif);
		
		icon = new ImageIcon("src/resources/Xml-tool-icon.png");
		buttonXML = new JButton(icon);
		buttonXML.setToolTipText("Create an XML from current animation");
		buttonXML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
	        {
				model.createXML();
	        }
		});
		
		toolBar.add(buttonXML);
		
		// setting up colour chooser
	    colorChooser = new JColorChooser();   
	    panels = colorChooser.getChooserPanels();
	    //colorChooser.removeChooserPanel(panels[2]);
	    //colorChooser.removeChooserPanel(panels[1]);
	    AbstractColorChooserPanel panel = colorChooser.getChooserPanels()[0];
	    panels = new AbstractColorChooserPanel[1];
	    panels[0] = panel;
	   // panels= colorChooser.getChooserPanels()[0];
	    
	    //colorChooser.setChooserPanels(colorChooser.getChooserPanels()[0]);
	    colorChooser.setChooserPanels(panels);
		colorChooser.setPreviewPanel(new JPanel()); // removing preview 
		colorChooser.setColor(Color.black);
		toolBar.add(colorChooser);
		
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		// toolBar.add(label);
		
		topPanel.add(toolBar, BorderLayout.CENTER);
		topPanel.setBorder(BorderFactory.createEtchedBorder(1));

		centerPanel.setBackground(Color.WHITE);

		icon = new ImageIcon("src/resources/pause.png");
		buttonPause = new JButton(icon);
		buttonPause.setToolTipText("Pause");
		buttonPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
	        {
				model.setPlayAnimation(false);
	        }
		});
		
		icon = new ImageIcon("src/resources/play.png");
		buttonPlay = new JButton(icon);
		buttonPlay.setToolTipText("Play");
		buttonPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
	        {
				model.setPlayAnimation(true);
	        }
		});
		
		controlsPanel.add(buttonPlay);
		controlsPanel.add(buttonPause);
		
		icon = new ImageIcon("src/resources/plus.png");
		buttonPlus = new JButton(icon);
		buttonPlus.setToolTipText("Insert a frame");
		buttonPlus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
	        {
				model.addScreenShotWithPrevs();
	        }
		});
		
		infoPanel.add(buttonPlus);
		infoPanel.add(label);
		
		bottomPanel.add(infoPanel, BorderLayout.EAST);
		bottomPanel.add(controlsPanel, BorderLayout.WEST);
	    bottomPanel.add(slider, BorderLayout.CENTER);
	    
	    
		content.add(topPanel, BorderLayout.NORTH);
		content.add(centerPanel, BorderLayout.CENTER);
		content.add(bottomPanel, BorderLayout.SOUTH);
	
		frame.setResizable(true);
		frame.setTitle("Simplified K-Sketch");
		frame.setSize(frameWidth,frameHeight);
		frame.setLocationRelativeTo(null);		
		frame.setMinimumSize(new Dimension(1010, 400));
		frame.setVisible(true);
	};	
	
	Color getCurrentColor(){
		return this.colorChooser.getColor();
	}
	
	JSlider getSlider(){
		return this.slider;
	}
	
	JButton getButtonA(){
		return this.buttonA;
	}
}
