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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packt.tsr.background.BuddySprite;
import com.packt.tsr.background.TrainingPanel;


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

    private ShapeRenderer shapeRendererEnemy;       //Creates the wire frames for enemies
    private ShapeRenderer shapeRendererUser;        //Creates the wire frame for user
    private ShapeRenderer shapeRendererBackground;  //Creates the wire frame for background objects
    private ShapeRenderer shapeRendererCollectible; //Creates wireframe for collectibles

    //The buttons that will be used in the menu
    private Stage menuStage;
    private ImageButton[] menuButtons;
    private ImageButton[] clickerButtons = new ImageButton[5];

    //Game object that holds the settings
    private TSR tsr;

    //Music that will start
    private Music music;


    //Font used for the user interaction
    private BitmapFont bitmapFont = new BitmapFont();
    //Font for viewing phone stats in developer mode
    private BitmapFont bitmapFontDeveloper = new BitmapFont();


    //Textures
    private Texture popUpTexture;                       //Pop up menu to show menu buttons and Help screen
    private Texture gameScreenTexture;
    private TextureRegion[][] symbolTextures;
    private TextureRegion[][] trainingPanelsTextures;
    private TextureRegion[][] buddyTextures;
    private TextureRegion[][] buttonSpriteSheet;


    private TrainingPanel[] trainingPanels = new TrainingPanel[5];
    private BuddySprite buddySprite;


    //Names of buttons
    private String[] menuButtonText = new String[]{"Main Menu", "Restart", "Help", "Sound Off", "Sound On"};

    //Flags
    private int[] currencies = new int[]{0,0,0,0,0};
    private boolean[] currenciesUnlock = new boolean[]{false, false, false, false};
    private boolean developerMode = false;      //Developer mode shows hit boxes and phone data
    private boolean pausedFlag = false;         //Stops the game from updating
    private boolean endFlag = false;            //Tells us game has been lost
    private float sfxVolume = 1f;               //Current sfx volume
    private boolean helpFlag = false;           //Tells us if help flag is on or off

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
        if(developerMode){showRender();}    //If in developer mode sets up the redners
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

        Texture buttonTexturePath = new Texture(Gdx.files.internal("Sprites/ButtonSpriteSheet.png"));
        buttonSpriteSheet = new TextureRegion(buttonTexturePath).split(86, 46); //Breaks down the texture into tiles


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
        //Sets up the position of the buttons in a square 2x2
        for(int i = 0; i < 5; i ++){
            clickerButtons[i] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[2][0]));
            clickerButtons[i].setPosition(WORLD_WIDTH - buttonSpriteSheet[0][0].getRegionWidth(), 250 - i * (buttonSpriteSheet[0][0].getRegionHeight() + 5));
            menuStage.addActor(clickerButtons[i]);

            //Sets up each buttons function
            final int finalI = i;
            clickerButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    //Returns to the main menu
                    if (finalI == 0) {
                        currencies[finalI]++;
                    }
                    else if (finalI == 1){
                        currenciesUnlock[0] = true;
                        currencies[finalI]++;
                    }
                    else if (finalI == 2){
                        currenciesUnlock[1] = true;
                        currencies[finalI]++;
                    }
                    else if (finalI == 3){
                        currenciesUnlock[2] = true;
                        currencies[finalI]++;
                    }
                    else{
                        currenciesUnlock[3] = true;
                        currencies[finalI]++;
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
        //Set up the texture
        Texture menuButtonTexturePath = new Texture(Gdx.files.internal("UI/MenuButton.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(menuButtonTexturePath).split(45, 44); //Breaks down the texture into tiles

        //Place the button
        menuButtons[0] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[0][1]));
        menuButtons[0].setPosition(430 - buttonSpriteSheet[0][0].getRegionWidth()/2f, WORLD_HEIGHT - 10 - buttonSpriteSheet[0][0].getRegionHeight());
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
        bitmapFont.getData().setScale(0.6f);

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
    Purpose: Sets up the different renders to draw objects in wireframe
    */
    private void showRender(){
        //Enemy
        shapeRendererEnemy = new ShapeRenderer();
        shapeRendererEnemy.setColor(Color.RED);

        //User
        shapeRendererUser = new ShapeRenderer();
        shapeRendererUser.setColor(Color.GREEN);

        //Background
        shapeRendererBackground = new ShapeRenderer();
        shapeRendererBackground.setColor(Color.WHITE);

        //Intractable
        shapeRendererCollectible = new ShapeRenderer();
        shapeRendererCollectible.setColor(Color.BLUE);
    }

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
        if (developerMode) {
            renderEnemy();
            renderUser();
            renderCollectible();
            renderBackground();
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the enemy/obstacle wireframe
    */
    private void renderEnemy(){
        shapeRendererEnemy.setProjectionMatrix(camera.projection);      		                 //Screen set up camera
        shapeRendererEnemy.setTransformMatrix(camera.view);            			                 //Screen set up camera
        shapeRendererEnemy.begin(ShapeRenderer.ShapeType.Line);         		                 //Sets up to draw lines
        shapeRendererEnemy.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws user wireframe
    */
    private void renderUser(){
        shapeRendererUser.setProjectionMatrix(camera.projection);    //Screen set up camera
        shapeRendererUser.setTransformMatrix(camera.view);           //Screen set up camera
        shapeRendererUser.begin(ShapeRenderer.ShapeType.Line);       //Sets up to draw lines
        shapeRendererUser.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the background object and UI wireframes
    */
    private void renderBackground(){
        shapeRendererBackground.setProjectionMatrix(camera.projection);                 //Screen set up camera
        shapeRendererBackground.setTransformMatrix(camera.view);                        //Screen set up camera
        shapeRendererBackground.begin(ShapeRenderer.ShapeType.Line);                    //Starts to draw
        shapeRendererBackground.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws wireframe of the collectibles -- needs to be redone along with collectible objects
    */
    private void renderCollectible(){
        shapeRendererCollectible.setProjectionMatrix(camera.projection);
        shapeRendererCollectible.setTransformMatrix(camera.view);
        shapeRendererCollectible.begin(ShapeRenderer.ShapeType.Line);
        shapeRendererCollectible.end();
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
        //If dev mode is on draw hit boxes and phone stats
        if(developerMode){drawDeveloperInfo();}
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
        batch.end();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the hit boxes and the phone stats
    */
    private void drawDeveloperInfo(){
        //Batch setting up texture
        int x = (int) Gdx.input.getAccelerometerX();
        int y = (int) Gdx.input.getAccelerometerY();
        int z = (int) Gdx.input.getAccelerometerZ();
        centerText(bitmapFontDeveloper, "X: " + x, 40, 300);
        centerText(bitmapFontDeveloper, "Y: " + y, 40, 280);
        centerText(bitmapFontDeveloper, "Z: " + z, 40, 260);
        if(Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)){ centerText(bitmapFontDeveloper, "Surface X", 40, 240); }
        else if(Math.abs(y) > Math.abs(x) && Math.abs(y) > Math.abs(z)){ centerText(bitmapFontDeveloper, "Surface Y", 40, 240); }
        else if(Math.abs(z) > Math.abs(x) && Math.abs(z) > Math.abs(y)){ centerText(bitmapFontDeveloper, "Surface Z", 40, 240); }
    }

    private void drawCurrencies(){
        for(int i = 0; i < 5; i++){
            batch.draw(symbolTextures[0][i], 20 + i * 70, 297);
            centerText(bitmapFont, "" + currencies[i], 35 + i * 70, 301.5f);
        }
    }

    private void drawOffButtons(){
        for(int i = 0; i < 5; i++){
            batch.draw(buttonSpriteSheet[2][0], WORLD_WIDTH - buttonSpriteSheet[0][0].getRegionWidth(), 250 - i * (buttonSpriteSheet[0][0].getRegionHeight() + 5));
        }
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
