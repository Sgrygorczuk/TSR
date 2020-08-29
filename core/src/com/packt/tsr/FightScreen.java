package com.packt.tsr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FightScreen extends ScreenAdapter{
    //Screen Dimensions
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 320;

    //Visual objects
    private SpriteBatch batch = new SpriteBatch();			 //Batch that holds all of the textures
    private Viewport viewport;
    private Camera camera;

    private Stage menuStage;
    private ImageButton[] menuButtons = new ImageButton[1];

    //The game object that keeps track of the settings
    private TSR tsr;

    private Music music;
    private Sound punchOneSFX;
    private Sound punchTwoSFX;
    private Sound victorySFX;
    private Sound laserSFX;

    //Font used to write in
    private BitmapFont bitmapFont = new BitmapFont();

    //Logo that's displayed when the game first loads
    private TextureRegion[][] intoPanelTextures;
    private TextureRegion[][] fightPanelTextures;
    private TextureRegion[][] transitionTextures;
    private TextureRegion[][] winPanelTextures;
    private Texture lifeBarTexture;
    private Texture bloodTexture;
    private Texture frameTexture;
    private Texture blockTexture;

    private Sprite fatBuddy;
    private Sprite thinBuddy;

    int life = 30;
    int levelState = 0; //0 Vs screen, 1 Hit Screen, 2 Win Screen
    int timeCounter = 0; //How many count passed
    int currentFrame = 0;
    float fatBuddyAlpha = 1;
    float thinBuddyAlpha = 0;
    boolean stageTwoNewBodyFlag = false;
    boolean transitionFlag = false;
    int stage;

    //Timing variable used to stop the abbot bounce effect from stacking
    private static final float FRAME_TIME = 0.6F;
    private float frameTimer = FRAME_TIME;

    /*
    Input: SpaceHops
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    FightScreen(TSR tsr, int stage) {
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
        if(tsr.getSFXVolume()){setMusic("Music/VsMusic.wav", 0.5f);}
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
        //Set up deafult paths
        Texture  introTexturePath = new Texture(Gdx.files.internal("Sprites/VSBagSpriteSheet.png"));
        Texture  fightTexturePath = new Texture(Gdx.files.internal("Sprites/BagHitSpriteSheet.png"));
        Texture  transitionTexturePath = new Texture(Gdx.files.internal("Sprites/MasterTranstion.png"));
        Texture  winTexturePath = new Texture(Gdx.files.internal("Sprites/BagWinSpriteSheet.png"));

        Texture buddyTexturePath = new Texture(Gdx.files.internal("Sprites/TransformSpriteSheet.png"));
        TextureRegion[][] buddyTextures = new TextureRegion(buddyTexturePath).split(126, 118); //Breaks down the texture into tiles

        switch (stage){
            case 0:{
                fatBuddy = new Sprite(buddyTextures[0][0]);
                thinBuddy = new Sprite(buddyTextures[0][1]);
                break;
            }
            case 1:{
                //New connections
                introTexturePath = new Texture(Gdx.files.internal("Sprites/VSMasterSprite.png"));
                fightTexturePath = new Texture(Gdx.files.internal("Sprites/MasterFigthSpriteSheet.png"));
                winTexturePath = new Texture(Gdx.files.internal("Sprites/MasterWinSpriteSheet.png"));

                fatBuddy = new Sprite(buddyTextures[0][1]);
                thinBuddy = new Sprite(buddyTextures[0][2]);
                break;
            }
            case 2:{
                introTexturePath = new Texture(Gdx.files.internal("Sprites/VsBullyOne.png"));
                fightTexturePath = new Texture(Gdx.files.internal("Sprites/BullyFightSpriteSheetOne.png"));
                winTexturePath = new Texture(Gdx.files.internal("Sprites/VillanWIn.png"));
                fatBuddy = new Sprite(buddyTextures[0][2]);
                thinBuddy = new Sprite(buddyTextures[0][3]);
                break;
            }
            case 3:{
                introTexturePath = new Texture(Gdx.files.internal("Sprites/VsBullyTwo.png"));
                fightTexturePath = new Texture(Gdx.files.internal("Sprites/BullyFightSpriteSheetTwo_One.png"));
                transitionTexturePath  = new Texture(Gdx.files.internal("Sprites/BullyTransmission.png"));
                winTexturePath = new Texture(Gdx.files.internal("Sprites/GoodguyWin.png"));
                fatBuddy = new Sprite(buddyTextures[0][2]);
                thinBuddy = new Sprite(buddyTextures[0][4]);
                break;
            }
        }

        intoPanelTextures = new TextureRegion(introTexturePath).split(480, 320); //Breaks down the texture into tiles
        fightPanelTextures = new TextureRegion(fightTexturePath).split(480, 320); //Breaks down the texture into tiles
        transitionTextures = new TextureRegion(transitionTexturePath).split(480, 320);
        winPanelTextures = new TextureRegion(winTexturePath).split(480, 320); //Breaks down the texture into tiles

        fatBuddy.setAlpha(1);
        fatBuddy.setPosition(WORLD_WIDTH / 2f - buddyTextures[0][0].getRegionWidth() / 2f, WORLD_HEIGHT / 2f - buddyTextures[0][0].getRegionHeight() / 2f);
        thinBuddy.setAlpha(1);
        thinBuddy.setPosition(WORLD_WIDTH / 2f - buddyTextures[0][0].getRegionWidth() / 2f, WORLD_HEIGHT / 2f - buddyTextures[0][0].getRegionHeight() / 2f);

        lifeBarTexture = new Texture(Gdx.files.internal("Sprites/LifeBar.png"));
        bloodTexture = new Texture(Gdx.files.internal("Sprites/LifeBarBlood.png"));
        frameTexture = new Texture(Gdx.files.internal("Sprites/Frame.png"));
        blockTexture = new Texture(Gdx.files.internal("Sprites/Block.png"));

    }


    private void setUpButtons(){
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuStage); //Gives controll to the stage for clicking on buttons

        Texture fightButtonPath = new Texture(Gdx.files.internal("Sprites/CircleButton.png"));
        final TextureRegion[][] fightButtonSpriteSheet = new TextureRegion(fightButtonPath).split(70, 71); //Breaks down the texture into tiles

        menuButtons[0] =  new ImageButton(new TextureRegionDrawable(fightButtonSpriteSheet[0][0]), new TextureRegionDrawable(fightButtonSpriteSheet[0][1]));
        menuButtons[0].setPosition(40, 60);
        menuStage.addActor(menuButtons[0]);
        menuButtons[0].setVisible(false);

        menuButtons[0].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);

                if(stage == 3 && stageTwoNewBodyFlag){playLaserSFX();}
                else{ playPunchSFX();}

                if(life > 20){
                    if(menuButtons[0].getX() == 40) {
                        menuButtons[0].setPosition(350, 60);
                        currentFrame = 2;
                    }
                    else{
                        menuButtons[0].setPosition(40, 60);
                        currentFrame = 1;
                    }
                }
                else if(life == 20){
                    currentFrame = 2;
                    menuButtons[0].setPosition(WORLD_WIDTH/2 - menuButtons[0].getWidth()/2f, 20);
                }
                else{
                    if(currentFrame < 2){currentFrame++;}
                    else{currentFrame = 1;}
                }

                if(life > 0){life--;}
                timeCounter = 0;

                if(stage == 1 && life == 5 && !stageTwoNewBodyFlag || stage == 3 && life == 5 && !stageTwoNewBodyFlag){
                    currentFrame = 0;
                    transitionFlag = true;
                    life = 30;
                    menuButtons[0].setVisible(false);
                    Texture fightTexturePath;
                    if(stage == 1){fightTexturePath = new Texture(Gdx.files.internal("Sprites/MasterFigthSpriteSheetTwo.png"));}
                    else{fightTexturePath = new Texture(Gdx.files.internal("Sprites/BullyFightSpriteSheetTwo_Two.png"));}
                    fightPanelTextures = new TextureRegion(fightTexturePath).split(480, 320); //Breaks down the texture into tiles
                    stageTwoNewBodyFlag = true;
                }
                if(life == 0){
                    currentFrame = 0;
                    levelState = 2;
                    menuButtons[0].setVisible(false);
                }
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the music that will play when screen is started
    */
    private void setMusic(String soundName, float volume){
        music = tsr.getAssetManager().get(soundName, Music.class);
        music.setVolume(volume);
        music.setLooping(true);
        music.play();

        punchOneSFX = tsr.getAssetManager().get("SFX/PunchOne.wav", Sound.class);
        punchTwoSFX = tsr.getAssetManager().get("SFX/PunchTwo.wav", Sound.class);
        victorySFX  = tsr.getAssetManager().get("SFX/Victory.wav", Sound.class);
        laserSFX  = tsr.getAssetManager().get("SFX/Laser.wav", Sound.class);
    }

    /*
    Input: Void
    Output: Void
    Purpose: SFX will play when player is punching
    */
    private void playPunchSFX() {
        if(tsr.getSFXVolume()) {
        if (currentFrame == 1) { punchOneSFX.play(); }
        else if (currentFrame == 2 || currentFrame == 0) { punchTwoSFX.play(); }
      }
    }

    /*
    Input: Void
    Output: Void
    Purpose: SFX will play when Buddy transforms
    */
    private void playVictorySFX() {if(tsr.getSFXVolume()){ victorySFX.play(); }}

    /*
    Input: Void
    Output: Void
    Purpose: SFX will play when Buddy transforms
    */
    private void playLaserSFX() {if(tsr.getSFXVolume()){ laserSFX.play(); }}


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
        updateFrameTimer(delta);
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Counts when the abbot SFX can be played again
    */
    private void updateFrameTimer(float delta) {
        frameTimer -= delta;

        if (frameTimer <= 0) {
            frameTimer = FRAME_TIME;

            if(levelState == 0){
                if(timeCounter < 6) {
                    if (currentFrame == 0) { currentFrame++; }
                    else { currentFrame = 0; }
                    timeCounter++;
                }
                else{
                    if(tsr.getSFXVolume()){
                        music.stop();
                        setMusic("Music/FightMusic.wav", 0.3f);
                    }
                    timeCounter = 0;
                    levelState = 1;
                    currentFrame = 0;
                    menuButtons[0].setVisible(true);
                }
            }
            else if(levelState == 1){
                timeCounter++;
                if(timeCounter >= 2){
                    if(transitionFlag && timeCounter > 2){
                        if(currentFrame < 3){currentFrame++;}
                        if(currentFrame == 3){
                            transitionFlag = false;
                            menuButtons[0].setVisible(true);
                            menuButtons[0].setPosition(350, 60);
                            timeCounter = 2;
                            currentFrame = 0;
                        }
                    }
                    else if(!transitionFlag && timeCounter == 2) {
                        timeCounter = 0;
                        currentFrame = 0;
                    }
                }
            }
            else if(levelState == 2){
                timeCounter++;
                if(timeCounter > 2) {
                    currentFrame++;
                    timeCounter = 0;
                    if(currentFrame == 3 && stage <=2 || currentFrame == 5 && stage == 3){levelState = 3;}
                }
            }
            else{
                if(tsr.getSFXVolume() && timeCounter == 0 && thinBuddyAlpha == 0) {
                    music.stop();
                    setMusic("Music/HeartBeatMusic.wav", 0.8f);
                }
                timeCounter++;
                if(thinBuddyAlpha < 1){
                    thinBuddyAlpha += 0.1f;
                    fatBuddyAlpha -= 0.1f;
                    timeCounter = 0;
                }
                else if(timeCounter == 1 && thinBuddyAlpha > 1){
                    music.stop();
                    playVictorySFX();
                }
                else if(timeCounter == 7){
                    dispose();
                    if(stage < 2){tsr.setScreen(new MainScreen(tsr, stage+1));}
                    else{tsr.setScreen(new MenuScreen(tsr));}
                }
            }
        }
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
        if(stage == 1 && life == 31){
            batch.draw(blockTexture, 0 ,0);
        }
        else{
        switch (levelState) {
            case 0: {
                batch.draw(intoPanelTextures[currentFrame][0], 0, 0);
                break;
            }
            case 1: {
                if(transitionFlag && currentFrame < 3) {
                    batch.draw(transitionTextures[currentFrame][0], 0, 0);
                }
                else {
                    if(currentFrame < 3){batch.draw(fightPanelTextures[currentFrame][0], 0, 0);}
                    batch.draw(bloodTexture, WORLD_WIDTH / 2f - bloodTexture.getWidth() / 2f, WORLD_HEIGHT - 43, bloodTexture.getWidth() * life / 30f, bloodTexture.getHeight());
                    batch.draw(lifeBarTexture, WORLD_WIDTH / 2f - bloodTexture.getWidth() / 2f - 10, WORLD_HEIGHT - 52);
                }
                break;
            }
            case 2: {
                if (currentFrame < 3 && stage <= 2 || currentFrame < 5 && stage == 3) {
                    batch.draw(winPanelTextures[currentFrame][0], 0, 0);
                }
                break;
            }
            case 3: {
                batch.draw(frameTexture, 0, 0);
                fatBuddy.draw(batch, fatBuddyAlpha);
                thinBuddy.draw(batch, thinBuddyAlpha);
                drawText();
            }
        }
        }
        batch.end();

        //Draw the buttons over the pop up
        menuStage.draw();

        batch.begin();
        batch.end();
    }

    private void drawText(){
        bitmapFont.getData().setScale(1f);
        String string = "";
        if(fatBuddyAlpha >= 0) {
            string = addNewLine("Something is happening to Buddy?!");
        }
        else{
            switch (stage){
                case 0:{
                    string = addNewLine("Buddy made mad gains. Now he needs to learn how to fight!");
                    break;
                }
                case 1:{
                    string = addNewLine("Buddy has ascended! He feels a strange power inside of him.");
                    break;
                }
                case 2:{
                    string = addNewLine("Buddy has become the Bully! Now he owns this block.");
                    break;
                }
                case 3:{
                    string = addNewLine("Bully has been placed and jail, and Buddy visits him to see how his rehabilitation is going. Buddy has become a hero!");
                    break;
                }
            }
        }
        centerText(bitmapFont, string);

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
    private void centerText(BitmapFont bitmapFont, String string){
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(bitmapFont, string);
        bitmapFont.draw(batch, string,  (float) 240.0 - glyphLayout.width/2, (float) 25 + glyphLayout.height/2);
    }

    /*
    Input: The given string, length - how many chars do we go till we start a new line
    Output: Void
    Purpose: This function take a string and adds a new line whenever it reaches the length between it's starting position andlengtht,
    if start + length happens to occur on a non space char it goes back to the nearest space char
    */
    private String addNewLine(String str){
        int spaceFound;
        for (int j = 0; 60 * (j + 1) + j*3 < str.length(); j++) {
            //Finds the new position of where a " " occurs
            spaceFound = str.lastIndexOf(" ", 60 * (j + 1) + j*3) + 1;
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

        menuStage.dispose();

        if(tsr.getSFXVolume()) { music.stop(); }

    }
}