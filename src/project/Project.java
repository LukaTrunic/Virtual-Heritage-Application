package project;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.sun.prism.impl.BufferUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.IntBuffer;

public class Project extends GLCanvas implements GLEventListener, KeyListener, MouseListener {

    // create multiple checkboxes
    private JCheckBox lightOnOff;
    private JCheckBox globalAmbientLight;
    private JCheckBox diffuseLight;
    private JCheckBox specularLight;
    private JCheckBox ambientLight;

    // create multiple buttons
    private JButton removeButton;
    private JButton addButton;
    private JButton finishButton;
    private JButton helpButton;
    private JButton quitButton;
    private JButton newGameButton;

    // placeholder to display text
    private JLabel label;

    // the main gui where all the components like buttons,
    // checkboxes, etc.. are added
    private JFrame frame;

    // initialize the canvas for the window
    private GLCanvas canvas;
    private FPSAnimator animator;
    private int WINDOW_WIDTH = 640;
    private int WINDOW_HEIGHT = 480;
    private static final String TITLE = "The Shapes";
    private static final int FPS = 120;

    // provide access to opengl utility libraries like scaling, projections, etc..
    private GLU glu;

    // render bitmapped java 2d text unto our opengl window
    private TextRenderer textRenderer;
    private TextRenderer textMatch;

    // initialize our textures files
    private String [] textureFileNames = {
            "sandstone.jpeg",
            "marbel.jpg",
            "frame.jpeg",
            "desert.jpg"
    };

    // help in loading textures from disk using opengl
    Texture [] textures = new Texture[textureFileNames.length];

    // initialize a nameId for picking the shapes
    private int nameId = 0;

    // id for the palette shapes insert to the blueprint
    private int cube_idn = 0;
    private int rectangularpyramid_idn = 0;
    private int cylinder_idn = 0;
    private int base_idn = 0;

    // initialize a variable to traverse through the blueprint
    private int traverse = 0;

    // a constant value for scaling the shapes in the blueprint (increase/decrease)
    private final float scaleDelta = 0.1f;

    // Scale values for shapes added from palette
    private float scaleCube = 1f;
    private float scaleRectangularpyramid = 1f;
    private float scaleCylinder = 1f;
    private float scaleBase = 1f;

    private int angleCubeX = 0;
    private int angleCubeY = 0;
    private int angleCubeZ = 0;

    private int angleRectangularPyramidX = 0;
    private int angleRectangularPyramidY = 0;
    private int angleRectangularPyramidZ = 0;

    private int angleCylinderX = 0;
    private int angleCylinderY = 0;
    private int angleCylinderZ = 0;

    private int angleBaseX = 0;
    private int angleBaseY = 0;
    private int angleBaseZ = 0;

    // a constant to rotate the shapes inserted into the blueprint (at an angle)
    private float rotate = 0;

    // ids for shapes
    private static final int CUBE_ID = 1;
    private static final int RECTANGULAR_PYRAMID_ID = 2;
    private static final int CYLINDER_ID = 3;
    private static final int BASE_ID = 4;

    // check result
    boolean isCubeMatched = false;
    boolean isRectangularPyramidMatched = false;
    boolean isCylinderMatched = false;
    boolean isBaseMatched = false;

    // total number of shapes (n+1)
    private static int TOTAL_NUM_OF_SHAPES = 4;

    // size of the buffer to store in memory, information about the selected shape
    private static final int BUFSIZE = 512;
    private IntBuffer selectBuffer;

    // used in selecting the object we want to draw on the blueprint
    // based on the (X,Y) coordinate
    private boolean inSelectionMode = false;
    private int xCursor = 0;
    private int yCursor = 0;

    // the current angle of the blueprint
    private int currentAngleOfRotationX = 0;
    private int currentAngleOfRotationY = 0;
    private int currentAngleOfVisibleField = 55; // camera

    // holds the value at which we want to rotate the shape
    private int angleDelta = 1;

    // Define constants for cylinder properties
    private static final double CYLINDER_BASE_HEIGHT = 0.6; // Base height for blueprint and palette
    private static final double CYLINDER_Y_COORDINATE = -2.4; // Y coordinate for cylinder placement
    private static final double[] CYLINDER_X_POSITIONS = {-5.5, -2.75, 0, 2.75, 5.5};
    private static final double[] CYLINDER_Z_POSITIONS = {3.5, 1.75, 0, -1.75, -3.5};

    // Define constants for rectangular pyramid properties
    private static final double PYRAMID_WIDTH = 12.0;
    private static final double PYRAMID_DEPTH = 8.0;
    private static final double PYRAMID_HEIGHT = 3.0;
    private static final double PYRAMID_BASE_HEIGHT = 0.6; // Base height for the pyramid

//    // Add these variables to store the initial random rotations
//    private int initialCubeRotationX = 0;
//    private int initialCubeRotationY = 0;
//    private int initialCubeRotationZ = 0;
//
//    private int initialRectangularPyramidRotationX = 0;
//    private int initialRectangularPyramidRotationY = 0;
//    private int initialRectangularPyramidRotationZ = 0;
//
//    private int initialCylinderRotationX = 0;
//    private int initialCylinderRotationY = 0;
//    private int initialCylinderRotationZ = 0;
//
//    private int initialBaseRotationX = 0;
//    private int initialBaseRotationY = 0;
//    private int initialBaseRotationZ = 0;


    private float aspect; // calculate the aspect ratio of the background
    private float aspectP; // calculate the aspect ratio of the palette

    // indicate if we have finished the game or want to start a new game
    private boolean gameFinished = false;
    private boolean newGame = true;

    // translate the blueprint
    private float translateX;
    private float translateY;
    private float translateZ;

    // Scale the shape added into the blueprint
    private float scale = 1;

    // manipulate the blueprint
    private double blueprintConstant = 0.1;

    // initialize our camera class
    private Camera camera;

    // set the net thingy
    private boolean drawNetOnShapes = false;

