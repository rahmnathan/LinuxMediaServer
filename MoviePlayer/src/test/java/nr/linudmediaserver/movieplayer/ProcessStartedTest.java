package nr.linudmediaserver.movieplayer;

import org.junit.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessStartedTest {

    @Test
    public void checkIfProcessHasStartedTest() throws Exception{
        Process process = new ProcessBuilder("/bin/bash", "-c", "ls").start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String test = br.readLine();
        Assert.assertNotNull(test);
    }
}
