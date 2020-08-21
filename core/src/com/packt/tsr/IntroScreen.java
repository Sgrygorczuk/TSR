package com.packt.tsr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class IntroScreen extends ScreenAdapter{
    //Screen Dimensions
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 320;

    //Visual objects
    private SpriteBatch batch = new SpriteBatch();			 //Batch that holds all of the textures
    private Viewport viewport;
    private Camera camera;

    //The game object that keeps track of the settings
    private TSR tsr;

    //Font used to write in
    private BitmapFont bitmapFont = new BitmapFont();

    //Logo that's displayed when the game first loads
    private TextureRegion[][] intoPanelTextures;

    int panelCounter = 0;
    int sizeCount = 0;
    boolean letGo = true;

    /*
    Input: SpaceHops
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    IntroScreen(TSR tsr) { this.tsr = tsr;}

    /*
    Input: Dimensions
    Output: Void
    Purpose: Resize the screen when window size changes
    */
    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    /*
    Input: Void
    Output: Void
    Purpose: Set up the the textures and objects
    */
    @Override
    public void show() {
        //Sets up the camera
        showCamera();           //Sets up camera through which objects are draw through
        loadAssets();           //Loads the stuff into the asset manager
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the camera through which all the objects are view through
    */
    private void showCamera(){
        camera = new OrthographicCamera();									//Sets a 2D view
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);	//Places the camera in the center of the view port
        camera.update();													//Updates the camera
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);		//
    }

    /*
    Input: Void
    Output: Void
    Purpose: Loads all the data needed for the assetmanager
    */
    private void loadAssets(){
        Texture  introTexturePath = new Texture(Gdx.files.internal("Sprites/IntroSpriteSheet.png"));
        intoPanelTextures = new TextureRegion(introTexturePath).split(480, 320); //Breaks down the texture into tiles
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: What gets drawn
    */
    @Override
    public void render(float delta) {
        update(delta);       //Update the variables
        draw();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the variable of the progress bar, when the whole thing is load it turn on game screen
    */
    private void update(float delta) {
        if( letGo && (Gdx.input.isTouched() || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))){
            panelCounter++;
            sizeCount = 0;
            letGo = false;
        }
        else if(!letGo && !Gdx.input.isTouched()){letGo = true;} //Make sure you only click once
        sizeCount++;
        if(panelCounter == 8){tsr.setScreen(new MainScreen(tsr));}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the progress
    */
    private void draw() {
        clearScreen();
        //Viewport/Camera projection
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        //Batch setting up texture before drawing buttons
        batch.begin();
        if(panelCounter < 8){batch.draw(intoPanelTextures[panelCounter][0], 0, 0);}
        drawText();
        batch.end();

    }

    private void drawText(){
        bitmapFont.getData().setScale(1f);
        String string = "";
        switch (panelCounter){
            case 0: {
                string = addNewLine("Buddy was heading home watching his favorite show, Dragon Squares Y, on his Change-Gear.", 60);
                break;
            }
            case 1:{
                string = addNewLine("But today he wouldn't be able to finish the show as Foe approached him.", 60);
                break;
            }
            case 2:{
                string = addNewLine("Foe wanted Buddy's Change-Gear, but Buddy wasn't going to give it to him.", 60);
                break;
            }
            case 3:{
                string = addNewLine("Foe snached and tossed the Change-Gear to the pavement with a hard thud.", 60);
                break;
            }
            case 4:{
                string = addNewLine("Buddy protested, screamed and yelled but it was for nought. The Change-Gear was done for.", 60);
                break;
            }
            case 5:{
                string = addNewLine("Pieces flew ever which way, with each stomp Foe took on it.", 60);
                break;
            }
            case 6:{
                string = addNewLine("And just for good measure Foe's hands also flew over Buddy.", 60);
                break;
            }
            case 7:{
                string = addNewLine("But this was the last time. This time Buddy will get his redemption!", 60);
                break;
            }
        }
        if(sizeCount > string.length()){sizeCount = string.length()-1;}
        if(sizeCount < 0){sizeCount = 0;}
        centerText(bitmapFont, string.substring(0, sizeCount), WORLD_WIDTH/2f, 25);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets screen color
    */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /*
    Input: BitmapFont for size and font of text, string the text, and x and y for position
    Output: Void
    Purpose: General purpose function that centers the text on the position
    */
    private void centerText(BitmapFont bitmapFont, String string, float x, float y){
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(bitmapFont, string);
        bitmapFont.draw(batch, string,  x - glyphLayout.width/2, y + glyphLayout.height/2);
    }

    /*
    Input: The given string, length - how many chars do we go till we start a new line
    Output: Void
    Purpose: This function take a string and adds a new line whenever it reaches the length between it's starting position andlengtht,
    if start + length happens to occur on a non space char it goes back to the nearest space char
    */
    private String addNewLine(String str, int length){
        int spaceFound;
        for (int j = 0; length * (j + 1) + j*3 < str.length(); j++) {
            //Finds the new position of where a " " occurs
            spaceFound = str.lastIndexOf(" ", length * (j + 1) + j*3) + 1;
            //Adds in a new line if this is not the end of the string
            if(str.length() >= spaceFound + 1){ str = str.substring(0, spaceFound) + "\n" + str.substring(spaceFound);}
        }
        return str;
    }

    /*
    Input: Void
    Output: Void
    Purpose: Gets rid of all visuals
    */
    @Override
    public void dispose() {

    }
}