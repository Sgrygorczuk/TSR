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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packt.tsr.background.BuddySprite;
import com.packt.tsr.background.TrainingPanel;

import java.util.Arrays;
import java.util.Vector;


class MainScreen extends ScreenAdapter {

    /*
    Dimensions -- Units the screen has
     */
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 320;

    /*
    Image processing -- Objects that modify the view and textures
    */
    private Viewport viewport;			 //The screen where we display things
    private Camera camera;				 //The camera viewing the viewport
    private SpriteBatch batch = new SpriteBatch();			 //Batch that holds all of the textures

    //The buttons that will be used in the menu
    private Stage menuStage;
    private ImageButton[] menuButtons;
    private ImageButton[] clickerButtons = new ImageButton[9];

    //Game object that holds the settings
    private TSR tsr;

    //Music that will start
    private Music music;

    //Font used for the user interaction
    private BitmapFont bitmapFont = new BitmapFont();

    //Textures
    private Texture popUpTexture;                       //Pop up menu to show menu buttons and Help screen
    private Texture gameScreenTexture;
    private TextureRegion[][] symbolTextures;
    private TextureRegion[][] trainingPanelsTextures;
    private TextureRegion[][] buddyTextures;
    private TextureRegion[][] buttonSpriteSheet;


    private TrainingPanel[] trainingPanels = new TrainingPanel[5];
    private BuddySprite buddySprite;

    private Vector<Sprite> willCoins = new Vector<>();
    private Vector<Float> willAlpha = new Vector<>();
    private Vector<Sprite> stamCoins = new Vector<>();
    private Vector<Float> stamAlpha = new Vector<>();
    private Vector<Sprite> strCoins = new Vector<>();
    private Vector<Float> strAlpha = new Vector<>();
    private Vector<Sprite> agilCoins = new Vector<>();
    private Vector<Float> agilAlpha = new Vector<>();
    private Vector<Sprite> techCoins = new Vector<>();
    private Vector<Float> techAlpha = new Vector<>();


    //Names of buttons
    private String[] menuButtonText = new String[]{"Main Menu", "Restart", "Help", "Sound Off", "Sound On"};

    //Flags
    private int[] currencies = new int[]{0,0,0,0,0};
    private int[] autoCoins = new int[]{0,0,0,0};
    private boolean[] currenciesUnlock = new boolean[]{false, false, false, false};
    private boolean[] currenciesTextUnlock = new boolean[]{true, false, false, false};
    private boolean techFlag = false;
    private boolean pausedFlag = false;         //Stops the game from updating
    private boolean endFlag = false;            //Tells us game has been lost
    private float sfxVolume = 1f;               //Current sfx volume
    private boolean helpFlag = false;           //Tells us if help flag is on or off

    //Timing variable used to stop the abbot bounce effect from stacking
    private static final float AUTO_TIME = 1F;
    private float autoTimer = AUTO_TIME;

