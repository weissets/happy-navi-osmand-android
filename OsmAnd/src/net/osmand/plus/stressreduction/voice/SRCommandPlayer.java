package net.osmand.plus.stressreduction.voice;

import net.osmand.plus.voice.CommandBuilder;
import net.osmand.plus.voice.CommandPlayer;

import java.util.List;

import alice.tuprolog.Struct;

/**
 * Created by Tobias on 12/11/15.
 */
public class SRCommandPlayer implements CommandPlayer {

	@Override
	public String getCurrentVoice() {
		return null;
	}

	@Override
	public CommandBuilder newCommandBuilder() {
		return null;
	}

	@Override
	public void playCommands(CommandBuilder builder) {

	}

	@Override
	public void clear() {

	}

	@Override
	public List<String> execute(List<Struct> listStruct) {
		return null;
	}

	@Override
	public void updateAudioStream(int streamType) {

	}

	@Override
	public String getLanguage() {
		return null;
	}

	@Override
	public boolean supportsStructuredStreetNames() {
		return false;
	}

	@Override
	public void stop() {

	}
}
