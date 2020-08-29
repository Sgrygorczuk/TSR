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
    private ImageButton[] menuButtons = new ImageButton[5];
    private ImageButton[] clickerButtons = new ImageButton[10];

    //Game object that holds the settings
    private TSR tsr;

    //Music that will start
    private Music music;
    private Sound buttonSFX;
    private Sound bellSFX;
    private Sound strikeSFX;

    //Font used for the user interaction
    private BitmapFont bitmapFont = new BitmapFont();

    //Textures
    private Texture gameScreenTexture;                  //The background texture
    private Texture progressTexture;                    //The red bar showing progress of the strikes
    private TextureRegion[][] symbolTextures;           //Symbols that show off currencies
    private TextureRegion[][] trainingPanelsTextures;   //The small panels in which Buddy trains
    private TextureRegion[][] buddyTextures;            //Buddies sprites
    private TextureRegion[][] buttonSpriteSheet;        //The buttons

    //Training object, each needs it's own animation and timing attached
    private TrainingPanel[] trainingPanels = new TrainingPanel[5];
    //Buddy sprites that follow the animations broken down from the sprite sheet
    private BuddySprite buddySprite;

    //Keeps track of all the coins that exits/The alpha of each coin
    private Vector<Sprite> coins = new Vector<>();
    private Vector<Float> alphas = new Vector<>();

    //The clicker buttons
    private String[] currentText = new String[10]; //The actual buttons
    //Names of each button based on the stage of the game
    private String[] stageOneText = new String[]{"Focus", "Jump", "Punch", "Balance", "Study", "Auto-Focus", "Auto-Jump", "Auto-Punch", "Auto-Balance", "Auto-Study"};
    private String[] stageTwoText = new String[]{"Meditate", "Run", "Punch", "Dodge", "Tournament", "Auto-Meditate", "Auto-Run", "Auto-Punch", "Auto-Dodge", "Auto-Tourney"};
    private String[] stageThreeText = new String[]{"Power Up", "Ki Blasts", "Squat", "Transmission", "Kame-Move", "Auto-Power", "Auto-Ki", "Auto-Squat", "Auto-Trans", "Auto-Kame"};

    //Flags
    private int[] currencies = new int[]{1000,1000,1000,1000,1000}; //The currency count that each player has
    private int[] autoCoins = new int[]{0,0,0,0,0};            //The auto coin for each auto generation
    //Tell us which buttons/button text are unlocked
    private boolean[] currenciesUnlock = new boolean[]{false, false, false, false};
    private boolean[] currenciesTextUnlock = new boolean[]{true, false, false, false};
    private int endGameState = 0;               //0 Will - 1 Stamina - 2 Strength - 3 Agility
    private boolean techFlag = false;           //Tells us if the auto is unlocked
    private boolean pausedFlag = false;         //Stops the game from updating
    private boolean sfxVolume = true;
    private int stage;                          //0 Fat, 1 Skinny, 2 Buff

    //Timing variable used to stop the abbot bounce effect from stacking
    private static final float AUTO_TIME = 1F;
    private float autoTimer = AUTO_TIME;

    /*
    Input: TRS, stage tells us which section of game we're in
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    MainScreen(TSR tsr, int stage) {
        this.tsr = tsr;
        this.stage = stage;
    }

    /*
    Input: SpaceHops
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    MainScreen(TSR tsr) {
        this.tsr = tsr;
        this.currencies = tsr.getCurrenciesInternal();
        this.autoCoins = tsr.getAutoCoinsInternal();
        this.currenciesUnlock = tsr.getCurrenciesUnlockInternal();
        this.currenciesTextUnlock = tsr.getCurrenciesTextUnlockInternal();
        this.endGameState = tsr.getEndGameState();
        this.techFlag = tsr.getTechFlag();
        this.stage = tsr.getStage();
    }


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
        showMusic();        //Sets up music
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
        //The single texture
        gameScreenTexture = new Texture(Gdx.files.internal("Sprites/GameScreen.png"));
        progressTexture = new Texture(Gdx.files.internal("Sprites/LifeBarBlood.png"));

        //Sprite sheets
        Texture buttonTexturePath = new Texture(Gdx.files.internal("Sprites/GameButtonSpriteSheet.png"));
        buttonSpriteSheet = new TextureRegion(buttonTexturePath).split(98, 40); //Breaks down the texture into tiles
        Texture symbolTexturePath = new Texture(Gdx.files.internal("Sprites/SymbolSpriteSheet.png"));
        symbolTextures = new TextureRegion(symbolTexturePath).split(9, 9); //Breaks down the texture into tiles
        Texture panelTexturePath = new Texture(Gdx.files.internal("Sprites/BoarderSpriteSheet.png"));
        trainingPanelsTextures = new TextureRegion(panelTexturePath).split(136, 128); //Breaks down the texture into tiles

        //Grab the sprite sheet for Buddy based on the stage
        switch (stage){
            case 0:{
                Texture buddyTexturePath = new Texture(Gdx.files.internal("Sprites/StageOneSpriteSheet.png"));
                buddyTextures = new TextureRegion(buddyTexturePath).split(126, 118); //Breaks down the texture into tiles
                break;
            }
            case 1:{
                Texture buddyTexturePath = new Texture(Gdx.files.internal("Sprites/StageTwoSpriteSheet.png"));
                buddyTextures = new TextureRegion(buddyTexturePath).split(126, 118); //Breaks down the texture into tiles
                break;
            }
            case 2:{
                Texture buddyTexturePath = new Texture(Gdx.files.internal("Sprites/StageThreeSpriteSheet.png"));
                buddyTextures = new TextureRegion(buddyTexturePath).split(126, 118); //Breaks down the texture into tiles
                break;
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the button
    */
    private void showButtons(){
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuStage); //Gives controll to the stage for clicking on buttons

        setClickerButtons();
        setUpOpenMenuButton();  //Sets up button used to open the menu
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the buttons for generating the currencies
    */
    private void setClickerButtons(){
        for(int i = 0; i < 10; i ++){
            //Set up the textures for the buttons
            if(i % 2 == 0) { clickerButtons[i] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1])); }
            else{ clickerButtons[i] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[1][0]), new TextureRegionDrawable(buttonSpriteSheet[1][1])); }

            //Set up position and size of the buttons
            clickerButtons[i].setPosition(WORLD_WIDTH-88, WORLD_HEIGHT - 32 * (i + 1));
            clickerButtons[i].setWidth(78.4f);
            clickerButtons[i].setHeight(32);

            //Add the buttons and make them invisible
            menuStage.addActor(clickerButtons[i]);
            if(i > 0){clickerButtons[i].setVisible(false);}

            //Sets up each buttons function
            final int finalI = i;
            clickerButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    playButtonSFX();
                    //Button to gain Will
                    switch (finalI) {
                        case 0: {
                            currencies[0] = currencies[0] + 1 + 2 * stage;
                            for (int i = 0; i < 1 + 2 * stage; i++) {
                                createCoin(0, 0);
                            }
                            break;
                        }
                        //Button to Auto-Generate Will
                        case 1: {
                            actionButtonAction(0, 5);
                            break;
                        }
                        //Button to gain Stamina
                        case 2: {
                            currenciesUnlock[0] = true;
                            currenciesTextUnlock[3] = true;
                            clickerButtonAction(1, 5);
                            break;
                        }
                        //Button to Auto-Generate Stamina
                        case 3: {
                            actionButtonAction(1, 10);
                            break;
                        }
                        //Button to gain Strength
                        case 4: {
                            currenciesUnlock[3] = true;
                            currenciesTextUnlock[2] = true;
                            clickerButtonAction(0, 1, 2);
                            break;
                        }
                        //Button to Auto-Generate Strength
                        case 5: {
                            actionButtonAction(2, 15);
                            break;
                        }
                        //Button to gain Agility
                        case 6: {
                            currenciesUnlock[2] = true;
                            clickerButtonAction(1, 2, 3);
                            break;
                        }
                        //Button to Auto-Generate Agility
                        case 7: {
                            actionButtonAction(3, 20);
                            break;
                        }
                        //Button to gain Study
                        case 8: {
                            currenciesUnlock[1] = true;
                            currenciesTextUnlock[2] = true;
                            techFlag = true;
                            clickerButtonAction(4, 10);
                            break;
                        }
                        //Button to Auto-Generate Study
                        case 9: {
                            autoCoins[4] += + 1 + 2 * stage;
                            currencies[4] -= 25;
                            currencies[0] -= 250;
                            break;
                        }
                    }
                }
            });
        }
    }

    /*
    Input:
        gainCoin - the coin that we are increasing
        cost - how much we have to pay for it
    Output: Void
    Purpose: Increase the chosen resource by spending one resource
    */
    private void clickerButtonAction(int gainCoin, int cost){
        /* Will spawn 1, 3, or 5 Coin tokens depending on stage */
        for(int i = 0; i < 1 + 2 * stage; i++){createCoin(gainCoin, gainCoin);}
        currencies[0] -= cost;
        currencies[gainCoin] += 1 + 2 * stage;
    }

    /*
    Input:
        coinCostOne and coinCostTwo are the two rescues that get taken away when clicked
        gainCoin - is the coin increase
    Output: Void
    Purpose: Increase the chosen resource by spending two resources
    */
    private void clickerButtonAction(int coinCostOne, int coinCostTwo, int gainCoin){
        /* Will spawn 1, 3, or 5 Coin tokens depending on stage */
        for(int i = 0; i < 1 + 2 * stage; i++){createCoin(gainCoin, gainCoin);}
        currencies[coinCostOne] -= 10;
        currencies[coinCostTwo] -= 5;
        currencies[gainCoin] += 1 + 2 * stage;
    }

    /*
    Input:
        gainCoin - which auto are we increasing
        studyCost - how much study it cost to do this
    Output: Void
    Purpose: Increase the chosen resource by spending two resources
    */
    private void actionButtonAction(int gainCoin, int studyCost){
        autoCoins[gainCoin] += + 1 + 2 * stage;
        currencies[4] -= studyCost;
        currencies[gainCoin] -= 5;
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up button used to open the menu
    */
    private void setUpOpenMenuButton(){
        //Menu Button
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
                playButtonSFX();
                pausedFlag = !pausedFlag;
                if (pausedFlag) { menuButtons[1].setVisible(true); }
                else { menuButtons[1].setVisible(false); }
            }
        });

        //Sound Button
        menuButtons[1] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[1].setPosition(380/2f - buttonSpriteSheet[0][0].getRegionWidth()/2f, 180);
        menuStage.addActor(menuButtons[1]);
        menuButtons[1].setVisible(false);       //Initially all the buttons are off

        menuButtons[1].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) { super.tap(event, x, y, count, button);
                playButtonSFX();
                soundButtonAction();
            }
        });

        //Strike Button
        menuButtons[2] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[2].setPosition(0, 0);
        menuStage.addActor(menuButtons[2]);
        menuButtons[2].setVisible(false);

        menuButtons[2].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if(endGameState < 4) {
                    playStrikeSFX();
                    currencies[endGameState] -= 1000;
                    for(int i = 0; i < 250; i++){ createCoin(endGameState, 5); }
                    endGameState++;
                }
            }
        });

        //Fight Button
        Texture fightButtonPath = new Texture(Gdx.files.internal("Sprites/FightButton.png"));
        final TextureRegion[][] fightButtonSpriteSheet = new TextureRegion(fightButtonPath).split(258, 138); //Breaks down the texture into tiles

        menuButtons[3] =  new ImageButton(new TextureRegionDrawable(fightButtonSpriteSheet[0][0]), new TextureRegionDrawable(fightButtonSpriteSheet[1][0]));
        menuButtons[3].setPosition(200 - fightButtonSpriteSheet[0][0].getRegionWidth()/2f, WORLD_HEIGHT/2f - fightButtonSpriteSheet[0][0].getRegionHeight()/2f);
        menuStage.addActor(menuButtons[3]);
        menuButtons[3].setVisible(false);

        menuButtons[3].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                playBellSFX();
                dispose();
                //Takes us to Fight Scene if we go from 0->1 and 1->2
                if(stage < 2){tsr.setScreen(new FightScreen(tsr, stage));}
                //Takes us to Cut Scene 2->3
                else{tsr.setScreen(new IntroScreen(tsr, 1));}
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Changes the button image and turn the sound on and off
    */
    private void soundButtonAction(){
        //Gets rid of current button
        menuButtons[1].setVisible(false);
        //Turns the volume down
        if(sfxVolume) {
            music.stop();
            sfxVolume = false;
            menuButtons[1] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][1]), new TextureRegionDrawable(buttonSpriteSheet[0][0]));
        }
        //Turns the sound on
        else{
            music.play();
            sfxVolume = true;
            menuButtons[1] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        }
        //Creates new button in the place of the old one with a different image
        menuButtons[1].setPosition(380/2f - buttonSpriteSheet[0][0].getRegionWidth()/2f, 180);
        menuStage.addActor(menuButtons[1]);
        //Adds in this function if the button is clicked again
        menuButtons[1].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                soundButtonAction();
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up player and the font
    */
    private void showObjects(){
        bitmapFont.getData().setScale(3f);

        //Setting up the panels, their position and the animation frame it starts on
        trainingPanels[0] = new TrainingPanel(20, 40, trainingPanelsTextures, true);
        trainingPanels[1] = new TrainingPanel(230, 130, trainingPanelsTextures, false);
        trainingPanels[2] = new TrainingPanel(120, 90, trainingPanelsTextures, true);
        trainingPanels[3] = new TrainingPanel(10, 140, trainingPanelsTextures, false);
        trainingPanels[4] = new TrainingPanel(220, 35, trainingPanelsTextures, true);

        //Set up Buddy's position
        float[] x = new float[]{25, 235, 125, 15, 225};
        float[] y = new float[]{45, 135, 95, 146, 40};
        //Creates Buddy's sprites
        buddySprite = new BuddySprite(x, y, buddyTextures);

        //Tells us which text set we should used on top of the buttons
        switch (stage){
            case 0:{
                currentText = stageOneText;
                break;
            }
            case 1:{
                currentText = stageTwoText;
                break;
            }
            case 2:{
                currentText = stageThreeText;
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the music for the level
    */
    private void showMusic(){
        music = tsr.getAssetManager().get("Music/CutSceneMusic.wav", Music.class);
        music.setVolume(0.5f);
        music.setLooping(true);
        music.play();

        buttonSFX = tsr.getAssetManager().get("SFX/Button.wav", Sound.class);
        bellSFX = tsr.getAssetManager().get("SFX/Bell.wav", Sound.class);
        strikeSFX  = tsr.getAssetManager().get("SFX/Strike.wav", Sound.class);
    }

    /*
    Input: Void
    Output: Void
    Purpose: SFX will play when player is punching
    */
    private void playButtonSFX() {  if(sfxVolume){buttonSFX.play(); }}

    /*
    Input: Void
    Output: Void
    Purpose: SFX will play when Buddy transforms
    */
    private void playStrikeSFX() { if(sfxVolume){strikeSFX.play(); }}

    /*
    Input: Void
    Output: Void
    Purpose: SFX will play when Buddy transforms
    */
    private void playBellSFX() { if(sfxVolume){bellSFX.play(); }}

    /*
    Input: Void
    Output: Void
    Purpose: Draws all of the variables on the screen
    */
    @Override
    public void render(float delta) {
        update(delta);
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
            trainingPanels[i].update(delta);        //Update the Training Panel Animation
            buddySprite.update(delta, i);           //Update the Buddy's Animation
        }
        updateCoinsMaster();                        //Updates all the coin position and transparency
        updateAutoTimer(delta);                     //Updates when the auto generate coins are made
        updateGameButtonVisibility();               //Updates the visibility of buttons based on resources available
        //Saves all the info to the main game which will later save that data locally
        tsr.saveSettingInternal(currencies, autoCoins, currenciesUnlock,
                currenciesTextUnlock, endGameState, techFlag, stage, true, sfxVolume);
    }

    /*
    Input: Delta, timing
    Output: Void
    Purpose: Used to count down the auto generated coins
    */
    private void updateAutoTimer(float delta) {
        autoTimer -= delta;
        //Checks if time has passed to generate coins
        if (autoTimer <= 0) {
            autoTimer = AUTO_TIME;
            //Goes through the each amount of coins {2,0,0,0,0}
            for (int j = 0; j < autoCoins.length; j++) {
                //If autoCoin[0] = 2, will create two coin tokens and increase the value but 1 each time
                for (int i = 0; i < autoCoins[j]; i++){
                    createCoin(j,j);
                    currencies[j]++;
                }
            }
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the visibility of the clicker and menu buttons based on resources available
    */
    private void updateGameButtonVisibility(){
        //Updates the Strike Button and the Fight Button visibility
        if(endGameState < 5){updateMenuButton(currencies[endGameState] >= 1000, 2);}
        updateMenuButton(endGameState == 4, 3);

        //Updates the clicker button visibility
        updateButton(currencies[0] >= 5, 2);
        updateButton(currencies[0] >= 10 && currencies[1] >= 5, 4);
        updateButton(currencies[1] >= 5 && currencies[2] >= 5, 6);
        updateButton(currenciesTextUnlock[3] && currencies[0] >= 10, 8);

        //Updates the auto generated button visibility
        updateButton(techFlag &&  currenciesUnlock[1] && currencies[4] >= 5 && currencies[0] >= 5, 1);
        updateButton(techFlag && currenciesUnlock[1] &&  currencies[4] >= 10 && currencies[1] >= 5, 3);
        updateButton(techFlag && currenciesUnlock[3] &&  currencies[4] >= 15 && currencies[2] >= 5, 5);
        updateButton(techFlag && currenciesUnlock[2] && currencies[4] >= 20 && currencies[3] >= 5, 7);
        updateButton(techFlag && currencies[4] >= 25 && currencies[0] >= 250, 9);
    }

    /*
    Input: Flag - a boolean expression that tells us if there is enough resource
           Position - the button it the Image Button Array
    Output: Void
    Purpose: Turns the menu button on and off based on flag
    */
    private void updateMenuButton(boolean flag, int position){
        if(flag){ menuButtons[position].setVisible(true);}
        else{ menuButtons[position].setVisible(false);}
    }

    /*
    Input: Flag - a boolean expression that tells us if there is enough resource
       Position - the button it the Image Button Array
    Output: Void
    Purpose: Turns the clicker button on and off based on flag
    */
    private void updateButton(boolean flag, int position){
        if(flag){ clickerButtons[position].setVisible(true);}
        else{ clickerButtons[position].setVisible(false);}
    }

    /*
    Input: Void
    Output: Void
    Purpose: Central function for updating the coins
    */
    private void updateCoinsMaster(){
        updateCoins();          //Updates the position/transparency of coins
        removeCoins();          //Gets rid of coins that have alpha < 0
    }

    /*
    Input: coinChoice - tells us which coin we're creating
    Output: Void
    Purpose: Creates a coin given the choice
    */
    private void createCoin(int coinChoice, int placement){
        Sprite coin = new Sprite(symbolTextures[0][coinChoice]);
        switch (placement){
            //Will
            case 0:{
                //Give it a random position in chosen ranges
                coin.setPosition(MathUtils.random(160, 190), MathUtils.random(190, 210));
                break;
            }
            //Stamina
            case 1:{
                coin.setPosition(MathUtils.random(70, 90), MathUtils.random(110, 130));
                break;
            }
            //Strength
            case 2:{
                coin.setPosition(MathUtils.random(260, 290), MathUtils.random(120, 140));
                break;
            }
            //Agility
            case 3:{
                coin.setPosition(MathUtils.random(70, 90), MathUtils.random(250, 270));
                break;
            }
            //Study
            case 4:{
                coin.setPosition(MathUtils.random(260, 290), MathUtils.random(230, 250));
                break;
            }
            //Strike
            case 5:{
                coin.setPosition(MathUtils.random(0, 90), MathUtils.random(-30, 50));
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + placement);
        }
        //Add coin sprite
        coins.add(coin);
        //Add the alpha for this coin
        alphas.add(1f);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the coins position and transparency
    */
    private void updateCoins(){
        for (int i = 0; i < coins.size(); i++){
            //Move the coin up
            coins.get(i).setPosition(coins.get(i).getX(), coins.get(i).getY() + 1);
            //Lower transparency and connect it to the sprite
            alphas.set(i, alphas.get(i) - 0.01f);
            coins.get(i).setAlpha(alphas.get(i));
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Removes all coins that have transparency < 0
    */
    private void removeCoins(){
        //Get vectors that will hold items/can't remove them instantly because it'll crash for function
        Vector<Sprite> removedCoin = new Vector<>();
        Vector<Float> removedAlpha = new Vector<>();
        //Goes through all coins
        for (int i = 0; i < coins.size(); i++){
            //If alpha < 0 add it to the remove list
            if(alphas.get(i) < 0){
                removedCoin.add(coins.get(i));
                removedAlpha.add(alphas.get(i));
            }
        }

        //Remove those coins from the original list
        for(int i = 0; i < removedCoin.size(); i++){
            alphas.remove(removedAlpha.get(i));
            coins.remove(removedCoin.get(i));
        }
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
        //Draw the progress bar
        batch.draw(progressTexture, 30, 0, progressTexture.getWidth()*endGameState/4f, WORLD_HEIGHT);
        //Draws the background
        batch.draw(gameScreenTexture, 0 ,0);
        //Draws the coins on top of the screen
        drawCurrencies();
        //Draws the turns off buttons
        drawOffButtons();
        //Draws the frames and buddy sprites
        drawBuddy();
        //Draws the coins as they float up
        for(Sprite sprite : coins){sprite.draw(batch);}
        batch.end();

        //Draw open menu button
        if(!pausedFlag){menuStage.draw();}

        batch.begin();
        //Draw the menu pop up
        bitmapFont.getData().setScale(0.3f);
        if(pausedFlag){batch.draw(trainingPanelsTextures[0][0], 380/2f - 283/2f, WORLD_HEIGHT/2 - 191/2f, 283, 191);}
        //Draw the help menu
        batch.end();

        //Draw the buttons over the pop up
        if(pausedFlag){menuStage.draw();}

        batch.begin();
        //Draw the menu button text
        if(pausedFlag){ drawButtonText();}
        //
        drawClickerAutoButtonText();        //Draws the button text
        drawMenuButtonText();               //Draws menu button text
        batch.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws Buddy sprites and the training panels he's in
    */
    private void drawBuddy(){
        //Draws the central panel/sprite
        trainingPanels[2].draw(batch);
        buddySprite.draw(batch, 2);

        //Draws the other four panels based on if they're unlocked
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
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the currencies on top of the screen
    */
    private void drawCurrencies(){
        for(int i = 0; i < 5; i++){
            batch.draw(symbolTextures[0][i], 100 + i * 50, 297);
            bitmapFont.getData().setScale(0.8f);
            centerText(bitmapFont, "" + currencies[i], 125 + i * 50, 301.5f);
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws off buttons when they can't be clicked
    */
    private void drawOffButtons(){
        batch.draw(buttonSpriteSheet[0][1], 0, 0);
        for(int i = 0; i < 10; i++) {
            if (i % 2 == 0) { batch.draw(buttonSpriteSheet[0][1], WORLD_WIDTH - 88, WORLD_HEIGHT - 32 * (i + 1), 78.4f, 32); }
            else if(techFlag){ batch.draw(buttonSpriteSheet[1][1], WORLD_WIDTH - 88, WORLD_HEIGHT - 32 * (i + 1), 78.4f, 32);} }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the Strike|Menu|Fight
    */
    private void drawMenuButtonText(){
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(1f);
        centerText(bitmapFont, "Menu", 44, WORLD_HEIGHT - 16);

        //Draws the fight text to Strike and how much it cost to click it
        bitmapFont.getData().setScale(0.9f);
        centerText(bitmapFont, "Strike", 46,  28);
        bitmapFont.getData().setScale(0.8f);
        if(endGameState < 4) {
            centerText(bitmapFont, "-1000", 38, 14);
            batch.draw(symbolTextures[0][endGameState], 58, 8, 11, 11);
        }

        //Draws the fight text to Fight button
        if(endGameState == 4){
            bitmapFont.setColor(Color.RED);
            bitmapFont.getData().setScale(1.5f);
            centerText(bitmapFont, "FIGHT", 200, WORLD_HEIGHT/2f);
        }
        bitmapFont.setColor(Color.WHITE);
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws text on clicker and auto buttons
    */
    private void drawClickerAutoButtonText(){
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(0.8f);
        //Always draw the first clicker button text
        centerText(bitmapFont, currentText[0], WORLD_WIDTH-48, WORLD_HEIGHT - 11);
        bitmapFont.getData().setScale(0.6f);
        centerText(bitmapFont, "+" + (1 + 2 * stage), WORLD_WIDTH-53, WORLD_HEIGHT - 21);
        batch.draw(symbolTextures[0][0], WORLD_WIDTH-45, WORLD_HEIGHT - 24.5f, 6, 6);

        //Draws the rest of the clicker button text based on their unlocked and resources
        if(currenciesTextUnlock[0]) { drawClickerTextTwo(currencies[0] >= 5, 1, "-5", 1, 75.5f); }
        if(currenciesTextUnlock[3]) { drawClickerTextThree(currencies[0] >= 10 && currencies[1] >= 5, 2, 0 , 1, 2, 139f); }
        if(currenciesTextUnlock[2]) { drawClickerTextThree(currencies[1] >= 5 && currencies[2] >= 5, 3, 1 , 2, 3, 203f); }
        if(currenciesTextUnlock[3]) { drawClickerTextTwo(currencies[0] >= 10, 4, "-10",  4, 267); }

        //Draws the auto button texts
        if(techFlag){
            if(currenciesUnlock[1]) {
                drawAutoText(currencies[4] >= 5 && currencies[0] >= 5, 5, "-5", 0, 43);
                drawAutoText(currencies[4] >= 10  && currencies[1] >= 5, 6, "-10", 1, 105);
            }
            if(currenciesUnlock[3]) { drawAutoText(currencies[4] >= 15 && currencies[2] >= 5, 7, "-15", 2, 170); }
            if(currenciesUnlock[2]) { drawAutoText(currencies[4] >= 20 && currencies[3] >= 5, 8, "-20", 3, 235); }
            drawAutoText(currencies[4] >= 25 && currencies[0] >= 250);
        }

        bitmapFont.setColor(Color.WHITE);
    }

    /*
    Input: Flag - tells us if we have enough resource to paint the text white
           Text - which word should be used
           Cost - string cost of the initial resource
           Symbol - the resource we are gaining
           Height - where the text is drawn
    Output: Void
    Purpose: Draws the text where button only cost one resource
    */
    private void drawClickerTextTwo(boolean flag, int text, String cost, int symbol, float height){
            bitmapColor(flag);
            bitmapFont.getData().setScale(0.8f);
            centerText(bitmapFont, currentText[text], WORLD_WIDTH - 48, WORLD_HEIGHT - height);
            bitmapFont.getData().setScale(0.6f);
            centerText(bitmapFont, cost, WORLD_WIDTH - 68, WORLD_HEIGHT - (height + 10));
            batch.draw(symbolTextures[0][0], WORLD_WIDTH - 61, WORLD_HEIGHT - (height + 13.5f), 6, 6);
            centerText(bitmapFont, "+" + (1 + 2 * stage), WORLD_WIDTH - 43, WORLD_HEIGHT - (height + 10));
            batch.draw(symbolTextures[0][symbol], WORLD_WIDTH - 35, WORLD_HEIGHT - (height + 13.5f), 6, 6);
    }

    /*
    Input: Flag - tells us if we have enough resource to paint the text white
       Text - which word should be used
       SymbolOne - the resource we are paying
       SymbolTwo - the resource we are paying
       SymbolThree - the resource we are gaining
       Height - where the text is drawn
    Output: Void
    Purpose: Draws the text where button cost two different resources
    */
    private void drawClickerTextThree(boolean flag, int text, int symbolOne, int symbolTwo, int symbolThree, float height){
        bitmapColor(flag);
        bitmapFont.getData().setScale(0.8f);
        centerText(bitmapFont, currentText[text], WORLD_WIDTH - 48, WORLD_HEIGHT - height);
        bitmapFont.getData().setScale(0.6f);
        centerText(bitmapFont, "-10", WORLD_WIDTH - 76, WORLD_HEIGHT - (height + 10));
        batch.draw(symbolTextures[0][symbolOne], WORLD_WIDTH - 66, WORLD_HEIGHT - (height + 13.5f), 6, 6);
        centerText(bitmapFont, "-5", WORLD_WIDTH - 51, WORLD_HEIGHT - (height + 10));
        batch.draw(symbolTextures[0][symbolTwo], WORLD_WIDTH - 46, WORLD_HEIGHT - (height + 13.5f), 6, 6);
        centerText(bitmapFont, "+" + (1 + 2 * stage), WORLD_WIDTH - 31, WORLD_HEIGHT - (height + 10));
        batch.draw(symbolTextures[0][symbolThree], WORLD_WIDTH - 23, WORLD_HEIGHT - (height + 13.5f), 6, 6);

    }

    /*
    Input: Flag - tells us if we have enough resource to paint the text white
        Text - which word should be used
        Cost - how much study it costs
        autoCoin - which coin we're showing
        Height - where the text is drawn
    Output: Void
    Purpose: Draws the text where button cost two different resources
    */
    private void drawAutoText(boolean flag, int text, String cost, int autoCoin, float height){
        bitmapColor(flag);
        bitmapFont.getData().setScale(0.8f);
        centerText(bitmapFont, currentText[text], WORLD_WIDTH - 38, WORLD_HEIGHT - height);
        bitmapFont.getData().setScale(0.6f);
        centerText(bitmapFont, cost, WORLD_WIDTH - 86, WORLD_HEIGHT - (height + 14));
        batch.draw(symbolTextures[0][4], WORLD_WIDTH - 78, WORLD_HEIGHT - (height + 17.5f), 6, 6);
        centerText(bitmapFont, "-5", WORLD_WIDTH - 60, WORLD_HEIGHT - (height + 14));
        batch.draw(symbolTextures[0][autoCoin], WORLD_WIDTH - 54, WORLD_HEIGHT - (height + 17.5f), 6, 6);
        centerText(bitmapFont, "+" + (autoCoins[autoCoin] + 1 + 2 * stage), WORLD_WIDTH - 40, WORLD_HEIGHT - (height + 14));
        batch.draw(symbolTextures[0][autoCoin], WORLD_WIDTH - 33, WORLD_HEIGHT - (height + 17.5f), 6, 6);
        centerText(bitmapFont, "/sec", WORLD_WIDTH - 18, WORLD_HEIGHT - (height + 14));
    }

    /*
    Input: Flag - tells us if we have enough resource to paint the text white
    Output: Void
    Purpose: Draws the text where button cost two different resources
    */
    private void drawAutoText(boolean flag){
        bitmapColor(flag);
        bitmapFont.getData().setScale(0.8f);
        centerText(bitmapFont, currentText[9], WORLD_WIDTH - 38, WORLD_HEIGHT - 300);
        bitmapFont.getData().setScale(0.6f);
        centerText(bitmapFont, "-25", WORLD_WIDTH - 96, WORLD_HEIGHT - ( 300 + 14));
        batch.draw(symbolTextures[0][4], WORLD_WIDTH - 88, WORLD_HEIGHT - ( 300 + 17.5f), 6, 6);
        centerText(bitmapFont, "-250", WORLD_WIDTH - 68, WORLD_HEIGHT - ( 300 + 14));
        batch.draw(symbolTextures[0][0], WORLD_WIDTH - 54, WORLD_HEIGHT - ( 300 + 17.5f), 6, 6);
        centerText(bitmapFont, "+" + (autoCoins[4] + 1 + 2 * stage), WORLD_WIDTH - 40, WORLD_HEIGHT - ( 300 + 14));
        batch.draw(symbolTextures[0][4], WORLD_WIDTH - 33, WORLD_HEIGHT - (300 + 17.5f), 6, 6);
        centerText(bitmapFont, "/sec", WORLD_WIDTH - 18, WORLD_HEIGHT - ( 300 + 14));
    }

    /*
    Input: Boolean flag - tells us if
    Output: Void
    Purpose: Makes the text white or gray based on the flag
    */
    private void bitmapColor(boolean flag){
        if (flag) { bitmapFont.setColor(Color.WHITE); }
        else { bitmapFont.setColor(Color.GRAY); }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws text pause menu
    */
    private void drawButtonText(){
        bitmapFont.getData().setScale(1);
        String string;
        if(!sfxVolume){ string = "Sound On";}
        else{string = "Sound Off";}
        centerText(bitmapFont, string, 380/2f, 200);

        bitmapFont.getData().setScale(0.9f);
        centerText(bitmapFont, "Goals:", 380/2f, 160);
        bitmapFont.getData().setScale(0.82f);
        switch (stage){
            case 0:{
                centerText(bitmapFont, "Lose Weight", 380/2f, 145);
                break;
            }
            case 1:{
                centerText(bitmapFont, "Lose Weight", 380/2f, 145);
                batch.draw(symbolTextures[0][0], 380/2f - 50, 144, 100, 1);
                centerText(bitmapFont, "Beat Master", 380/2f, 130);
                break;
            }
            case 2:{
                centerText(bitmapFont, "Lose Weight", 380/2f, 145);
                batch.draw(symbolTextures[0][0], 380/2f - 50, 144, 100, 1);
                centerText(bitmapFont, "Beat Master", 380/2f, 130);
                batch.draw(symbolTextures[0][0], 380/2f - 50, 129, 100, 1);
                centerText(bitmapFont, "Confront Bully", 380/2f, 115);
                break;
            }
        }
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
        music.stop();

        gameScreenTexture.dispose();
        progressTexture.dispose();
    }
}
