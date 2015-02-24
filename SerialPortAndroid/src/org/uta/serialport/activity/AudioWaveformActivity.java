package org.uta.serialport.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.uta.serialport.util.WaveFormGenerator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class AudioWaveformActivity extends Activity {

	private ScheduledExecutorService scheduler;
	private SeekBar channel1;
	private TextView freqChannel1;
	
	private WaveFormGenerator waveFormGenerator;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_waveform);
		
		initUI();
	}
	
	
	private void initUI() {
		channel1 = (SeekBar) findViewById(R.id.seekBar_channel1);
		channel1.setMax(3000);
		channel1.setProgress(300);
		channel1.setOnSeekBarChangeListener(channel1UpdateListener);
		
		freqChannel1 = (TextView) findViewById(R.id.textView_channel1_frequency);
		freqChannel1.setText("300");
		
		waveFormGenerator = new WaveFormGenerator();
	}
	
	
	public void onRadioButtonClicked(View view) {
		
		if(((RadioButton) view).isChecked()) {   
			
		    switch(view.getId()) {
		        case R.id.radioButton_wave_off:
		        	removeFrequencyTimer();
		        	waveFormGenerator.stopPlaying();
		            break;
		        case R.id.radioButton_sine_wave:
		        	waveFormGenerator.startPlaying(channel1.getProgress());
		        	addFrequencyTimer();
		            break;
		        default:
		        	break;
		    }
		}
	}
	
	
	private OnSeekBarChangeListener channel1UpdateListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			freqChannel1.setText(String.valueOf(progress));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			waveFormGenerator.startPlaying(channel1.getProgress());
		}
	};
	
	
	private void addFrequencyTimer() {
		scheduler = Executors.newSingleThreadScheduledExecutor();

			scheduler.scheduleAtFixedRate
			      (new Runnable() {
			         public void run() {
			            channel1.setProgress(channel1.getProgress() + 10);
			            waveFormGenerator.startPlaying(channel1.getProgress());
			         }
			      }, 0, 1, TimeUnit.SECONDS);
	}
	
	
	private void removeFrequencyTimer() {
		if(null != scheduler) {
			scheduler.shutdownNow();
			
		}
		scheduler = null;
	}
}
