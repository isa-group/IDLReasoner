package es.us.isa.idlreasoner.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private BufferedReader bufferedReader;

    public StreamGobbler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public BufferedReader getReader() {
        return bufferedReader;
    }
}