    public Project(){

        // gathers information about the current hardware & software configuration
        // to allow us render graphics to our screen
        GLProfile profile = GLProfile.getDefault();

        // specify a set of capabilities that our rendering should support
        GLCapabilities caps = new GLCapabilities(profile);
        caps.setAlphaBits(8); // set the number of bits for the color buffer alpha component
        caps.setDepthBits(24); // set the number of bits for the depth buffer
        caps.setDoubleBuffered(true); // reduce flicking and provide smooth animation
        caps.setStencilBits(8); // mask pixels in an image to product special effects

        SwingUtilities.invokeLater(() -> {

            // create the openGL rendering canvas
            canvas = new GLCanvas();

            // set the desired frame size upon launch
            canvas.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            // listen for functions to be called when a specific event happens
            canvas.addGLEventListener(this);
            canvas.addKeyListener(this); //receive keyboard events
            canvas.addMouseListener(this); // notify when the mouse state changes
            canvas.setFocusable(true); // get's the focus state of the component
            canvas.requestFocus(); // allow user input via the keyboard
            canvas.requestFocusInWindow(); // ensures the window gains focus once launched

            // initialize the FPSAnimator
            animator = new FPSAnimator(canvas, FPS, true);

            // initialize the jFrame constructor
            frame = new JFrame();

            // initialize the buttons, checkbox, labels and
            // set a preferred dimensions
            removeButton = new JButton("Remove");
            addButton = new JButton("Add");
            finishButton =  new JButton("Finish");
            quitButton = new JButton("Quit");
            helpButton = new JButton("Help");
            newGameButton = new JButton("New Game");

            removeButton.setPreferredSize(new Dimension(100, 20));
            addButton.setPreferredSize(new Dimension(100, 20));
            finishButton.setPreferredSize(new Dimension(100, 20));
            quitButton.setPreferredSize(new Dimension(100, 20));
            helpButton.setPreferredSize(new Dimension(100, 20));
            newGameButton.setPreferredSize(new Dimension(100, 20));

            // initialize the JLabel
            label = new JLabel("Click On The Help Button To Read Game Instructions");

            // initialize the JCheckbox
            lightOnOff = new JCheckBox("Turn Light On/Off", true);
            ambientLight = new JCheckBox("Ambient Light", false);
            globalAmbientLight = new JCheckBox("Global Ambient Light", false);
            specularLight = new JCheckBox("Specular Light", false);
            diffuseLight = new JCheckBox("Diffuse Light", false);

            // create a layout for the buttons (2,2) grid
            JPanel windowPanel = new JPanel();
            windowPanel.setLayout(new GridLayout(2, 2));

            // create the panel for the first row
            JPanel topPanel = new JPanel();
            topPanel.add(removeButton);
            topPanel.add(addButton);
            topPanel.add(globalAmbientLight);
            topPanel.add(lightOnOff);
            topPanel.add(ambientLight);
            topPanel.add(diffuseLight);
            topPanel.add(specularLight);

            windowPanel.add(topPanel);

            // create the panel for the second row
            JPanel bottomPanel = new JPanel();
            bottomPanel.add(label);
            bottomPanel.add(helpButton);
            bottomPanel.add(finishButton);
            bottomPanel.add(newGameButton);
            bottomPanel.add(quitButton);

            windowPanel.add(bottomPanel);

            // add the windowPanel to the frame
            frame.add(windowPanel, BorderLayout.SOUTH);

            // set the component to false so once it's clicked
            // an action is the performed
            ambientLight.setFocusable(false);
            lightOnOff.setFocusable(false);
            globalAmbientLight.setFocusable(false);
            diffuseLight.setFocusable(false);
            specularLight.setFocusable(false);

            addButton.addActionListener( e -> {
                if(e.getSource() == addButton){
                    if(traverse == 1){
                        cube_idn = nameId;
                    } else if (traverse == 2) {
                        rectangularpyramid_idn = nameId;
                    } else if (traverse == 3) {
                        cylinder_idn = nameId;
                    } else if (traverse == 4) {
                        base_idn = nameId;
                    }
                }
//                        if(traverse == 1){
//                            cube_idn = nameId;
//                            // Set initial random rotation
//                            initialCubeRotationX = (int)(Math.random() * 360);
//                            initialCubeRotationY = (int)(Math.random() * 360);
//                            initialCubeRotationZ = (int)(Math.random() * 360);
//                        } else if (traverse == 2) {
//                            rectangularpyramid_idn = nameId;
//                            initialRectangularPyramidRotationX = (int)(Math.random() * 360);
//                            initialRectangularPyramidRotationY = (int)(Math.random() * 360);
//                            initialRectangularPyramidRotationZ = (int)(Math.random() * 360);
//                        } else if (traverse == 3) {
//                            cylinder_idn = nameId;
//                            initialCylinderRotationX = (int)(Math.random() * 360);
//                            initialCylinderRotationY = (int)(Math.random() * 360);
//                            initialCylinderRotationZ = (int)(Math.random() * 360);
//                        } else if (traverse == 4) {
//                            base_idn = nameId;
//                            initialBaseRotationX = (int)(Math.random() * 360);
//                            initialBaseRotationY = (int)(Math.random() * 360);
//                            initialBaseRotationZ = (int)(Math.random() * 360);
//                        }
//                    }
                addButton.setFocusable(false);
            });

            removeButton.addActionListener(e -> {
                if(traverse == 1){
                    cube_idn = 0;
                } else if (traverse == 2) {
                    rectangularpyramid_idn = 0;
                } else if (traverse == 3) {
                    cylinder_idn = 0;
                } else if (traverse == 4) {
                    base_idn = 0;
                }
                removeButton.setFocusable(false);
            });

            finishButton.addActionListener(e -> {
                if(e.getSource() == finishButton){
                    gameFinished = true;
                    currentAngleOfVisibleField = 80;
                    translateY = -1;
                }
                finishButton.setFocusable(false);
            });

            helpButton.addActionListener( e -> {
                if (e.getSource() == helpButton) {

                    JOptionPane.showMessageDialog(frame, "Instructions: \n" +
                                    "W - traverse through the blueprint\n" +
                                    "A - reduce the scale of the shape inserted into the blueprint\n" +
                                    "S - increase the scale of the shape inserted into the blueprint\n" +
                                    "Z - increase the scale of the blueprint\n" +
                                    "X - reduce the scale of the blueprint\n" +
                                    "I - move the blueprint (translate) on the z-axis in positive direction\n" +
                                    "O - move the blueprint (translate) on the z-axis in negative direction\n" +
                                    "J - move the blueprint (translate) on the x-axis in positive direction \n" +
                                    "K - move the blueprint (translate) on the x-axis in negative direction\n" +
                                    "N - move the blueprint (translate) on the y-axis in positive direction\n" +
                                    "M - move the blueprint (translate) on the y-axis in negative direction\n" +


                                    "Add Button - after selecting a shape from the palette, you can add it to the selected blueprint shape by the Add button\n" +
                                    "Remove Button - after selecting a shape from the palette, you can remove it from the selected blueprint shape by the Remove button\n" +

                                    "Finish Button - after the game finished, by pressing on the finish button, you can see your results\n" +
                                    "New Game Button - generate a new game\n" +
                                    "Quit Button - quit from the game \n" +
                                    "Light - you can enable/disable different light models by checking/unchecking  the light chekboxes (global ambient light, ambient, diffuse and specular)\n" +


                                    "+ (Numerical Keypad 9)- zoom in\n" +
                                    "- (Numerical Keypad 9)- zoom out\n" +
                                    "Left arrow - negative rotation around the x-axis of the blueprint\n" +
                                    "Right arrow - positive rotation around the x-axis of the blueprint \n" +
                                    "Up arrow - negative rotation around the y-axis of the blueprint\n" +
                                    "Down arrow - positive rotation around the y-axis of the blueprint\n" +
                                    "1 (Numerical Keypad 1) - positive rotation around the x-axis of the shape inserted into the blueprint\n" +
                                    "3 (Numerical Keypad 3)- negative rotation around the x-axis of the shape inserted into the blueprint\n" +
                                    "4 (Numerical Keypad 4)- positive rotation around the y-axis of the shape inserted into the blueprint\n" +
                                    "6 (Numerical Keypad 6)- negative rotation around the y-axis of the shape inserted into the blueprint\n" +
                                    "7 (Numerical Keypad 7)- positive rotation around the z-axis of the shape inserted into the blueprint\n" +
                                    "9 (Numerical Keypad 9)- negative rotation around the z-axis of the shape inserted into the blueprint\n"
                            , "Help", JOptionPane.INFORMATION_MESSAGE);
                }
                helpButton.setFocusable(false);
            });

            quitButton.addActionListener(e -> {
                if(e.getSource() == quitButton){
                    animator.stop();
                    System.exit(0);
                }
                quitButton.setFocusable(false);
            });

            newGameButton.addActionListener(e -> {
                if(e.getSource() == newGameButton){
                    newGame = true;
                    drawNetOnShapes = false;
                    gameFinished = false;
                }
                newGameButton.setFocusable(false);
            });

            frame.getContentPane().add(canvas);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread(() -> {
                        if(animator.isStarted()){
                            animator.stop();
                            System.exit(0);
                        }
                    }).start();
                }
            });

