package org.uta.wavegenerator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Main {

	private static final float AMPLITUDE = 127f;	
	protected static final int SAMPLE_RATE = 16 * 1024;

	
	private static byte[] createSinWaveBuffer(double freq, int ms, int pulseGap) {
		int samples = (int) ((ms * SAMPLE_RATE) / 1000);
		byte[] output = new byte[samples];
		
		double period = (double) SAMPLE_RATE / freq;
		double gap = 0;
		
		for (int i = 0; i < output.length; i++) {
			
			if(gap <= 0) {
				double angle = 2.0 * Math.PI * i / period;
				output[i] = (byte) (Math.sin(angle) * AMPLITUDE);
				
				if(i > 0 && ((int) (i % period)) == 0) {
					gap = period * pulseGap;
				}
			} else {
				output[i] = (byte) 0;
				--gap;
			}
		}
		
		return output;
	}

	
	private static byte[] createSawToothWaveBuffer(double freq, int ms, int pulseGap) {
	
		int samplesPerSecond = (int) ((ms * SAMPLE_RATE) / 1000);		
		byte[] output = new byte[samplesPerSecond];
		
		double period = (SAMPLE_RATE / freq);
		double gap = 0;
		
		for (int i = 0; i < output.length; i++) {
			
			if(gap <= 0) {
				double y = ((i/period) % 1);
				output[i] = (byte) (y * AMPLITUDE);

				if(i > 0 && ((int) (i % period)) == 0) {
					gap = period * pulseGap;
				}
			} else {
				output[i] = (byte) 0;
				--gap;
			}
		}

		return output;
	}
	
	
	private static void playSound(int waveType, int freqStart, 
			int freqStop, int steps, int timePerStep, int pulseGap) 
					throws LineUnavailableException {
		
		final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
		SourceDataLine line = AudioSystem.getSourceDataLine(af);
		
		line.open(af, SAMPLE_RATE);
		line.start();

		for (double freq = freqStart; freq <= freqStop; freq += steps) {			
			byte[] toneBuffer;
			
			switch (waveType) {
				case 0:
					toneBuffer = createSinWaveBuffer(freq, timePerStep, pulseGap);
					break;
				case 1:
					toneBuffer = createSawToothWaveBuffer(freq, timePerStep, pulseGap);
					break;
				default: return;
			}

			line.write(toneBuffer, 0, toneBuffer.length);
			System.out.println("Frequency: " + freq);
		}
	
		line.drain();
		line.close();
	}
	
	
	public static void main(String[] args) throws LineUnavailableException, InterruptedException {
			
		if(args.length == 6) {
			int waveType = Integer.parseInt(args[0]);
			int freqStart = Integer.parseInt(args[1]);
			int freqStop = Integer.parseInt(args[2]);			
			int steps = Integer.parseInt(args[3]);
			int timePerStep = Integer.parseInt(args[4]);
			int pulseGap =  Integer.parseInt(args[5]);
			
			playSound(waveType, freqStart, freqStop, steps, timePerStep, pulseGap);
			
		} else {
			System.out.println("Arguments:");
			System.out.println("1. Wave Type (0 = sine, 1 = Sawtooth)");
			System.out.println("2. Starting Frequency");
			System.out.println("3. End Frequency");
			System.out.println("4. Frequency Steps");
			System.out.println("5. Time per step (in ms)");
			System.out.println("6. Gap between 2 pulses (in nr of pulses)");			
		}
	}
}