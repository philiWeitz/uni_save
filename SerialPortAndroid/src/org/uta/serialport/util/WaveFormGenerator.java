package org.uta.serialport.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class WaveFormGenerator {

	private Thread thread;
	private boolean isRunning;
	private static final int SAMPLE_RATE = 44100;

	
	public void startPlaying(int frequency) {
		stopPlaying();
		
		thread = new Thread(new PlaySound(frequency));
		thread.start();
	}
	
	
	public void stopPlaying() {
		if (thread != null && isRunning) {
			isRunning = false;
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class PlaySound implements Runnable {
		private int frequency;

		
		public PlaySound(int frequency) {
			this.frequency = frequency;
		}

		
		@Override
		public void run() {
			isRunning = true;
			
			int buffsize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT);

			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, buffsize,
					AudioTrack.MODE_STREAM);

			short samples[] = new short[buffsize];
			int amp = 10000;
			double twopi = 8. * Math.atan(1.);
			double ph = 0.0;

			audioTrack.play();

			while (isRunning) {
				for (int i = 0; i < buffsize; i++) {
					samples[i] = (short) (amp * Math.sin(ph));
					ph += twopi * frequency / SAMPLE_RATE;
				}
				audioTrack.write(samples, 0, buffsize);
			}

			audioTrack.stop();
			audioTrack.release();
		}
	}
}
