package org.uta.serialport.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class WaveFormGenerator {

	private Thread thread;
	private boolean isRunning;
	private static final int SAMPLE_RATE = 44100;

	
	public void startPlayingSineWave(int frequency) {
		stopPlaying();
		
		thread = new Thread(new PlaySineSound(frequency));
		thread.start();
	}
	
	
	public void startPlayingSawtoothWave(int frequency) {
		stopPlaying();
		
		thread = new Thread(new PlaySawToothSound(frequency));
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
	
	
	private abstract class AbstractPlaySound implements Runnable {
		protected static final int AMPLITUDE = 10000;
		
		protected int frequency;
		protected int buffsize;
		protected AudioTrack audioTrack;
		
		public AbstractPlaySound(int frequency) {
			this.frequency = frequency;
			init();
		}
	
		protected void init() {
			buffsize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT);

			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, buffsize,
					AudioTrack.MODE_STREAM);

		}
	}
	
	
	private class PlaySineSound extends AbstractPlaySound {
		
		public PlaySineSound(int frequency) {
			super(frequency);
		}
		
		
		@Override
		public void run() {
			isRunning = true;

			short samples[] = new short[buffsize];
			double twopi = 8. * Math.atan(1.);
			double ph = 0.0;

			audioTrack.play();

			while (isRunning) {
				for (int i = 0; i < buffsize; i++) {
					samples[i] = (short) (AMPLITUDE * Math.sin(ph));
					ph += twopi * frequency / SAMPLE_RATE;
				}
				audioTrack.write(samples, 0, buffsize);
			}

			audioTrack.stop();
			audioTrack.release();
		}
	}
	
	
	private class PlaySawToothSound extends AbstractPlaySound {
		
		public PlaySawToothSound(int frequency) {
			super(frequency);
		}
		
		
		@Override
		public void run() {
			isRunning = true;

			short samples[] = new short[buffsize];
			double period = (SAMPLE_RATE / frequency) / 2;
			short samplePosition = 0;
			
			audioTrack.play();
			
			while (isRunning) {
				
				for (int i = 0; i < buffsize; i++) {
					
					samples[i] = samplePosition;
					samplePosition += AMPLITUDE / period;
					
					if(samplePosition > AMPLITUDE) {
						samplePosition = 0;
					}
				}
				audioTrack.write(samples, 0, buffsize);
			}

			audioTrack.stop();
			audioTrack.release();
		}
	}
}
