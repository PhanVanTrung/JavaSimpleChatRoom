/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cosc2082_assignment2_s3479773_s3479765_s3446356;

/**
 *
 * @author VS9 X64Bit
 */
import java.net.*;
import java.io.*;

import javax.sound.sampled.*;

public class ReceiveAudio implements Runnable {

    Socket connection = null;
    DataInputStream soundIn = null;
    SourceDataLine inSpeaker = null;

    public ReceiveAudio(Socket conn) throws Exception {
        connection = conn;
        soundIn = new DataInputStream(connection.getInputStream());
        AudioFormat af = new AudioFormat(8000.0f, 8, 1, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
        inSpeaker = (SourceDataLine) AudioSystem.getLine(info);
        inSpeaker.open(af);
    }

    public void run() {
        int bytesRead = 0;
        byte[] inSound = new byte[1];
        inSpeaker.start();
        while (bytesRead != -1) {
            try {
                bytesRead = soundIn.read(inSound, 0, inSound.length);
            } catch (Exception e) {
            }
            if (bytesRead >= 0) {
                inSpeaker.write(inSound, 0, bytesRead);
            }
        }
    }
}
