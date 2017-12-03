package com.million.sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

public class SoundProducer {

		
	public void play() throws MalformedURLException, InterruptedException	{
	  URL url = new URL("file:sound.wav");
	  AudioClip clip = Applet.newAudioClip(url);
	  clip.play();
	  
	  Thread.sleep(1000 * 5);
	}
	
	public static void main(String[] args) throws MalformedURLException, InterruptedException {
		
		new SoundProducer().play();

	}

}
