Simplified-K-Sketch
===================
Nikolai Semenenko
nsemenen

Note on makefile: make run compiles and executes
Note on GIF: takes a while - button will be blue while it's processing

General Functionality (as per requirements)
1. Draw mode entered by clicking the draw button; similar functionality for the Erase and Select buttons
2. Inserting a new page only works by clicking the button
3. Your current mode is displayed in the bottom right hand corner
4. You can export your current animation (from the start to the end) to a GIF
5. The colours are only available in the draw mode - your latest color choice is always used; default is black
6. The rest of the functionality is as outlined by the requirements

Enhancements
1. Colors
2. Icons for buttons
3. Output animation as GIF to execution directory with the file name "YourCustomAnimation.gif"

Sources

Icons: www.iconarchive.com
Gif Encoder: www.java2s.com/Code/Java/2D-Graphics-GUI/AnimatedGifEncoder.htm

Future Improvements

- Store objects using a HashMap
- Use ID's for all indexing
- Move some functions from Model to ScreenShot because it makes more sense for them to be internal to the class