            // Initialize the camera, set the position of it
            camera = new Camera();
            camera.lookAt(-5, 0, 3, // look from camera XYZ
                    0, 0, 0, // look at the origin
                    0, 1, 0); // positive Y up vector (roll of the camera)

            // set the size of the shape while zoom in/out
            camera.setScale(15);

            // once the application starts, the window size will fit our
            // preferred layout
            frame.pack();
            frame.setTitle(TITLE);
            frame.setVisible(true);
            animator.start();
        });

    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glClearColor(0.95f, 0.95f, 1f, 0); // RGBA

        // enable the depth buffer to allow us represent depth information in 3d space
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING); // enable lighting calculation
        gl.glEnable(GL2.GL_LIGHT0); // initial value for light (1,1,1,1) -> RGBA
        gl.glEnable(GL2.GL_NORMALIZE);

        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
        gl.glMateriali(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 100);

        // initialize different light sources
        float [] ambient = {0.1f, 0.1f, 0.1f, 1.0f};
        float [] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        float [] specular = {1.0f, 1.0f, 1.0f, 1.0f};

        // configure different light sources
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, specular, 0);

        gl.glClearDepth(1.0f); // set clear depth value to farthest
        gl.glEnable(GL2.GL_DEPTH_TEST); // enable depth testing
        gl.glDepthFunc(GL2.GL_LEQUAL); // the type of depth test to do
        // perspective correction
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glShadeModel(GL2.GL_SMOOTH); // blend colors nicely & have smooth lighting

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // initialize the textures to use
        glu = GLU.createGLU(gl); // get Gl utilities

        for (int i=0; i<textureFileNames.length; i++){
            try{
                URL textureURL = getClass()
                        .getClassLoader()
                        .getResource("textures/"+textureFileNames[i]);

                if(textureURL != null){
                    BufferedImage image = ImageIO.read(textureURL);
                    ImageUtil.flipImageVertically(image);

                    textures[i] = AWTTextureIO.newTexture(GLProfile.getDefault(),
                            image,
                            true);

                    textures[i].setTexParameteri(gl,
                            GL2.GL_TEXTURE_WRAP_S,
                            GL2.GL_REPEAT);

                    textures[i].setTexParameteri(gl,
                            GL2.GL_TEXTURE_WRAP_T,
                            GL2.GL_REPEAT);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // enables a texture by default
        textures[0].enable(gl);

        // initialize the font to use when rendering our text
        textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12));
        textMatch = new TextRenderer(new Font("SansSerif", Font.BOLD, 20));
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) { }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        // clears both the color and depth buffer before rendering
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // check if we are in selection model
        if(inSelectionMode){
            // allow the user to pick a shape from the palette and add it to
            // the blueprint
            pickModels(glAutoDrawable);
        }else{
            palette(glAutoDrawable); // add the palette (left side)
            drawBlueprint(glAutoDrawable); // draws the blueprint (right side)
            drawBackground(glAutoDrawable); // draws the background (rainbow)
        }

        camera.apply(gl); // add the camera

        float [] zero = {0, 0, 0, 1};
        lights(gl, zero); // add the lights

        // turn on the global ambient light
        if(globalAmbientLight.isSelected()){ // if it's checked
            gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT,
                    new float[] {0.2F, 0.2F, 0.2F, 1},
                    0);

        }else{
            gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT,
                    zero,
                    0);
        }

        // add a specular light to make the object shiny
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,
                GL2.GL_SPECULAR,
                new float[] {0.2F, 0.2F, 0.2F, 1}, 0);

        // check if the game is finished, print the total matched shapes
        if(gameFinished){
            printResult();
        }

        // print the match shapes, once we've inserted it correctly
        if(!gameFinished){
            printMatch();
        }

        // reset the game if the user decides
        if(newGame){
            newGame();
            newGame = false;
        }

        // message to begin the game
        if (!drawNetOnShapes) {
            displayMessage();
        }

    }

    private void message(int x, int y){
        textMatch.beginRendering(WINDOW_WIDTH, WINDOW_HEIGHT);
        textMatch.setColor(0.3f, 0.3f, 0.3f, 1);
        textMatch.draw("please press ENTER to start", x, y);
        textMatch.endRendering();
    }

    private void displayMessage() {
        int x = WINDOW_WIDTH - 300; // Adjust this value as needed
        int y = WINDOW_HEIGHT - 40; // Adjust this value as needed
        message(x, y);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
        WINDOW_HEIGHT = h;
        WINDOW_WIDTH = w;
    }

    private void newGame() {

                // Reset variables
                translateX = 0;
                translateY = 0;
                translateZ = 0;

                currentAngleOfVisibleField = 85;
                currentAngleOfRotationX = 0;
                currentAngleOfRotationY = 0;

                scale = 1;

                cube_idn = 0;
                rectangularpyramid_idn = 0;
                cylinder_idn = 0;
                base_idn = 0;

                traverse = 0;

                scaleCube = 0.5f;
                scaleRectangularpyramid = 0.5f;
                scaleCylinder = 0.5f;
                scaleBase = 0.5f;

                angleCubeX = 0;
                angleCubeY = 0;
                angleCubeZ = 0;

                angleRectangularPyramidX = 0;
                angleRectangularPyramidY = 0;
                angleRectangularPyramidZ = 0;

                angleCylinderX = 90;
                angleCylinderY = 90;
                angleCylinderZ = 90;

                angleBaseX = 0;
                angleBaseY = 0;
                angleBaseZ = 0;

                isCubeMatched = false;
                isRectangularPyramidMatched = false;
                isCylinderMatched = false;
                isBaseMatched = false;
    }

    private void lights(GL2 gl, float [] zero) {
        gl.glColor3d(0.5, 0.5, 0.5);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, zero, 0);

        if(lightOnOff.isSelected()){
            gl.glDisable(GL2.GL_LIGHTING);
        }else{
            gl.glEnable(GL2.GL_LIGHTING);
        }

        float [] ambient = {0.1f, 0.1f, 0.1f, 1};
        float [] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        float [] specular = {1.0f, 1.0f, 1.0f, 1.0f};

        // ambient light
        if(ambientLight.isSelected()){
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, ambient, 0);
            gl.glEnable(GL2.GL_LIGHT0);
        }else{
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, zero, 0);
            gl.glDisable(GL2.GL_LIGHT0);
        }

        // diffuse light
        if(diffuseLight.isSelected()){
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, diffuse, 0);
            gl.glEnable(GL2.GL_LIGHT1);
        }else{
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, zero, 0);
            gl.glDisable(GL2.GL_LIGHT1);
        }

        // specular light
        if(specularLight.isSelected()){
            float [] shininess = {0.1f, 0.1f, 0.1f, 1};
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, specular, 0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess, 0);
            gl.glEnable(GL2.GL_LIGHT2);
        }else{
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, zero, 0);
            gl.glDisable(GL2.GL_LIGHT2);
        }

        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, zero, 0);
    }

    private void drawBackground(GLAutoDrawable glAutoDrawable){
        try {
            GL2 gl = glAutoDrawable.getGL().getGL2();

            // define the characteristics of our camera such as clipping, point of view...
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity(); // reset the current matrix

            // set the window screen
            gl.glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

            // calculate the aspect ratio
            aspect = (float) WINDOW_HEIGHT / ((float) WINDOW_WIDTH);

            // define the orthographic view
            gl.glOrtho(
                    (float) -10/2, // left vertical clipping plane
                    (float) 10/2, // right vertical clipping plane
                    (-10*aspect) / 2, // bottom horizontal clipping plane
                    (10*aspect) / 2, // top horizontal clipping plane
                    0, // near depth clipping plane
                    100 // near farther clipping plane
            );

            // define the position orientation of the camera
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

            gl.glPushMatrix();
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);

            textures[3].bind(gl); // specify the texture to use

            gl.glTranslated(0, 0, -100);
            gl.glScalef(1.75f, 1, 1);
            gl.glColor3f(1f, 1f, 1f);

            double radius = 5;

            // add the texture to our background
            gl.glBegin(GL2.GL_POLYGON);
            gl.glNormal3f(0, 0, 1); // lighting calculation

            // top left corner of a square
            gl.glTexCoord2d(0, 1);
            gl.glVertex2d(-radius, radius);

            // bottom left corner of a square
            gl.glTexCoord2d(0, 0);
            gl.glVertex2d(-radius, -radius);

            // bottom right corner of a square
            gl.glTexCoord2d(1, 0);
            gl.glVertex2d(radius, -radius);

            // top right corner of a square
            gl.glTexCoord2d(1, 1);
            gl.glVertex2d(radius, radius);

            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glEnd();
            gl.glPopMatrix();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void drawBlueprint(GLAutoDrawable glAutoDrawable){
        GL2 gl = glAutoDrawable.getGL().getGL2();

        // define the point of view of the blueprint (right side)
        gl.glViewport(WINDOW_WIDTH/8, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(currentAngleOfVisibleField, 1.f*WINDOW_WIDTH/WINDOW_HEIGHT, 1, 100);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        setObserver();

        // draw the blueprint
        gl.glPushMatrix();

        // change the orientation of the blueprint
        gl.glTranslated(translateX, translateY, translateZ);
        gl.glScalef(scale, scale, scale);
        gl.glRotated(currentAngleOfRotationX, 1, 0, 0);
        gl.glRotated(currentAngleOfRotationY, 0, 1, 0);

        // add some texture on the blueprint
        if(drawNetOnShapes)
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        else
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_POLYGON);
        textures[1].bind(gl);
        gl.glEnable(GL.GL_TEXTURE_2D);

        // Base
        if (traverse == BASE_ID)
            gl.glLineWidth(3);

        // Position the base at ground level
        gl.glPushMatrix();
        gl.glTranslated(0, -2.2, 0);
        Shape.base(gl);
        gl.glPopMatrix();

        gl.glLineWidth(1); // Reset

        // Cylinders in blueprint
        if (traverse == CYLINDER_ID)
            gl.glLineWidth(3);

        for (double x : CYLINDER_X_POSITIONS) {
            for (double z : new double[]{CYLINDER_Z_POSITIONS[0], CYLINDER_Z_POSITIONS[4]}) {
                gl.glPushMatrix();
                gl.glTranslated(x, CYLINDER_BASE_HEIGHT, z);
                Shape.cylinder(gl);
                gl.glPopMatrix();
            }
        }

        for (double z : CYLINDER_Z_POSITIONS) {
            for (double x : new double[]{CYLINDER_X_POSITIONS[0], CYLINDER_X_POSITIONS[4]}) {
                if (z != CYLINDER_Z_POSITIONS[0] && z != CYLINDER_Z_POSITIONS[4]) {
                    gl.glPushMatrix();
                    gl.glTranslated(x, CYLINDER_BASE_HEIGHT, z);
                    Shape.cylinder(gl);
                    gl.glPopMatrix();
                }
            }
        }

        gl.glLineWidth(1); // Reset


        //roof
        if (traverse == RECTANGULAR_PYRAMID_ID)
            gl.glLineWidth(3);

        // Position the roof on top of the pillars
        gl.glPushMatrix();
        gl.glTranslated(0, PYRAMID_BASE_HEIGHT, 0);
        Shape.rectangularPyramid(gl, PYRAMID_WIDTH, PYRAMID_DEPTH, PYRAMID_HEIGHT, true);
        gl.glPopMatrix();

        gl.glLineWidth(1);

        gl.glDisable(GL.GL_TEXTURE_2D);

        // Allows the user to select a shape from the palette and deploy it unto the blueprint
        deployShapeFromPaletteToBlueprint(glAutoDrawable, 1); // wall
        deployShapeFromPaletteToBlueprint(glAutoDrawable, 2); // roof
        deployShapeFromPaletteToBlueprint(glAutoDrawable, 3); // pillar
        deployShapeFromPaletteToBlueprint(glAutoDrawable, 4); // floor

        gl.glPopMatrix();
    }

    private void deployShapeFromPaletteToBlueprint(GLAutoDrawable drawable, int choice){
        //  Allows the user to select a shape from the palette and deploy it unto the
        switch (choice){
            case 1: // cube
                addShapeToBlueprint(drawable,
                        0, 0, 0, // translation
                        angleCubeX, angleCubeY, angleCubeZ, // angle
                        scaleCube, rotate, // scale & rotate
                        cube_idn); // shape to be drawn
                break;
            case 2: // rectangular
                addShapeToBlueprint(drawable,
                        0, PYRAMID_BASE_HEIGHT, 0,  // translation
                        angleRectangularPyramidX, angleRectangularPyramidY, angleRectangularPyramidZ, // angle
                        scaleRectangularpyramid, rotate, // scale & rotate
                        rectangularpyramid_idn); // shape to be drawn
                break;
            case 3: // cylinder (pillars)
                // For the pillars, position them around the base
                for (double x : CYLINDER_X_POSITIONS) {
                    for (double z : new double[]{CYLINDER_Z_POSITIONS[0], CYLINDER_Z_POSITIONS[4]}) {
                        addShapeToBlueprint(drawable, x, CYLINDER_Y_COORDINATE, z,
                                angleCylinderX, angleCylinderY, angleCylinderZ,
                                scaleCylinder, rotate,
                                cylinder_idn);
                    }
                }

                for (double z : CYLINDER_Z_POSITIONS) {
                    for (double x : new double[]{CYLINDER_X_POSITIONS[0], CYLINDER_X_POSITIONS[4]}) {
                        if (z != CYLINDER_Z_POSITIONS[0] && z != CYLINDER_Z_POSITIONS[4]) {
                            addShapeToBlueprint(drawable, x, CYLINDER_Y_COORDINATE, z,
                                    angleCylinderX, angleCylinderY, angleCylinderZ,
                                    scaleCylinder, rotate,
                                    cylinder_idn);
                        }
                    }
                }
                break;

            case 4: // base
                addShapeToBlueprint(drawable,
                        0, -2.2, 0, // translation
                        angleBaseX, angleBaseY, angleBaseZ, // angle
                        scaleBase, rotate, // scale & rotate
                        base_idn); // shape to be drawn
                break;
            default:
                break;
        }
    }

    private void addShapeToBlueprint(GLAutoDrawable glAutoDrawable,
                                     double tX, double tY, double tZ, // translation
                                     float angleX, float angleY, float angleZ, // angle
                                     float scale, float rotate, // scale & rotate
                                     int shapeId){ // shape to be drawn

        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glPushMatrix();

        // Apply translation first
        gl.glTranslated(tX, tY, tZ);

        // Apply scaling next
        gl.glScalef(scale, scale, scale); // Ensure scaling is applied correctly

        // Apply rotation last
        gl.glRotatef(angleX, 1, 0, 0);
        gl.glRotatef(angleY, 0, 1, 0);
        gl.glRotatef(angleZ, 0, 0, 1);

//        // Apply initial random rotation and user rotation
//        switch (shapeId) {
//            case CUBE_ID:
//                gl.glRotatef(angleX + initialCubeRotationX, 1, 0, 0);
//                gl.glRotatef(angleY + initialCubeRotationY, 0, 1, 0);
//                gl.glRotatef(angleZ + initialCubeRotationZ, 0, 0, 1);
//                break;
//            case RECTANGULAR_PYRAMID_ID:
//                gl.glRotatef(angleX + initialRectangularPyramidRotationX, 1, 0, 0);
//                gl.glRotatef(angleY + initialRectangularPyramidRotationY, 0, 1, 0);
//                gl.glRotatef(angleZ + initialRectangularPyramidRotationZ, 0, 0, 1);
//                break;
//            case CYLINDER_ID:
//                gl.glRotatef(angleX + initialCylinderRotationX, 1, 0, 0);
//                gl.glRotatef(angleY + initialCylinderRotationY, 0, 1, 0);
//                gl.glRotatef(angleZ + initialCylinderRotationZ, 0, 0, 1);
//                break;
//            case BASE_ID:
//                gl.glRotatef(angleX + initialBaseRotationX, 1, 0, 0);
//                gl.glRotatef(angleY + initialBaseRotationY, 0, 1, 0);
//                gl.glRotatef(angleZ + initialBaseRotationZ, 0, 0, 1);
//                break;
//        }

        // Enable texturing and bind the texture
        gl.glEnable(GL.GL_TEXTURE_2D);
        textures[1].bind(gl); // Assuming texture[1] is the desired texture

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        pickShape(glAutoDrawable, shapeId);

        gl.glDisable(GL.GL_TEXTURE_2D); // Disable texturing after drawing the shape

        gl.glPopMatrix();
    }


    private void pickShape(GLAutoDrawable glAutoDrawable, int nameID){
        GL2 gl = glAutoDrawable.getGL().getGL2();
        textures[1].bind(gl);
        switch (nameID){
            case CUBE_ID:
                Shape.cube(gl);
                break;
            case RECTANGULAR_PYRAMID_ID:
                double width = 11.4; // Adjust width as needed
                double depth = 7.4;  // Adjust depth as needed
                double height = 3.0; // Adjust height as needed
                boolean makeTextureCoordinate = true;
                Shape.rectangularPyramid(gl, width, depth, height, makeTextureCoordinate);
                break;
            case CYLINDER_ID:
                Shape.cylinder(gl);
                break;
            case BASE_ID:
                Shape.base(gl);
                break;
        }
        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    // set the position of the camera
    private void setObserver() {
        glu.gluLookAt(-1, 2, 10.0, // look from camera XYZ
                0.0, 0.0, 0.0, // look at the origin
                0.0, 1.0, 0.0); // positive Y up vector
    }

    // the palette drawn on the left side of the screen
    private void palette(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        // apply the subsequent matrix operation to the modelview matrix stack
        gl.glMatrixMode(GL2.GL_MODELVIEW); // convert local coordinates to world space
        gl.glLoadIdentity(); // reset the value

        // apply the subsequent matrix operation to the projection matrix stack
        gl.glMatrixMode(GL2.GL_PROJECTION); // add perspective to the current operation
        gl.glLoadIdentity();

        // specify the lower left of the viewport rectangle in pixel (0,0), width and height
        gl.glViewport(0, 0, WINDOW_WIDTH/3, WINDOW_HEIGHT);

        aspectP = (float) WINDOW_HEIGHT / ((float) WINDOW_WIDTH /3);

        // multiply the current matrix with an orthographic matrix
        gl.glOrtho(
                (float) -10/2, // left vertical clipping plane
                (float) 10 / 2, // right vertical clipping plane
                (-10 * aspectP) / 2, // bottom horizontal clipping plane
                (10 * aspectP) / 2, // top horizontal clipping plane
                1, // near depth clipping plane
                11 // near farther clipping plane
        );
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        // draw the background of the palette
        paletteBackground(glAutoDrawable);
        gl.glLoadIdentity();

        // set the camera for the palette
        glu.gluLookAt(-1, 2, 10.0, // look from camera XYZ
                0.0, 0.0, 0.0, // look at the origin
                0.0, 1.0, 0.0); // positive Y up vector

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // draw the shape on top of the background palette
        if(drawNetOnShapes) {
            drawPaletteShape(glAutoDrawable, 1); // draws a cube
            drawPaletteShape(glAutoDrawable, 2); // draws a rectangular pyramid
            drawPaletteShape(glAutoDrawable, 3); // draws a cylinder
            drawPaletteShape(glAutoDrawable, 4); // draw a base
        }
    }
    private void paletteBackground(GLAutoDrawable glAutoDrawable) {
        try {
            GL2 gl = glAutoDrawable.getGL().getGL2(); // get the openGL graphics context
            gl.glPushMatrix();

            // enable the server-side GL capabilities for texture
            gl.glEnable(GL2.GL_TEXTURE_2D);

            // set different texture parameters
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);

            // set the texture to use
            textures[2].bind(gl);

            gl.glTranslated(-1.35f, -2f, -10f);
            gl.glScalef(3.5f, 5f, 0f);
            gl.glColor3f(1f, 1f, 1f);

            Shape.square(gl, 2, true);

            gl.glDisable(GL2.GL_TEXTURE_2D);

            gl.glPopMatrix();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void drawPaletteShape(GLAutoDrawable drawable, int shapeChoice){
        switch (shapeChoice){
            case CUBE_ID: // draw a cube
                paletteShape(drawable,
                        -3.5f, -2.0f, 0f, 1f, 1);
                break;
            case RECTANGULAR_PYRAMID_ID: // draw a rectangular pyramid
                paletteShape(drawable,
                        -1.5f, 0f, 0f, 0.2f,2);
                break;
            case CYLINDER_ID: // draw a cylinder
                paletteShape(drawable,
                        -0.5f, -0.5f, 3f, 0.6f, 3);
                break;
            case BASE_ID:
                paletteShape(drawable,
                        -2.1f, 3.5f, 7f, 0.5f, 4);
                break;
        }
    }
    private void paletteShape(GLAutoDrawable drawable,
                              float tX, float tY, float tZ, // translation
                              float randomScale, // the size of the shape
                              int shapeId // the choice of shape
    ){
        GL2 gl = drawable.getGL().getGL2();
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // control the transformation applied to the object
        gl.glPushMatrix();
        gl.glTranslated(tX, tY, tZ); // add the translation matrix
        gl.glScalef(randomScale, randomScale, randomScale);
        textures[1].bind(gl);
        gl.glEnable(GL.GL_TEXTURE_2D);
        pickShape(drawable, shapeId);
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }

    /* Allows the user to picks object from the palette and draw it on the blueprint */
    private void pickModels(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        // start picking objects from the screen
        startPicking(glAutoDrawable);

        // enables the user pick objects from the palette and
        // deploy them to the blueprint
        palettePicking(glAutoDrawable);

        gl.glPushName(CUBE_ID);
        drawPaletteShape(glAutoDrawable, 1); // draws a cube
        gl.glPopName();

        gl.glPushName(RECTANGULAR_PYRAMID_ID);
        drawPaletteShape(glAutoDrawable, 2); // draws a rectangular pyramid
        gl.glPopName();

        gl.glPushName(CYLINDER_ID);
        drawPaletteShape(glAutoDrawable, 3); // draws a cylinder
        gl.glPopName();

        gl.glPushName(BASE_ID);
        drawPaletteShape(glAutoDrawable, 4); // draws a sphere
        gl.glPopName();

        // we are done picking
        endPicking(glAutoDrawable);
    }

    private void startPicking(GLAutoDrawable glAutoDrawable){
        GL2 gl = glAutoDrawable.getGL().getGL2();
        // determine which shape are to be drawn on the blueprint
        selectBuffer = BufferUtil.newIntBuffer(BUFSIZE);
        gl.glSelectBuffer(BUFSIZE, selectBuffer);
        gl.glRenderMode(GL2.GL_SELECT);
        gl.glInitNames();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    private void palettePicking(GLAutoDrawable glAutoDrawable){
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();

        gl.glLoadIdentity();
        int [] viewport = new int[4];
        float [] projectionMatrix = new float[16];

        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        viewport[0] = 0;
        viewport[1] = 0;
        viewport[2] = WINDOW_WIDTH / 3;
        viewport[3] = WINDOW_HEIGHT;

        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projectionMatrix, 0);

        // define the picking region
        glu.gluPickMatrix((double) xCursor,
                (double) (viewport[3] - yCursor),
                1.0,
                1.0,
                viewport,
                0);
        gl.glMultMatrixf(projectionMatrix, 0);
        gl.glOrtho((float) -10/2,
                (float) 10/2,
                (-10*aspectP) / 2,
                (10*aspectP) / 2,
                1,
                11);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(-1, 2, 10.0,
                0.0, 0.0, 0.0,
                0.0, 1.0, 0.0);
        gl.glPopMatrix();
    }

    private void endPicking(GLAutoDrawable glAutoDrawable){
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glFlush();

        int numHits = gl.glRenderMode(GL2.GL_RENDER);
        processHits(glAutoDrawable, numHits);
        inSelectionMode = false;
    }

    private void processHits(GLAutoDrawable glAutoDrawable, int numHits) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        if(numHits == 0)  return;

        // store the Id's for what was selected
        int selectedNameId = 0;
        float smallestZ = -1.0f;
        boolean isFirstLoop = true;
        int offset = 0;

        for (int i = 0; i < numHits; i++) {
            int numNames = selectBuffer.get(offset);
            offset++;

            float minZ = getDepth(offset);
            offset++;

            // store the smallest z value
            if(isFirstLoop){
                smallestZ = minZ;
                isFirstLoop = false;
            }else{
                if(minZ < smallestZ){
                    smallestZ = minZ;
                }
            }

            float maxZ = getDepth(offset);
            offset++;

            for (int j = 0; j < numNames; j++) {
                nameId = selectBuffer.get(offset);
                System.out.println(idToString(nameId)+" \n");
                if(j == (numNames - 1)){
                    if(smallestZ == minZ){
                        selectedNameId = nameId;
                    }
                }
                offset++;
            }
        }
    }

    private String idToString(int nameId){
        if(nameId == CUBE_ID){
            return "palette_cube";
        }else if(nameId == RECTANGULAR_PYRAMID_ID){
            return "palette_rectangular_pyramid";
        } else if (nameId == CYLINDER_ID) {
            return "palette_cylinder";
        } else if (nameId == BASE_ID) {
            return "palette_base";
        } else{
            return "nameId: "+nameId;
        }
    }

    private float getDepth(int offset){
        long depth = (long) selectBuffer.get(offset);
        return (1.0f + ((float) depth / 0x7fffffff)); // 7'fs
    }

    /* Valid the matched shapes and display to screen*/

    // check if the user has correctly added a shape from the palette to the blueprint
    // the scale, rotation, etc... is valid
    private void printMatch() {
        if(traverse == 1){
            // check if the shape matched
            boolean isShapeMatched = (CUBE_ID == cube_idn);

            // check if the scale for the top shape matches
            String isScaleMatched = scaleCheck(scaleCube);

            // check if the rotation matches
            String isRotationMatched = rotationCheck(CUBE_ID, angleCubeX, angleCubeY, angleCubeZ);

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
                isCubeMatched = true;
            }else{
                isCubeMatched = false;
            }
        }
        else if (traverse == 2) {
            boolean isShapeMatched = (RECTANGULAR_PYRAMID_ID == rectangularpyramid_idn); // check if the shape matched

            String isScaleMatched = scaleCheck(scaleRectangularpyramid); // is scale appropriate

            String isRotationMatched = rotationCheck(RECTANGULAR_PYRAMID_ID, angleRectangularPyramidX, angleRectangularPyramidY, angleRectangularPyramidZ); // is rotation correct

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
                isRectangularPyramidMatched = true;
            }else{
                isRectangularPyramidMatched = false;
            }
        }else if(traverse == 3){
            boolean isShapeMatched = (CYLINDER_ID == cylinder_idn); // check if the shape matched

            String isScaleMatched = scaleCheck(scaleCylinder); // is scale appropriate

            String isRotationMatched = rotationCheck(CYLINDER_ID, angleCylinderX, angleCylinderY, angleCylinderZ);

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
                isCylinderMatched = true;
            }else{
                isCylinderMatched = false;
            }
        }else if(traverse == 4){
            boolean isShapeMatched = (BASE_ID == base_idn); // check if the shape matched

            String isScaleMatched = scaleCheck(scaleBase); // is scale appropriate

            String isRotationMatched = rotationCheck(BASE_ID, angleBaseX, angleBaseY, angleBaseZ);

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
                isBaseMatched = true;
            }else{
                isBaseMatched = false;
            }
        }
    }

    private void writeMatch(int x, int y){
        textMatch.beginRendering(WINDOW_WIDTH, WINDOW_HEIGHT);
        textMatch.setColor(0.3f, 0.3f, 0.3f, 1);
        textMatch.draw("Well done! It is Correct", x, y);
        textMatch.endRendering();
    }

    private String scaleCheck(float value) {
        double scaling = Math.round(value * 100.0) / 100.0;
        String text = "";
        if(scaling == 1.0){
            text = "appropriate";
        }else{
            text = "not appropriate";
        }
        return text;
    }

    private String rotationCheck(int shape, int angleX, int angleY, int angleZ) {
        String text = "";
        if(shape == CUBE_ID){ // cube
            // rotation: x, y, z
            boolean checkX = (angleX == 0 || angleX == 90 || angleX == 180 || angleX == 270 || angleX == 360);
            boolean checkY = (angleY == 0 || angleY == 90 || angleY == 180 || angleY == 270 || angleY == 360);
            boolean checkZ = (angleZ == 0 || angleZ == 90 || angleZ == 180 || angleZ == 270 || angleZ == 360);

            if(checkX & checkY & checkZ){
                text = "correct";
            }else{
                text = "incorrect";
            }
        }else if(shape == RECTANGULAR_PYRAMID_ID){ // check for rectangular pyramid
            boolean checkX = (angleX == 0);
            boolean checkY = (angleY == 0 );
            boolean checkZ = (angleZ == 0 );

            if(checkX && checkY && checkZ){
                text = "correct";
            }else{
                text = "incorrect";
            }
        }else if(shape == CYLINDER_ID){ // check for cylinder
            boolean checkX = (angleX == 90);
            boolean checkY = (angleY == 90);
            boolean checkZ = (angleZ == 90);

            if (checkX & checkY & checkZ) {
                text = "correct";
            } else {
                text = "incorrect";
            }
        } else if(shape == BASE_ID){ // check for base
            // rotation: x, y, z
            boolean checkX = (angleX == 0 || angleX == 180 || angleX == 360);
            boolean checkY = (angleY == 0 || angleY == 180 || angleY == 360);
            boolean checkZ = (angleZ == 0 || angleZ == 180 || angleZ == 360);

            if (checkX & checkY & checkZ) {
                text = "correct";
            } else {
                text = "incorrect";
            }
        }
        return text;
    }

    private void printResult(){
        String text = String.format("RESULT: %d/%d shapes matched correctly", matchedShape(), 18);
        writeText(text, (int) (WINDOW_WIDTH/3.5f), WINDOW_HEIGHT-40);
    }

    private void writeText(String text, int x, int y){
        textRenderer.beginRendering(WINDOW_WIDTH, WINDOW_HEIGHT);
        textRenderer.setColor(0.3f, 0.3f, 0.5f, 1);
        textRenderer.draw(text, x, y);
        textRenderer.endRendering();
    }

    private int matchedShape(){
        int match = 0;
        for(int i = 0 ; i < 0; i++)
            if(isCubeMatched) match++;
        for(int i = 0 ; i < 1; i++)
            if(isRectangularPyramidMatched) match++;
        for(int i = 0 ; i < 16; i++)
            if(isCylinderMatched) match++;
        for(int i = 0 ; i < 1; i++)
            if(isBaseMatched) match++;
        return match;
    }

    /* Event listeners for keyboard, mouse clicks, etc..*/
    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode(); // keyboard code for the pressed key

        // traverse through the blueprint
        if(key == KeyEvent.VK_W){
            traverse = traverse + 1;
            if(traverse == TOTAL_NUM_OF_SHAPES+1){
                traverse = 0;
            }
        }
        // ============== BLUEPRINT CONTROLS =================
        else if(key == KeyEvent.VK_Z){ // increase the scale of the blueprint
            if(scale <= 2.1){
                scale += blueprintConstant;
            }
        }
        else if(key == KeyEvent.VK_X){ // decrease the scale of the blueprint
            if(scale >= 0.1){
                scale -= blueprintConstant;
            }
        }
        // move the blueprint (translate) on the z-axis in the positive direction
        else if(key == KeyEvent.VK_I){
            translateZ += blueprintConstant;
        }
        // move the blueprint (translate) on the z-axis in the negative direction
        else if (key == KeyEvent.VK_O) {
            translateZ -= blueprintConstant;
        }
        // move the blueprint (translate) on the x-axis in the positive direction
        else if (key == KeyEvent.VK_J) {
            translateX += blueprintConstant;
        }
        // move the blueprint (translate) on the x-axis in the negative direction
        else if (key == KeyEvent.VK_K) {
            translateX -= blueprintConstant;
        }
        // move the blueprint (translate) on the y-axis in the positive direction
        else if(key == KeyEvent.VK_N){
            translateY += blueprintConstant;
        }
        // move the blueprint (translate) on the y-axis in the negative direction
        else if(key == KeyEvent.VK_M){
            translateY -= blueprintConstant;
        }
        // zoom into our blueprint (The Z axis contains the point of view of the object)
        else if(key == KeyEvent.VK_ADD){
            if(currentAngleOfVisibleField > 10){
                currentAngleOfVisibleField--;
            }
        }
        // zoom out of our blueprint
        else if(key == KeyEvent.VK_SUBTRACT){
            if(currentAngleOfVisibleField < 175){
                currentAngleOfVisibleField++;
            }
        }
        // negative rotation around the x-axis of the blueprint
        else if(key == KeyEvent.VK_LEFT){
            currentAngleOfRotationX++;
        }
        // positive rotation around the x-axis of the blueprint
        else if(key == KeyEvent.VK_RIGHT){
            currentAngleOfRotationX--;
        }
        // negative rotation around the y-axis of the blueprint
        else if(key == KeyEvent.VK_UP){
            currentAngleOfRotationY--;
        }
        // positive rotation around the y-axis of the blueprint
        else if(key == KeyEvent.VK_DOWN){
            currentAngleOfRotationY++;
        }

        // =============== SHAPES ADDED INTO THE BLUEPRINT =====================
        else if(key == KeyEvent.VK_A){ // reduce the scale of the shape inserted into the blueprint
            if(traverse == 1){
                scaleCube -= scaleDelta;
            } else if (traverse == 2) {
                scaleRectangularpyramid -= scaleDelta;
            } else if (traverse == 3) {
                scaleCylinder -= scaleDelta;
            } else if (traverse == 4){
                scaleBase -= scaleDelta;
            }
        }
        else if(key == KeyEvent.VK_S){ // increase the scale of the shape inserted into the blueprint
            if(traverse == 1){
                scaleCube += scaleDelta;
            } else if (traverse == 2) {
                scaleRectangularpyramid += scaleDelta;
            } else if (traverse == 3) {
                scaleCylinder += scaleDelta;
            } else if (traverse == 4){
                scaleBase += scaleDelta;
            }
        }

        // (Numerical Keypad 1) - positive rotation around the x-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD1){
            if(traverse == 1) {
                if (angleCubeX <= 360) {
                    angleCubeX += angleDelta;
                }
            }else if (traverse == 2){
                if (angleRectangularPyramidX <= 360) {
                    angleRectangularPyramidX += angleDelta;
                }
            }else if (traverse == 3){
                if (angleCylinderX <= 360) {
                    angleCylinderX += angleDelta;
                }
            }else if (traverse == 4){
                if (angleBaseX <= 360) {
                    angleBaseX += angleDelta;
                }
            }
        }

        // (Numerical Keypad 3) - negative rotation around the x-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD3){
            if(traverse == 1){
                if(angleCubeX >= 0){
                    angleCubeX -= angleDelta;
                }
            } else if (traverse == 2) {
                if(angleRectangularPyramidX >= 0){
                    angleRectangularPyramidX -= angleDelta;
                }
            }else if(traverse == 3){
                if(angleCylinderX >= 0){
                    angleCylinderX -= angleDelta;
                }
            }else if(traverse == 4){
                if(angleBaseX >= 0){
                    angleBaseX -= angleDelta;
                }
            }

        }

        // (Numerical Keypad 4) - positive rotate around the y-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD4){
            if(traverse == 1){
                if(angleCubeY <= 360){
                    angleCubeY += angleDelta;
                }
            } else if (traverse == 2) {
                if(angleRectangularPyramidY <= 360){
                    angleRectangularPyramidY += angleDelta;
                }
            }else if(traverse == 3){
                if(angleCylinderY <= 360){
                    angleCylinderY += angleDelta;
                }
            }else if(traverse == 4){
                if(angleBaseY <= 360){
                    angleBaseY += angleDelta;
                }
            }
        }

        // (Numerical Keypad 6) - negative rotation around the y-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD6){
            if(traverse == 1){
                if(angleCubeY >= 0){
                    angleCubeY -= angleDelta;
                }
            } else if (traverse == 2) {
                if(angleRectangularPyramidY >= 0){
                    angleRectangularPyramidY -= angleDelta;
                }
            } else if (traverse == 3){
                if(angleCylinderY >= 0){
                    angleCylinderY -= angleDelta;
                }
            } else if(traverse == 4){
                if(angleBaseY >= 0){
                    angleBaseY -= angleDelta;
                }
            }
        }

        // (Numerical Keypad 7) - positive rotation around the z-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD7){
            if(traverse == 1){
                if(angleCubeZ <= 360){
                    angleCubeZ += angleDelta;
                }
            } else if (traverse == 2) {
                if(angleRectangularPyramidZ <= 360){
                    angleRectangularPyramidZ += angleDelta;
                }
            } else if(traverse == 3){
                if(angleCylinderZ <= 360){
                    angleCylinderZ += angleDelta;
                }
            } else if(traverse == 4){
                if(angleBaseZ <= 360){
                    angleBaseZ += angleDelta;
                }
            }
        }

        // (Numerical Keypad 9) - negative rotation around the z-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD9){
            if(traverse == 1){
                if(angleCubeZ >= 0){
                    angleCubeZ -= angleDelta;
                }
            } else if (traverse == 2) {
                if(angleRectangularPyramidZ >= 0){
                    angleRectangularPyramidZ -= angleDelta;
                }
            } else if (traverse == 3) {
                if(angleCylinderZ >= 0){
                    angleCylinderZ -= angleDelta;
                }
            }else if(traverse == 4){
                if(angleBaseZ >= 0){
                    angleBaseZ -= angleDelta;
                }
            }
        }

        // Escape key - stop the animator and exit the game
        else if(key == KeyEvent.VK_ESCAPE){
            animator.stop();
            System.exit(0);
        }

        // Enter key - change blueprint from filled to line on beginning of the game
        else if (key == KeyEvent.VK_ENTER) {
            drawNetOnShapes = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) { }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()){
            case MouseEvent.BUTTON1: { // left click
                xCursor = mouseEvent.getX();
                yCursor = mouseEvent.getY();
                inSelectionMode = true;
                break;
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {}

    @Override
    public void mouseEntered(MouseEvent mouseEvent) { }

    @Override
    public void mouseExited(MouseEvent mouseEvent) { }

    public static void main(String[] args) {
        new Project();
    }
}
