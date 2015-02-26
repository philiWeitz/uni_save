package org.uta.serialport.activity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.uta.serialport.util.WaveFormGenerator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AudioWaveformActivity extends Activity {

	private ScheduledExecutorService scheduler;
	private SeekBar channel1;
	private TextView freqChannel1;
	
	private WAVE_FORM waveForm = WAVE_FORM.NONE;
	private WaveFormGenerator waveFormGenerator;
	
	
	private enum WAVE_FORM {
		NONE,
		SINE,
		SAWTOOTH;
	}
	
	
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
	
	
	public void OffButtonClick(View view) {
		waveForm = WAVE_FORM.NONE;
       	waveFormGenerator.stopPlaying();
       	removeFrequencyTimer();
       	
       	ToggleButton button = (ToggleButton) findViewById(R.id.toggle_button_timer);
       	button.setChecked(false);
	}
	
	public void SineWaveClick(View view) {
		waveForm = WAVE_FORM.SINE;
		waveFormGenerator.startPlayingSineWave(channel1.getProgress());
	}	
       	
	public void SawtoothWaveClick(View view) {
		waveForm = WAVE_FORM.SAWTOOTH;
       	waveFormGenerator.startPlayingSawtoothWave(channel1.getProgress());
	}       	

	public void IncreaseFrequencyTimerClick(View view) {
		ToggleButton button = (ToggleButton) view;
		
		if(button.isChecked()) {
			addFrequencyTimer();
		} else {
			removeFrequencyTimer();
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
			switch(waveForm) {
				case SINE:
					waveFormGenerator.startPlayingSineWave(channel1.getProgress());
					break;
				case SAWTOOTH:
					waveFormGenerator.startPlayingSawtoothWave(channel1.getProgress());
					break;
				default:
					break;
			}
		}
	};
	
	
	private void addFrequencyTimer() {
		scheduler = Executors.newSingleThreadScheduledExecutor();

			scheduler.scheduleAtFixedRate
			      (new Runnable() {
			         public void run() {
			            channel1.setProgress(channel1.getProgress() + 10);
			            
						switch(waveForm) {
							case SINE:
								waveFormGenerator.startPlayingSineWave(channel1.getProgress());
								break;
							case SAWTOOTH:
								waveFormGenerator.startPlayingSawtoothWave(channel1.getProgress());
								break;
							default:
								break;
						}			            
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
