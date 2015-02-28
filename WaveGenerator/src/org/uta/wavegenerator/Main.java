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

	
	private static byte[] createSawToothWaveBuffer(double freq, int ms, int pulseGap, int pulseWidth) {
	
		int samplesPerSecond = (int) ((ms * SAMPLE_RATE) / 1000);		
		byte[] output = new byte[samplesPerSecond];
		
		double period = (SAMPLE_RATE / freq);
		double gap = 0;
		double pulse = period * pulseWidth;
		
		for (int i = 0; i < output.length; i++) {
			
			if(gap < 1) {
				--pulse;
				
				double y = ((i/period) % 1);
				output[i] = (byte) (y * AMPLITUDE);

				if(pulse < 1 && ((int) (i % period)) == 0) {
					gap = period * pulseGap;
				}
			} else {
				output[i] = (byte) 0;
				--gap;
				
				if(gap < 1) {
					pulse = period * pulseWidth;
				}
			}
		}

		return output;
	}
	
	
	
	private static byte[] createCapacitiveWaveBuffer(double freq, int ms, int pulseGap) {
		int samples = (int) ((ms * SAMPLE_RATE) / 1000);
		byte[] output = new byte[samples];
		
		double period = (SAMPLE_RATE / freq);
		double gap = 0;
		
		for (int i = 0; i < output.length; i++) {
			
			if(gap <= 0) {
				double y = 1 - Math.pow(Math.E, - ((i % period)/50));
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
	
	
	private static byte[] createSquareWaveBuffer(double freq, int ms, int pulseGap) {
		int samples = (int) ((ms * SAMPLE_RATE) / 1000);
		byte[] output = new byte[samples];
		
		double period = (SAMPLE_RATE / freq);
		double gap = 0;
		
		for (int i = 0; i < output.length; i++) {
			
			if(gap <= 0) {
				double y = Math.pow(((i % period) / period),2);					
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
	
	
	private static void playSound(int waveType, int direction, int freqStart, 
			int freqStop, int steps, int timePerStep, int pulseGap, int pulseWidth) 
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
					toneBuffer = createSawToothWaveBuffer(freq, timePerStep, pulseGap, pulseWidth);
					break;
				case 2: 
					toneBuffer = createCapacitiveWaveBuffer(freq, timePerStep, pulseGap);
					break;
				case 3:
					toneBuffer = createSquareWaveBuffer(freq, timePerStep, pulseGap);
					break;
				default: return;
			}

			if(direction == 1) {
				byte[] reverse = new byte[toneBuffer.length];
				
				for(int i = 0; i < toneBuffer.length; i++) {
					reverse[i] = toneBuffer[toneBuffer.length - 1 - i];
				}
				
				toneBuffer = reverse;
			}
			
			line.write(toneBuffer, 0, toneBuffer.length);
			System.out.println("Frequency: " + freq);
		}
	
		line.drain();
		line.close();
	}
	
	
	public static void main(String[] args) throws LineUnavailableException, InterruptedException {
			
		if(args.length == 8) {
			int waveType = Integer.parseInt(args[0]);
			int direction = Integer.parseInt(args[1]);			
			int freqStart = Integer.parseInt(args[2]);
			int freqStop = Integer.parseInt(args[3]);			
			int steps = Integer.parseInt(args[4]);
			int timePerStep = Integer.parseInt(args[5]);
			int pulseGap =  Integer.parseInt(args[6]);
			int pulseWidth =  Integer.parseInt(args[7]);			
			
			playSound(waveType, direction, freqStart, freqStop, steps, timePerStep, pulseGap, pulseWidth);
			
		} else {
			System.out.println("Arguments:");			
			System.out.println("1. Wave Type (0 = sine, 1 = Sawtooth, 2 = Square function)");
			System.out.println("2. Direction (0 or 1)");			
			System.out.println("3. Starting Frequency");
			System.out.println("4. End Frequency");
			System.out.println("5. Frequency Steps");
			System.out.println("6. Time per step (in ms)");
			System.out.println("7. Gap between 2 pulses (in nr of pulses)");	
			System.out.println("8. Nr of pulses");				
		}
	}
}