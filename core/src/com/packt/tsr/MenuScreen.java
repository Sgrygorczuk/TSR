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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen extends ScreenAdapter{
    //Screen Dimensions
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 320;

    //Visual objects
    private SpriteBatch batch = new SpriteBatch();			 //Batch that holds all of the textures
    private Viewport viewport;
    private Camera camera;

    //The buttons used to move around the menus
    private Stage menuStage;
    private ImageButton[] menuButtons = new ImageButton[4];

    //Textures
    private Texture menuScreenTexture;    //This is the background
    private TextureRegion[][] trainingPanelsTextures;   //The small panels in which Buddy trains

    //String used on the buttons
    private String[] buttonText = new String[]{"Continue", "Play", "Credits"};
    //Font used to write in
    private BitmapFont bitmapFont = new BitmapFont();

    //Music player
    private Music music;

    //Game object that keeps track of settings
    private TSR tsr;

    private boolean creditsFlag;   //Tells if credits menu is up or not

    /*
    Input: SpaceHops
    Output: Void
    Purpose: Grabs the info from main screen that holds asset manager
    */
    MenuScreen(TSR tsr) { this.tsr = tsr;}

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
        showTextures();         //Sets up the textures
        showButtons();          //Sets up the buttons
        //showMusic();            //Sets up the music
        showObjects();          //Sets up the font
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
    Purpose: Sets textures that will be drawn
    */
    private void showTextures(){
        menuScreenTexture = new Texture(Gdx.files.internal("Sprites/MenuScreen.png"));
        Texture panelTexturePath = new Texture(Gdx.files.internal("Sprites/BoarderSpriteSheet.png"));
        trainingPanelsTextures = new TextureRegion(panelTexturePath).split(136, 128); //Breaks down the texture into tiles

    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets buttons and their interactions
    */
    private void showButtons(){
        menuStage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(menuStage); //Give power to the menuStage

        setUpMainButtons(); //Places the three main Play|Help|Credits buttons on the screen
        setUpExitButton();  //Palaces the exit button that leaves the Help and Credits menus
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets main three main Play|Help|Credits buttons on the screen
    */
    private void setUpMainButtons(){
        //Get the texures of the buttons
        Texture buttonTexturePath = new Texture(Gdx.files.internal("Sprites/ButtonSpriteSheet.png"));
        TextureRegion[][] buttonSpriteSheet = new TextureRegion(buttonTexturePath).split(86, 46); //Breaks down the texture into tiles

        //Places the three main Play|Help|Credits buttons on the screen
        for(int i = 0; i < 3; i ++){
            menuButtons[i] =  new ImageButton(new TextureRegionDrawable(buttonSpriteSheet[0][0]), new TextureRegionDrawable(buttonSpriteSheet[2][0]));
            menuButtons[i].setPosition( 20 + buttonSpriteSheet[0][0].getRegionWidth()/2f,
                    WORLD_HEIGHT/3 + 20 - 60 * i);
            menuStage.addActor(menuButtons[i]);

            final int finalI = i;
            menuButtons[i].addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    playButtonFX();
                    switch (finalI) {
                        case 0: {
                            //Load Saved Game
                            //music.stop();
                            //dispose();
                            break;
                        }
                        //Launches the game
                        case 1:{
                            //music.stop();
                            dispose();
                            tsr.setScreen(new IntroScreen(tsr, 0));
                            break;
                        }
                        //Turns on the credits menu
                        case 2:{
                            for (ImageButton imageButton : menuButtons) { imageButton.setVisible(false); }
                            creditsFlag = true;
                            menuButtons[3].setVisible(true);
                            break;
                        }
                    }
                }
            });
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets exit button that leaves the Help and Credits menus
    */
    private void setUpExitButton(){
        //Gets the textures
        Texture exitButtonTexturePath = new Texture(Gdx.files.internal("UI/ExitButton.png"));
        TextureRegion[][] exitButtonSpriteSheet = new TextureRegion(exitButtonTexturePath).split(45, 44); //Breaks down the texture into tiles

        //Places the button and adds it to the stage
        menuButtons[3] =  new ImageButton(new TextureRegionDrawable(exitButtonSpriteSheet[0][0]), new TextureRegionDrawable(exitButtonSpriteSheet[0][1]));
        menuButtons[3].setPosition(WORLD_WIDTH - 100, WORLD_HEIGHT - 70);
        menuButtons[3].setWidth(20);
        menuButtons[3].setHeight(20);
        menuStage.addActor(menuButtons[3]);
        menuButtons[3].setVisible(false);
        //If tapped turn of any menu and turn back the main three buttons
        menuButtons[3].addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                //playButtonFX();
                creditsFlag = false;
                for (ImageButton imageButton : menuButtons) { imageButton.setVisible(true); }
                menuButtons[3].setVisible(false);
            }
        });
    }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the music that will play when screen is started
    */
    private void showMusic(){
        music = tsr.getAssetManager().get("Music/GoboMainMenuTheme.wav", Music.class);
        music.setVolume(0.1f);
        music.setLooping(true);
        music.play();
    }

    /*
    Input: Void
    Output: Void
    Purpose: SFX will be played any time a button is clicked
    */
    private void playButtonFX() { tsr.getAssetManager().get("SFX/Button.wav", Sound.class).play(1/2f); }

    /*
    Input: Void
    Output: Void
    Purpose: Sets up the font
    */
    private void showObjects(){ bitmapFont.getData().setScale(0.6f); }


    /*
    Input: Delta, timing
    Output: Void
    Purpose: What gets drawn
    */
    @Override
    public void render(float delta) {
        update();       //Update the variables
        draw();
    }

    /*
    Input: Void
    Output: Void
    Purpose: Updates the title size and position
    */
    private void update() {
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
        batch.draw(menuScreenTexture, 0, 0);
        //Draw the pop up menu
        if(creditsFlag){batch.draw(trainingPanelsTextures[0][0], 10, 10, WORLD_WIDTH-20, WORLD_HEIGHT-20);}
        batch.end();

        menuStage.draw(); // Draws the buttons

        batch.begin();
        //Draws the Play|Credits text on buttons
        if(!creditsFlag){drawButtonText();}
        //Draws the credits text
        else{drawCredits();}
        batch.end();
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
    Input: Void
    Output: Void
    Purpose: Draws the text on the Continue|Play|Credits buttons
    */
    private void drawButtonText(){
        bitmapFont.getData().setScale(1f);
        for(int i = 0; i < 3; i ++) {
            centerText(bitmapFont, buttonText[i], 105, WORLD_HEIGHT / 3 + 43 - 60 * i);
        }
    }

    /*
    Input: Void
    Output: Void
    Purpose: Draws the credits screen
    */
    private void drawCredits(){
        //Title
        bitmapFont.getData().setScale(1f);
        centerText(bitmapFont, "Credits", WORLD_WIDTH/2f, WORLD_HEIGHT-55);
        bitmapFont.getData().setScale(0.8f);

        centerText(bitmapFont, "Programming & Art", WORLD_WIDTH/2f, WORLD_HEIGHT - 75);
        centerText(bitmapFont, "Sebastian Grygorczuk", WORLD_WIDTH/2f, WORLD_HEIGHT - 90);

        centerText(bitmapFont, "Music", WORLD_WIDTH/2f, WORLD_HEIGHT - 125);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f, WORLD_HEIGHT - 140);

        centerText(bitmapFont, "SFX - ########", WORLD_WIDTH/2f, WORLD_HEIGHT - 170);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f - 120, WORLD_HEIGHT - 190);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f, WORLD_HEIGHT - 190);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f + 120, WORLD_HEIGHT - 190);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f - 120, WORLD_HEIGHT - 210);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f, WORLD_HEIGHT - 210);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f + 120, WORLD_HEIGHT - 210);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f - 120, WORLD_HEIGHT - 230);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f, WORLD_HEIGHT - 230);
        centerText(bitmapFont, "########", WORLD_WIDTH/2f + 120, WORLD_HEIGHT - 230);
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
    Purpose: Gets rid of all visuals
    */
    @Override
    public void dispose() {
        menuStage.dispose();

        menuScreenTexture.dispose();
    }


}