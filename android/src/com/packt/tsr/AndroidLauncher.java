package com.packt.tsr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.packt.tsr.TSR;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class AndroidLauncher extends AndroidApplication {



	//Timer and task used to save data
	Timer timer = new Timer();

	/*
	Input: Void
	Output: Void
	Purpose: Saves the data to the local storage
	*/
	TimerTask saveTask = new TimerTask() {@Override public void run() { saveSettings(); }};

	/*
	Input: Void
	Output: Void
	Purpose: When app turns on it loads saved data, if there is no saved data makes saved data then loads it
	*/
	TimerTask loadTask = new TimerTask() {@Override public void run() { loadSettings(); }};

	/*
	Input: Void
	Output: Void
	Purpose: Takes all the values from the running game at 30sec interval and saves all the new values
	*/
	private void saveSettings(){
		SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
		SharedPreferences.Editor settingEditor = settings.edit();

		System.out.println("Saving Settings");
		settingEditor.putString("Currencies", tsr.getCurrenciesExternal());
		settingEditor.putString("AutoCoins", tsr.getAutoCoinExternal());
		settingEditor.putString("CurrenciesUnlock", tsr.getCurrenciesUnlockExternal());
		settingEditor.putString("CurrenciesTextUnlock", tsr.getCurrenciesTextUnlockExternal());
		settingEditor.putInt("EndGameState", tsr.getEndGameState());
		settingEditor.putBoolean("TechFlag", tsr.getTechFlag());
		settingEditor.putInt("Stage", tsr.getStage());
		settingEditor.putBoolean("GameStarted", tsr.getGameStarted());
		settingEditor.apply();
	}

	/*
	Input: Void
	Output: Void
	Purpose: When app first loads it puts all the stored data from local drive and puts it in game
	*/
	private void loadSettings(){
		SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);
		System.out.println("Loading Settings");
		tsr.loadSetting(
				settings.getString("Currencies", "0,0,0,0,0"),
				settings.getString("AutoCoins", "0,0,0,0,0"),
				settings.getString("CurrenciesUnlock", "false,false,false,false"),
				settings.getString("CurrenciesTextUnlock", "true,false,false,false"),
				settings.getInt("EndGameState", 0),
				settings.getBoolean("TechFlag", false),
				settings.getInt("Stage", 0),
				settings.getBoolean("GameStarted", false));
	}

	TSR tsr = new TSR(true);

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//Loads the data into the game (Needs to be on a separate thread so is put in a timer)
		timer.schedule(loadTask, 0);
		//Start the app
		initialize(tsr, config);
		//Every 30 sec save the data to data base
		timer.scheduleAtFixedRate(saveTask, 3000, 3000);
	}
}
