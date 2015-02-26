package org.uta.wavegenerator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Main {

	private static final float AMPLITUDE = 127f;	
	protected static final int SAMPLE_RATE = 16 * 1024;

	
	private static byte[] createSinWaveBuffer(double freq, int ms) {
		int samples = (int) ((ms * SAMPLE_RATE) / 1000);
		byte[] output = new byte[samples];
		
		double period = (double) SAMPLE_RATE / freq;
		
		for (int i = 0; i < output.length; i++) {
			double angle = 2.0 * Math.PI * i / period;
			output[i] = (byte) (Math.sin(angle) * AMPLITUDE);
		}

		return output;
	}

	
	private static byte[] createSawToothWaveBuffer(double freq, int ms) {
		int samples = (int) ((ms * SAMPLE_RATE) / 1000);
		byte[] output = new byte[samples];
		
		double period = (SAMPLE_RATE / freq);
				
		for (int i = 0; i < output.length; i++) {
			double y = ((i/period) % 1);
			output[i] = (byte) (y * AMPLITUDE);
		}

		return output;
	}
	
	
	private static void playSawTooth(int freqStart, int freqStop, int steps, int timePerStep, int gap) throws LineUnavailableException, InterruptedException {
		
		final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
		SourceDataLine line = AudioSystem.getSourceDataLine(af);
		
		line.open(af, SAMPLE_RATE);
		line.start();

		for (double freq = freqStart; freq <= freqStop; freq += steps) {
				byte[] toneBuffer = createSawToothWaveBuffer(freq, timePerStep);
				line.write(toneBuffer, 0, toneBuffer.length);
			
				if(gap != 0) {
					line.stop();
					Thread.sleep(gap);
					line.start();
				}
				
				System.out.println("Frequency: " + freq);
		}
	
		line.drain();
		line.close();
	}
	
	
	private static void playSineWave(int freqStart, int freqStop, int steps, int timePerStep, int gap) throws LineUnavailableException {
		
		final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
		SourceDataLine line = AudioSystem.getSourceDataLine(af);
		
		line.open(af, SAMPLE_RATE);
		line.start();

		for (double freq = freqStart; freq <= freqStop; freq += steps) {
			byte[] toneBuffer = createSinWaveBuffer(freq, timePerStep);
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
			int gap =  Integer.parseInt(args[5]);
			
			if(waveType == 0) {
				playSineWave(freqStart, freqStop, steps, timePerStep, gap);
			} else {
				playSawTooth(freqStart, freqStop, steps, timePerStep, gap);
			}
		} else {
			System.out.println("Arguments:");
			System.out.println("1. Wave Type (0 = sine, 1 = Sawtooth)");
			System.out.println("2. Starting Frequency");
			System.out.println("3. End Frequency");
			System.out.println("4. Frequency Steps");
			System.out.println("5. Time per step (in ms)");
			System.out.println("6. Gap between pulses (in ms)");			
		}
	}
}