    /*
    Input: SpaceHops
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    MainScreen(TSR tsr) { this.tsr = tsr;}


    /*
    Input: The width and height of the screen
    Output: Void;
    Purpose: Updates the dimensions of the screen
    */
    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    /*
    Input: Void
    Output: Void
    Purpose: Initializes all the variables that are going to be displayed
    */
    @Override
    public void show() {
        showCamera();       //Set up the camera
        showTextures();     //Sets up textures
        showObjects();      //Sets up player and font
        showButtons();      //Sets up the buttons
        //showMusic();        //Sets up music
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
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);  //Stretches the image to fit the screen
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up all of the textures
    */
    private void showTextures(){
        popUpTexture = new Texture(Gdx.files.internal("UI/PopUpBoarder.png"));
        gameScreenTexture = new Texture(Gdx.files.internal("Sprites/GameScreen.png"));

        Texture buttonTexturePath = new Texture(Gdx.files.internal("Sprites/GameButtonSpriteSheet.png"));
        buttonSpriteSheet = new TextureRegion(buttonTexturePath).split(98, 40); //Breaks down the texture into tiles


        Texture symbolTexturePath = new Texture(Gdx.files.internal("Sprites/SymbolSpriteSheet.png"));
        symbolTextures = new TextureRegion(symbolTexturePath).split(9, 9); //Breaks down the texture into tiles

        Texture panelTexturePath = new Texture(Gdx.files.internal("Sprites/BoarderSpriteSheet.png"));
        trainingPanelsTextures = new TextureRegion(panelTexturePath).split(136, 128); //Breaks down the texture into tiles

        Texture buddyTexturePath = new Texture(Gdx.files.internal("Sprites/StageOneSpriteSheet.png"));
        buddyTextures = new TextureRegion(buddyTexturePath).split(126, 118); //Breaks down the texture into tiles

    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the button
    */
    private void showButtons(){
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuStage); //Gives controll to the stage for clicking on buttons
        //Sets up 6 Buttons
        menuButtons = new ImageButton[6];

        setClickerButtons();
        setUpOpenMenuButton();  //Sets up button used to open the menu
        setUpMenuButtons();     //Sets up the button in the menu
        setUpExitButton();      //Sets up the button used to exit Help
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the button in the menu
    */
    private void setClickerButtons(){
        for(int i = 0; i < 9; i ++){
            if(i % 2 == 0) {
                clickerButtons[i] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
            }
            else{
                clickerButtons[i] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[1][0]), new TextureRegionDrawable(buttonSpriteSheet[1][1]));
            }
            clickerButtons[i].setPosition(WORLD_WIDTH-buttonSpriteSheet[0][0].getRegionWidth(), WORLD_HEIGHT - 35 * (i + 1));
            clickerButtons[i].setWidth(100);
            clickerButtons[i].setHeight(35);
            menuStage.addActor(clickerButtons[i]);
            if(i > 0){clickerButtons[i].setVisible(false);}

            //Sets up each buttons function
            final int finalI = i;
            clickerButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    //Returns to the main menu
                    if (finalI == 0) {
                        currencies[0]++;
                        createWillCoin(0);
                    }
                    else if(finalI == 1){
                        autoCoins[0]++;
                        currencies[4] = currencies[4] - 10;
                    }
                    else if (finalI == 2){
                        currenciesUnlock[0] = true;
                        currenciesTextUnlock[3] = true;
                        techFlag = true;
                        createWillCoin(1);
                        currencies[0] = currencies[0] - 5;
                        currencies[1]++;
                    }
                    else if(finalI == 3){
                        autoCoins[1]++;
                        currencies[4] = currencies[4] - 20;
                    }
                    else if (finalI == 4){
                        createWillCoin(2);
                        currencies[0] = currencies[0] - 10;
                        currencies[1] = currencies[1] - 5;
                        currenciesUnlock[3] = true;
                        currenciesTextUnlock[2] = true;
                        currencies[2]++;
                    }
                    else if(finalI == 5){
                        autoCoins[2]++;
                        currencies[4] = currencies[4] - 30;
                    }
                    else if (finalI == 6){
                        createWillCoin(3);
                        currenciesUnlock[2] = true;
                        currencies[1] = currencies[2] - 5;
                        currencies[2] = currencies[2] - 5;
                        currencies[3]++;
                    }
                    else if(finalI == 7){
                        autoCoins[3]++;
                        currencies[4] = currencies[4] - 40;
                    }
                    else {
                        createWillCoin(4);
                        currenciesUnlock[1] = true;
                        currenciesTextUnlock[2] = true;
                        currencies[0] = currencies[0] - 10;
                        currencies[4]++;
                    }

                }
            });
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up button used to open the menu
    */
    private void setUpOpenMenuButton(){
        //Place the button
        menuButtons[0] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[0].setPosition(0, WORLD_HEIGHT - 36);
        menuButtons[0].setHeight(40);
        menuButtons[0].setWidth(90);
        menuStage.addActor(menuButtons[0]);

        //If button has not been clicked turn on menu and pause game,
        //If the menu is up turn it off and un-pause the game
        menuButtons[0].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if (!endFlag) {
                    playButtonSFX();
                    pausedFlag = !pausedFlag;
                    for (int i = 1; i < 5; i++) {
                        if (pausedFlag) { menuButtons[i].setVisible(true); }  //Turns on 1-5 buttons
                        else{menuButtons[i].setVisible(false);}               //Turns off 1-5 buttons
                    }
                }
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the button in the menu
    */
    private void setUpMenuButtons(){
        //Sets up the texture
        Texture popUpButtonTexturePath = new Texture(Gdx.files.internal("UI/ButtonSpriteSheet.png"));
        final TextureRegion[][] popUpButtonSpriteSheet = new TextureRegion(popUpButtonTexturePath).split(117, 47); //Breaks down the texture into tiles

        //Sets up the position of the buttons in a square 2x2
        float x;
        float y;
        for(int i = 1; i < 5; i ++){
            menuButtons[i] =  new ImageButton(new TextureRegionDrawable(popUpButtonSpriteSheet[i-1][0]), new TextureRegionDrawable(popUpButtonSpriteSheet[i-1][1]));
            if(i == 1 || i == 3){ x = 380/2f - 5 - popUpButtonSpriteSheet[0][0].getRegionWidth();}
            else { x = 380/2f + 5;}
            if(i < 3){ y = WORLD_HEIGHT/2f - 10 + popUpButtonSpriteSheet[0][0].getRegionHeight()/2f;}
            else{y = WORLD_HEIGHT/2f - 10 - popUpButtonSpriteSheet[0][0].getRegionHeight();}
            menuButtons[i].setPosition(x, y);
            menuStage.addActor(menuButtons[i]);
            menuButtons[i].setVisible(false);       //Initially all the buttons are off

            //Sets up each buttons function
            final int finalI = i;
            menuButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    playButtonSFX();
                    //Returns to the main menu
                    if(finalI == 1){
                        //music.stop();
                        tsr.setScreen(new MenuScreen(tsr));
                    }
                    //Restarts the game
                    else if(finalI == 2){ restart(); }
                    //Turns on the help menu
                    else if(finalI == 3){
                        helpFlag = true;
                        //Turns off all the buttons
                        for(ImageButton imageButton : menuButtons){ imageButton.setVisible(false); }
                        //Turns exit button on
                        menuButtons[5].setVisible(true);
                    }
                    //Turns on/off the sound
                    else {soundButtonAction(finalI, popUpButtonSpriteSheet);}
                }
            });
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Changes the button image and turn the sound on and off
    */
    private void soundButtonAction(final int finalI, final TextureRegion[][] popUpButtonSpriteSheet){
        //Gets rid of current button
        menuButtons[finalI].setVisible(false);
        //Turns the volume down
        if(sfxVolume == 1f) {
            //music.stop();
            sfxVolume = 0;
            menuButtons[finalI] =  new ImageButton(new TextureRegionDrawable(popUpButtonSpriteSheet[4][0]), new TextureRegionDrawable(popUpButtonSpriteSheet[4][1]));
        }
        //Turns the sound on
        else{
            //music.play();
            sfxVolume = 1;
            menuButtons[finalI] =  new ImageButton(new TextureRegionDrawable(popUpButtonSpriteSheet[3][0]), new TextureRegionDrawable(popUpButtonSpriteSheet[3][1]));
        }
        //Creates new button in the place of the old one with a different image
        menuButtons[finalI].setPosition(380/2f + 5, WORLD_HEIGHT/2f - 10 - popUpButtonSpriteSheet[0][0].getRegionHeight());
        menuStage.addActor(menuButtons[finalI]);
        //Adds in this function if the button is clicked again
        menuButtons[finalI].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                soundButtonAction(finalI, popUpButtonSpriteSheet);
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the button used to exit the help menu
    */
    private void setUpExitButton(){
        //Sets up the texture
        Texture exitButtonTexturePath = new Texture(Gdx.files.internal("UI/ExitButton.png"));
        TextureRegion[][] exitButtonSpriteSheet = new TextureRegion(exitButtonTexturePath).split(45, 44); //Breaks down the texture into tiles

        //Sets up the position
        menuButtons[5] =  new ImageButton(new TextureRegionDrawable(exitButtonSpriteSheet[0][0]), new TextureRegionDrawable(exitButtonSpriteSheet[0][1]));
        menuButtons[5].setPosition(WORLD_WIDTH - 50, WORLD_HEIGHT - 50);
        menuButtons[5].setWidth(20);
        menuButtons[5].setHeight(20);
        menuStage.addActor(menuButtons[5]);
        menuButtons[5].setVisible(false);
        //Sets up to turn of the help menu if clicked
        menuButtons[5].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                playButtonSFX();
                helpFlag = false;
                //Turn on all buttons but turn off this one
                for (ImageButton imageButton : menuButtons) { imageButton.setVisible(true); }
                menuButtons[5].setVisible(false);
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up player and the font
    */
    private void showObjects(){
        //if(dogFighter.getAssetManager().isLoaded("Fonts/GreedyGobo.fnt")){bitmapFont = dogFighter.getAssetManager().get("Fonts/GreedyGobo.fnt");}
        bitmapFont.getData().setScale(3f);

        trainingPanels[0] = new TrainingPanel(20, 40, trainingPanelsTextures, true);
        trainingPanels[1] = new TrainingPanel(230, 130, trainingPanelsTextures, false);
        trainingPanels[2] = new TrainingPanel(120, 90, trainingPanelsTextures, true);
        trainingPanels[3] = new TrainingPanel(10, 140, trainingPanelsTextures, false);
        trainingPanels[4] = new TrainingPanel(220, 35, trainingPanelsTextures, true);

        float[] x = new float[]{25, 235, 125, 15, 225};
        float[] y = new float[]{45, 135, 95, 146, 40};
        buddySprite = new BuddySprite(x, y, buddyTextures);

    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the music for the level
    */
    private void showMusic(){
        music = tsr.getAssetManager().get("Music/GoboLevelTheme.wav", Music.class);
        music.setVolume(0.1f);
        music.setLooping(true);
        music.play();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Plays the sound effect when called
    */
    private void playButtonSFX() { tsr.getAssetManager().get("SFX/Button.wav", Sound.class).play(1/2f); }

    /*
    Input: Void
    Output: Void
    Purpose: Draws all of the variables on the screen
    */
    @Override
    public void render(float delta) {
        if(!pausedFlag) { update(delta); }
        clearScreen();
        draw();
    }


    /*
    Input: Void
    Output: Void
    Purpose: Updates all the moving components and game variables
    */
    private void update(float delta){
        for(int i = 0; i < 5; i++){
            trainingPanels[i].update(delta);
            buddySprite.update(delta, i);
        }
        updateCoinsMaster();
        updateAutoTimer(delta);
        updateGameButtonVisibility();
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Counts when the abbot SFX can be played again
    */
    private void updateAutoTimer(float delta) {
        autoTimer -= delta;
        if (autoTimer <= 0) {
            autoTimer = AUTO_TIME;
            for (int j = 0; j < autoCoins.length; j++) {
                for (int i = 0; i < autoCoins[j]; i++){
                    createWillCoin(j);
                    currencies[j]++;
                }
            }
        }
    }

    private void updateGameButtonVisibility(){
        if(currencies[0] >= 5){ clickerButtons[2].setVisible(true);}
        else{ clickerButtons[2].setVisible(false);}

        if(currencies[0] >= 10 && currencies[1] >= 5){ clickerButtons[4].setVisible(true);}
        else{ clickerButtons[4].setVisible(false);}

        if(currencies[1] >= 5 && currencies[2] >= 5){ clickerButtons[6].setVisible(true);}
        else{ clickerButtons[6].setVisible(false);}


        if(techFlag && currencies[0] >= 10){ clickerButtons[8].setVisible(true);}
        else{ clickerButtons[8].setVisible(false);}

        if(techFlag &&  currencies[4] >= 10){ clickerButtons[1].setVisible(true);}
        else{ clickerButtons[1].setVisible(false);}

        if(techFlag && currencies[4] >= 20){ clickerButtons[3].setVisible(true);}
        else{ clickerButtons[3].setVisible(false);}

        if(techFlag &&  currencies[4] >= 30){ clickerButtons[5].setVisible(true);}
        else{ clickerButtons[5].setVisible(false);}

        if(techFlag && currencies[4] >= 40){ clickerButtons[7].setVisible(true);}
        else{ clickerButtons[7].setVisible(false);}
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Central function for updating the knights
    */
    private void updateCoinsMaster(){
        updateCoins();
        removeCoins();
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Central function for updating the knights
    */
    private void createWillCoin(int vectorChoice){
        switch (vectorChoice){
            case 0:{
                Sprite coin = new Sprite(symbolTextures[0][0]);
                coin.setAlpha(1);
                float x = MathUtils.random(160, 190);
                float y = MathUtils.random(190, 210);
                coin.setPosition(x, y);
                willCoins.add(coin);
                willAlpha.add(1f);
                break;
            }
            case 1:{
                Sprite coin = new Sprite(symbolTextures[0][1]);
                coin.setAlpha(1);
                float x = MathUtils.random(70, 90);
                float y = MathUtils.random(110, 130);
                coin.setPosition(x, y);
                stamCoins.add(coin);
                stamAlpha.add(1f);
                break;
            }
            case 2:{
                Sprite coin = new Sprite(symbolTextures[0][2]);
                coin.setAlpha(1);
                float x = MathUtils.random(260, 290);
                float y = MathUtils.random(120, 140);
                coin.setPosition(x, y);
                strCoins.add(coin);
                strAlpha.add(1f);
                break;
            }
            case 3:{
                Sprite coin = new Sprite(symbolTextures[0][3]);
                coin.setAlpha(1);
                float x = MathUtils.random(70, 90);
                float y = MathUtils.random(250, 270);
                coin.setPosition(x, y);
                agilCoins.add(coin);
                agilAlpha.add(1f);
                break;
            }
            case 4:{
                Sprite coin = new Sprite(symbolTextures[0][4]);
                coin.setAlpha(1);
                float x = MathUtils.random(260, 290);
                float y = MathUtils.random(230, 250);
                coin.setPosition(x, y);
                techCoins.add(coin);
                techAlpha.add(1f);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + vectorChoice);
        }
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose:
    */
    private void updateCoins(){
        for (int i = 0; i < willCoins.size(); i++){
            willCoins.get(i).setPosition(willCoins.get(i).getX(), willCoins.get(i).getY() + 1);
            willAlpha.set(i, willAlpha.get(i) - 0.01f);
            willCoins.get(i).setAlpha(willAlpha.get(i));
        }

        for (int i = 0; i < stamCoins.size(); i++){
            stamCoins.get(i).setPosition(stamCoins.get(i).getX(), stamCoins.get(i).getY() + 1);
            stamAlpha.set(i, stamAlpha.get(i) - 0.01f);
            stamCoins.get(i).setAlpha(stamAlpha.get(i));
        }

        for (int i = 0; i < strCoins.size(); i++){
            strCoins.get(i).setPosition(strCoins.get(i).getX(), strCoins.get(i).getY() + 1);
            strAlpha.set(i, strAlpha.get(i) - 0.01f);
            strCoins.get(i).setAlpha(strAlpha.get(i));
        }

        for (int i = 0; i < agilCoins.size(); i++){
            agilCoins.get(i).setPosition(agilCoins.get(i).getX(), agilCoins.get(i).getY() + 1);
            agilAlpha.set(i, agilAlpha.get(i) - 0.01f);
            agilCoins.get(i).setAlpha(agilAlpha.get(i));
        }

        for (int i = 0; i < techCoins.size(); i++){
            techCoins.get(i).setPosition(techCoins.get(i).getX(), techCoins.get(i).getY() + 1);
            techAlpha.set(i, techAlpha.get(i) - 0.01f);
            techCoins.get(i).setAlpha(techAlpha.get(i));
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Checks if the knight left screen and removes him or has interacted with player
    */
    private void removeCoins(){
        Vector<Sprite> removedWillCoin = new Vector<>();
        Vector<Float> removedWillAlpha = new Vector<>();
        for (int i = 0; i < willCoins.size(); i++){
            if(willAlpha.get(i) < 0){
                removedWillCoin.add(willCoins.get(i));
                removedWillAlpha.add(willAlpha.get(i));
            }
        }

        for(int i = 0; i < removedWillAlpha.size(); i++){
                willAlpha.remove(removedWillAlpha.get(i));
                willCoins.remove(removedWillCoin.get(i));
        }

        Vector<Sprite> removedStamCoin = new Vector<>();
        Vector<Float> removedStamAlpha = new Vector<>();
        for (int i = 0; i <  stamCoins.size(); i++){
            if( stamAlpha.get(i) < 0){
                removedStamCoin.add(stamCoins.get(i));
                removedStamAlpha.add(stamAlpha.get(i));
            }
        }

        for(int i = 0; i < removedStamAlpha.size(); i++){
            stamAlpha.remove(removedStamAlpha.get(i));
            stamCoins.remove(removedStamCoin.get(i));
        }


        Vector<Sprite> removedStrCoin = new Vector<>();
        Vector<Float> removedStrAlpha = new Vector<>();
        for (int i = 0; i <  strCoins.size(); i++){
            if( strAlpha.get(i) < 0){
                removedStrCoin.add(strCoins.get(i));
                removedStrAlpha.add(strAlpha.get(i));
            }
        }

        for(int i = 0; i < removedStrAlpha.size(); i++){
            strAlpha.remove(removedStrAlpha.get(i));
            strCoins.remove(removedStrCoin.get(i));
        }

        Vector<Sprite> removedAgilCoin = new Vector<>();
        Vector<Float> removedAgilAlpha = new Vector<>();
        for (int i = 0; i <  agilCoins.size(); i++){
            if( agilAlpha.get(i) < 0){
                removedAgilCoin.add(agilCoins.get(i));
                removedAgilAlpha.add(agilAlpha.get(i));
            }
        }

        for(int i = 0; i < removedAgilAlpha.size(); i++){
            agilAlpha.remove(removedAgilAlpha.get(i));
            agilCoins.remove(removedAgilCoin.get(i));
        }


        Vector<Sprite> removedTechCoin = new Vector<>();
        Vector<Float> removedTechAlpha = new Vector<>();
        for (int i = 0; i <  techCoins.size(); i++){
            if( techAlpha.get(i) < 0){
                removedTechCoin.add(techCoins.get(i));
                removedTechAlpha.add(techAlpha.get(i));
            }
        }

        for(int i = 0; i < removedTechAlpha.size(); i++){
            techAlpha.remove(removedTechAlpha.get(i));
            techCoins.remove(removedTechCoin.get(i));
        }
    }


    /*
    Input: Void
    Output: Void
    Purpose: Restarts the game to base state
    */
    private void restart(){
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central drawing function
    */
    private void draw(){
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.draw(gameScreenTexture, 0 ,0);
        drawCurrencies();
        trainingPanels[2].draw(batch);
        drawOffButtons();
        buddySprite.draw(batch, 2);
        for(int i = 0; i < 4;i ++){
            if(currenciesUnlock[i]){
                if(i > 1){
                    trainingPanels[i+1].draw(batch);
                    buddySprite.draw(batch, i+1);
                }
                else{
                    trainingPanels[i].draw(batch);
                    buddySprite.draw(batch, i);
                }
            }
        }
        for(Sprite sprite : willCoins){sprite.draw(batch);}
        for(Sprite sprite : stamCoins){sprite.draw(batch);}
        for(Sprite sprite : strCoins){sprite.draw(batch);}
        for(Sprite sprite : agilCoins){sprite.draw(batch);}
        for(Sprite sprite : techCoins){sprite.draw(batch);}
        //If dev mode is on draw hit boxes and phone stats
        batch.end();

        //Draw open menu button
        if(!pausedFlag){menuStage.draw();}

        batch.begin();
        //Draw the menu pop up
        bitmapFont.getData().setScale(0.3f);
        if(pausedFlag || endFlag){batch.draw(popUpTexture, 380/2f - popUpTexture.getWidth()/2f, WORLD_HEIGHT/2 - popUpTexture.getHeight()/2f);}
        //Draw the help menu
        if(helpFlag){
            batch.draw(popUpTexture, 10, 10, WORLD_WIDTH - 20, WORLD_HEIGHT-20);
            drawHelpScreen();
        }
        batch.end();

        //Draw the buttons over the pop up
        if(pausedFlag || endFlag || helpFlag){menuStage.draw();}

        batch.begin();
        //Draw the menu button text
        if(pausedFlag && !helpFlag){ drawButtonText();}
        drawInGameButtonText();
        batch.end();
    }


    private void drawCurrencies(){
        for(int i = 0; i < 5; i++){
            batch.draw(symbolTextures[0][i], 100 + i * 50, 297);
            bitmapFont.getData().setScale(0.8f);
            centerText(bitmapFont, "" + currencies[i], 125 + i * 50, 301.5f);
        }
    }

    private void drawOffButtons(){
        for(int i = 0; i < 9; i++) {
            if (i % 2 == 0) { batch.draw(buttonSpriteSheet[0][1], WORLD_WIDTH - 92, WORLD_HEIGHT - 35 * (i + 1), 88, 35); }
            else { batch.draw(buttonSpriteSheet[1][1], WORLD_WIDTH - 92, WORLD_HEIGHT - 35 * (i + 1), 88, 35);} }
    }

    private void drawInGameButtonText(){
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(1f);
        centerText(bitmapFont, "Menu", 44, WORLD_HEIGHT - 16);

        bitmapFont.getData().setScale(0.8f);
        centerText(bitmapFont, "Focus", WORLD_WIDTH-48, WORLD_HEIGHT - 12);
        bitmapFont.getData().setScale(0.6f);
        centerText(bitmapFont, "+", WORLD_WIDTH-53, WORLD_HEIGHT - 22);
        batch.draw(symbolTextures[0][0], WORLD_WIDTH-48, WORLD_HEIGHT - 25f, 6, 6);

        if(currenciesTextUnlock[0]) {
            if (currencies[0] >= 5) { bitmapFont.setColor(Color.WHITE); }
            else { bitmapFont.setColor(Color.GRAY); }
            bitmapFont.getData().setScale(0.8f);
            centerText(bitmapFont, "Jump", WORLD_WIDTH - 48, WORLD_HEIGHT - 82);
            bitmapFont.getData().setScale(0.6f);
            centerText(bitmapFont, "-5", WORLD_WIDTH - 66, WORLD_HEIGHT - 92);
            batch.draw(symbolTextures[0][0], WORLD_WIDTH - 61, WORLD_HEIGHT - 95, 6, 6);
            centerText(bitmapFont, "+", WORLD_WIDTH - 43, WORLD_HEIGHT - 92);
            batch.draw(symbolTextures[0][1], WORLD_WIDTH - 38, WORLD_HEIGHT - 95, 6, 6);
        }

        if(currenciesTextUnlock[3]) {
            if (currencies[0] >= 10 && currencies[1] >= 5) { bitmapFont.setColor(Color.WHITE); }
            else { bitmapFont.setColor(Color.GRAY); }
            bitmapFont.getData().setScale(0.8f);
            centerText(bitmapFont, "Punch", WORLD_WIDTH - 48, WORLD_HEIGHT - 152);
            bitmapFont.getData().setScale(0.6f);
            centerText(bitmapFont, "-10", WORLD_WIDTH - 76, WORLD_HEIGHT - 162);
            batch.draw(symbolTextures[0][0], WORLD_WIDTH - 66, WORLD_HEIGHT - 165, 6, 6);
            centerText(bitmapFont, "-5", WORLD_WIDTH - 51, WORLD_HEIGHT - 162);
            batch.draw(symbolTextures[0][1], WORLD_WIDTH - 46, WORLD_HEIGHT - 165, 6, 6);
            centerText(bitmapFont, "+", WORLD_WIDTH - 28, WORLD_HEIGHT - 162);
            batch.draw(symbolTextures[0][2], WORLD_WIDTH - 23, WORLD_HEIGHT - 165, 6, 6);
        }

        if(currenciesTextUnlock[2]) {
            if (currencies[1] >= 5 && currencies[2] >= 5) { bitmapFont.setColor(Color.WHITE); }
            else { bitmapFont.setColor(Color.GRAY); }
            bitmapFont.getData().setScale(0.8f);
            centerText(bitmapFont, "Balance", WORLD_WIDTH - 48, WORLD_HEIGHT - 222);
            bitmapFont.getData().setScale(0.6f);
            centerText(bitmapFont, "-5", WORLD_WIDTH - 76, WORLD_HEIGHT - 232);
            batch.draw(symbolTextures[0][1], WORLD_WIDTH - 66, WORLD_HEIGHT - 235, 6, 6);
            centerText(bitmapFont, "-5", WORLD_WIDTH - 51, WORLD_HEIGHT - 232);
            batch.draw(symbolTextures[0][2], WORLD_WIDTH - 46, WORLD_HEIGHT - 235, 6, 6);
            centerText(bitmapFont, "+", WORLD_WIDTH - 28, WORLD_HEIGHT - 232);
            batch.draw(symbolTextures[0][3], WORLD_WIDTH - 23, WORLD_HEIGHT - 235, 6, 6);
        }

        if(currenciesTextUnlock[1]) {
            if (techFlag && currencies[0] > 10) { bitmapFont.setColor(Color.WHITE); }
            else { bitmapFont.setColor(Color.GRAY); }
            bitmapFont.getData().setScale(0.8f);
            centerText(bitmapFont, "Study", WORLD_WIDTH - 48, WORLD_HEIGHT - 292);
            bitmapFont.getData().setScale(0.6f);
            centerText(bitmapFont, "-10", WORLD_WIDTH - 66, WORLD_HEIGHT - 302);
            batch.draw(symbolTextures[0][0], WORLD_WIDTH - 58, WORLD_HEIGHT - 305, 6, 6);
            centerText(bitmapFont, "+", WORLD_WIDTH - 43, WORLD_HEIGHT - 302);
            batch.draw(symbolTextures[0][4], WORLD_WIDTH - 38, WORLD_HEIGHT - 305, 6, 6);
        }

        if(techFlag){
            if(currenciesUnlock[1]) {
                if (currencies[4] > 10) { bitmapFont.setColor(Color.WHITE); }
                else { bitmapFont.setColor(Color.GRAY); }
                bitmapFont.getData().setScale(0.8f);
                centerText(bitmapFont, "Auto-Focus", WORLD_WIDTH - 38, WORLD_HEIGHT - 48);
                bitmapFont.getData().setScale(0.6f);
                centerText(bitmapFont, "-10", WORLD_WIDTH - 66, WORLD_HEIGHT - 58);
                batch.draw(symbolTextures[0][4], WORLD_WIDTH - 58, WORLD_HEIGHT - 62, 6, 6);
                centerText(bitmapFont, "+" + (autoCoins[0] + 1), WORLD_WIDTH - 45, WORLD_HEIGHT - 58);
                batch.draw(symbolTextures[0][0], WORLD_WIDTH - 35, WORLD_HEIGHT - 62f, 6, 6);
                centerText(bitmapFont, "/sec", WORLD_WIDTH - 20, WORLD_HEIGHT - 58);
            }
            if(currenciesUnlock[3]) {
                if (currencies[4] > 10) { bitmapFont.setColor(Color.WHITE); }
                else { bitmapFont.setColor(Color.GRAY); }
                bitmapFont.getData().setScale(0.8f);
                centerText(bitmapFont, "Auto-Jump", WORLD_WIDTH - 38, WORLD_HEIGHT - 118);
                bitmapFont.getData().setScale(0.6f);
                centerText(bitmapFont, "-20", WORLD_WIDTH - 66, WORLD_HEIGHT - 128);
                batch.draw(symbolTextures[0][4], WORLD_WIDTH - 58, WORLD_HEIGHT - 132, 6, 6);
                centerText(bitmapFont, "+" + (autoCoins[1] + 1), WORLD_WIDTH - 45, WORLD_HEIGHT - 128);
                batch.draw(symbolTextures[0][1], WORLD_WIDTH - 35, WORLD_HEIGHT - 132f, 6, 6);
                centerText(bitmapFont, "/sec", WORLD_WIDTH - 20, WORLD_HEIGHT - 128);
            }
        }

        bitmapFont.setColor(Color.WHITE);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws text over the menu buttons
    */
    private void drawButtonText(){
        float x;
        float y;
        for(int i = 1; i < 5; i ++){
            if(i == 1 || i == 3){ x = 380/2f - 1.4f*menuButtons[0].getWidth();}
            else { x = 380/2f + 1.4f*menuButtons[0].getWidth();}
            if(i < 3){ y = WORLD_HEIGHT/2f - 15 + menuButtons[0].getHeight();}
            else{y = WORLD_HEIGHT/2f - menuButtons[0].getHeight();}
            //If the volume is off draw Sound On else Sound off
            if(i == 4 && sfxVolume == 0){i = 5;}
            centerText(bitmapFont, menuButtonText[i -1], x , y);
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the help screen
    */
    private void drawHelpScreen(){
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
    Input: Void
    Output: Void
    Purpose: Updates all the variables on the screen
    */
    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a); //Sets color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);										 //Sends it to the buffer
    }

    /*
    Input: Void
    Output: Void
    Purpose: Destroys everything once we move onto the new screen
    */
    @Override
    public void dispose() {
        menuStage.dispose();
        music.dispose();
    }
}
