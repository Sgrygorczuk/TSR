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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

    private Stage menuStage;
    private ImageButton[] menuButtons = new ImageButton[2];

    //Logo that's displayed when the game first loads
    private TextureRegion[][] intoPanelTextures;

    int panelCounter = 0;
    int sizeCount = 0;
    boolean letGo = true;
    int stage = 0;
    boolean continueEnd = false;

    /*
    Input: SpaceHops
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    IntroScreen(TSR tsr, int stage) {
        this.tsr = tsr;
        this.stage = stage;
    }

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
        setUpButtons();
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
        switch (stage) {
            case 0: {
                Texture introTexturePath = new Texture(Gdx.files.internal("Sprites/IntroSpriteSheet.png"));
                intoPanelTextures = new TextureRegion(introTexturePath).split(480, 320); //Breaks down the texture into tiles
                break;
            }
            case 1: {
                Texture introTexturePath = new Texture(Gdx.files.internal("Sprites/Stage3Meeting.png"));
                intoPanelTextures = new TextureRegion(introTexturePath).split(480, 320); //Breaks down the texture into tiles
                break;
            }
        }
    }

    private void setUpButtons(){
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuStage); //Gives controll to the stage for clicking on buttons

        Texture fightButtonPath = new Texture(Gdx.files.internal("Sprites/FightButton.png"));
        TextureRegion[][] fightButtonSpriteSheet = new TextureRegion(fightButtonPath).split(258, 138); //Breaks down the texture into tiles

        menuButtons[0] =  new ImageButton(new TextureRegionDrawable(fightButtonSpriteSheet[0][0]), new TextureRegionDrawable(fightButtonSpriteSheet[1][0]));
        menuButtons[0].setPosition(100, WORLD_HEIGHT - menuButtons[0].getHeight()/3f - 5);
        menuButtons[0].setWidth(menuButtons[0].getWidth()/3f);
        menuButtons[0].setHeight(menuButtons[0].getHeight()/3f);
        menuStage.addActor(menuButtons[0]);
        menuButtons[0].setVisible(false);

        menuButtons[0].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                menuButtons[0].setVisible(false);
                menuButtons[1].setVisible(false);
                tsr.setScreen(new FightScreen(tsr, 2));
            }
        });

        fightButtonPath = new Texture(Gdx.files.internal("Sprites/RedeemButton.png"));
        fightButtonSpriteSheet = new TextureRegion(fightButtonPath).split(258, 138); //Breaks down the texture into tiles

        menuButtons[1] =  new ImageButton(new TextureRegionDrawable(fightButtonSpriteSheet[0][0]), new TextureRegionDrawable(fightButtonSpriteSheet[1][0]));
        menuButtons[1].setPosition(380 - menuButtons[1].getWidth()/3f, WORLD_HEIGHT - menuButtons[1].getHeight()/3f - 5);
        menuButtons[1].setWidth(menuButtons[1].getWidth()/3f);
        menuButtons[1].setHeight(menuButtons[1].getHeight()/3f);
        menuStage.addActor(menuButtons[1]);
        menuButtons[1].setVisible(false);

        menuButtons[1].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                menuButtons[0].setVisible(false);
                menuButtons[1].setVisible(false);
                continueEnd = true;
                panelCounter++;
            }
        });

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
            if(stage == 0 || (stage == 1 && panelCounter < 4) || continueEnd){panelCounter++;}
            sizeCount = 0;
            letGo = false;
        }
        else if(!letGo && !Gdx.input.isTouched()){letGo = true;} //Make sure you only click once
        sizeCount++;
        if(stage == 1 && panelCounter == 4 && !continueEnd){
            menuButtons[0].setVisible(true);
            menuButtons[1].setVisible(true);
        }
        if(panelCounter == 8 && stage == 0){tsr.setScreen(new MainScreen(tsr, 0));}
        if(stage == 1 && panelCounter == 7){tsr.setScreen(new FightScreen(tsr, 3));}

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
        if(panelCounter < 8 && stage == 0 || panelCounter < 7 && stage == 1){batch.draw(intoPanelTextures[panelCounter][0], 0, 0);}
        if(stage == 0){drawTextOne();}
        else if(stage == 1){
            drawTextTwo();
        }
        batch.end();

        menuStage.draw();

        batch.begin();
        if(stage == 1 && panelCounter == 4){drawButtonText();}
        batch.end();

    }

    private void drawButtonText(){
        bitmapFont.getData().setScale(0.8f);
        bitmapFont.setColor(Color.RED);
        centerText(bitmapFont, "Revenge", 143, WORLD_HEIGHT-28);

        bitmapFont.setColor(Color.BLUE);
        centerText(bitmapFont, "Forgive", 336, WORLD_HEIGHT-28);
    }

    private void drawTextOne(){
        bitmapFont.setColor(Color.WHITE);
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
                string = addNewLine("Bully wanted Buddy's Change-Gear, but Buddy wasn't going to give it to him.", 60);
                break;
            }
            case 3:{
                string = addNewLine("Bully snached and tossed the Change-Gear to the pavement with a hard thud.", 60);
                break;
            }
            case 4:{
                string = addNewLine("Buddy protested, screamed and yelled but it was for nought. The Change-Gear was done for.", 60);
                break;
            }
            case 5:{
                string = addNewLine("Pieces flew ever which way, with each stomp Foe took on it.                 ", 60);
                break;
            }
            case 6:{
                string = addNewLine("And just for good measure Bully's hands also flew over Buddy.", 60);
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

    private void drawTextTwo(){
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(1f);
        String string = "";
        switch (panelCounter){
            case 0: {
                string = addNewLine("Again we find an innocent citizen walking down the streets.                  ", 60);
                break;
            }
            case 1:{
                string = addNewLine("Only to be met with imposing stature of the Bully.                             ", 60);
                break;
            }
            case 2:{
                string = addNewLine("Business as usual give the Bully the stuff or pay the price              ", 60);
                break;
            }
            case 3:{
                string = addNewLine("But today is different. Today Buddy was ready. No more bullying, no more pain. Buddy was here to save the day.", 60);
                break;
            }
            case 4: {
                string = addNewLine("Will you enact your revenge or forgive the Bully?                 ", 60);
                break;
            }
            case 5:{
                string = addNewLine("The Bully steamed with anger at the 'forgiveness' he was not gonna let Buddy do this to him.", 60);
                break;
            }
            case 6: {
                string = addNewLine("It was time to unleash his final form!\n                                                       ", 60);
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