package GameClasses;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    private Clip clip;

    //in loop mostly copy paste
    public void playBackgroundMusic(String filePath, float volume) {
        try {
            URL url = this.getClass().getClassLoader().getResource(filePath);
            if (url == null) 
            {
                throw new IllegalArgumentException("The audio file was not found: " + filePath);
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            
            if (volume <= 0f) 
            {
                volume = 0.0001f; //for any defoult
            }

            float dB = (float) (Math.log(volume) / Math.log(10) * 20);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(dB);

            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) 
        {
            e.printStackTrace();
        }
    }

    //only once modified copy paste 
    public void playSound(String filePath, float volume) 
    {
        try {
            URL url = this.getClass().getClassLoader().getResource(filePath);
            if (url == null) 
            {
                throw new IllegalArgumentException("The audio file was not found: " + filePath);
            }
            Clip soundClip = AudioSystem.getClip();
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            soundClip.open(audioIn);

            //Set volume it rec like this
            if (volume < 0f || volume > 1f) 
            {
                throw new IllegalArgumentException("Volume not valid: " + volume);
            }
            FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);

            //close the clip when the sound finished
            soundClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) 
                {
                    soundClip.close();
                }
            });

            soundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) 
        {
            e.printStackTrace();
        }
    }

    public void stop() 
    {
        if (clip != null) 
        {
            clip.stop();
            clip.close();
        }
    }
}
