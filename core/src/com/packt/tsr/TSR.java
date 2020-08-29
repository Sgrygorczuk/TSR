package com.packt.tsr;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class TSR extends Game {

	private final AssetManager assetManager = new AssetManager();	//Holds the UI images and sound files


	private int[] currencies = new int[]{0,0,0,0,0}; 			//The currency count that each player has
	private int[] autoCoins = new int[]{0,0,0,0,0};            //The auto coin for each auto generation
	//Tell us which buttons/button text are unlocked
	private boolean[] currenciesUnlock = new boolean[]{false, false, false, false};
	private boolean[] currenciesTextUnlock = new boolean[]{true, false, false, false};
	private int endGameState;               //0 Will - 1 Stamina - 2 Strength - 3 Agility
	private boolean techFlag;
	private int stage;                          //0 Fat, 1 Skinny, 2 Buff
	private boolean gameStarted;
	private boolean onAndroid;
	private boolean sfxVolume;

	/*
	Input: Boolean tells us if the game is started from Android or PC
	Output: Void
	Purpose: Tells the game what controls/information it should provide
	*/
	public TSR(boolean onAndroid){ this.onAndroid = onAndroid; }

	/*
	Input: Void
	Output: Asset Manager
	Purpose: Returns asset manager with all its data
	*/
	AssetManager getAssetManager() { return assetManager; }

	public String getCurrenciesExternal(){ return toString(currencies); }
	public int[] getCurrenciesInternal(){return currencies;}

	public String getAutoCoinExternal(){return toString(autoCoins);}
	public int[] getAutoCoinsInternal(){return autoCoins;}

	public String getCurrenciesUnlockExternal(){return  toString(currenciesUnlock);}
	public boolean[] getCurrenciesUnlockInternal(){return  currenciesUnlock;}

	public String getCurrenciesTextUnlockExternal(){return  toString(currenciesTextUnlock);}
	public boolean[] getCurrenciesTextUnlockInternal(){return  currenciesTextUnlock;}

	public int getEndGameState(){return endGameState;}
	public boolean getTechFlag(){return techFlag;}
	public int getStage(){return stage;}
	public boolean getGameStarted(){return gameStarted;}
	public boolean getOnAndroid(){return onAndroid;}
	public boolean getSFXVolume(){return sfxVolume;}

	public void saveSettingInternal(int[] currencies, int[] autoCoins, boolean[] currenciesUnlock, boolean[] currenciesTextUnlock,
	 int endGameState, boolean techFlag, int stage, boolean gameStarted, boolean sfxVolume){
		this.currencies = currencies;
		this.autoCoins = autoCoins;
		this.currenciesUnlock = currenciesUnlock;
		this.currenciesTextUnlock = currenciesTextUnlock;
		this.endGameState = endGameState;
		this.techFlag = techFlag;
		this.stage = stage;
		this.gameStarted = gameStarted;
		this.sfxVolume = sfxVolume;
	}

	public void loadSetting(String currencies, String autoCoins, String currenciesUnlock, String currenciesTextUnlock,
							int endGameState, boolean techFlag, int stage, boolean gameStarted){
		this.currencies = toIntArray(currencies);
		this.autoCoins = toIntArray(autoCoins);
		this.currenciesUnlock = toBooleanArray(currenciesUnlock);
		this.currenciesTextUnlock = toBooleanArray(currenciesTextUnlock);
		this.endGameState = endGameState;
		this.techFlag = techFlag;
		this.stage = stage;
		this.gameStarted = gameStarted;
	}

	int[] toIntArray(String input){
		int lastIndex = 0;
		int counter = 0;
		int[] output = new int[5];
		for(int i = 0; i < input.length(); i++) {
			if(input.charAt(i) == ',') {
				output[counter] = Integer.parseInt(input.substring(lastIndex, i));
				lastIndex = i+1;
				counter++;
			}
		}
		return output;
	}

	boolean[] toBooleanArray(String input){
		int lastIndex = 0;
		int counter = 0;
		boolean[] output = new boolean[5];
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == ',') {
				output[counter] = Boolean.parseBoolean(input.substring(lastIndex, i));
				lastIndex = i + 1;
				counter++;
			}
		}
		return output;
	}

	String toString(boolean[] array){
		StringBuilder output = new StringBuilder();
		for (boolean value : array) { output.append(value).append(","); }
		return output.toString();
	}

	String toString(int[] array){
		StringBuilder output = new StringBuilder();
		for (int value : array) { output.append(value).append(","); }
		return output.toString();
	}

	/*
	Input: Void
	Output: Void
	Purpose: Starts the app
	*/
	@Override
	public void create () { setScreen(new LoadingScreen(this)); }
}
