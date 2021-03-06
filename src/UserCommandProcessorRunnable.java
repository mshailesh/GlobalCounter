

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This class responds to each user client connection made.
 */
public class UserCommandProcessorRunnable implements Runnable {

    private static String SUCESSS = "success";
    private static String FAILURE = "failure";
   
    final private Socket socket;
    final private GlobalDataHolder globalDataHolder;
    
    /**
     * Constructor.
     * @param socket
     * @param globalDataHolder
     */
    public UserCommandProcessorRunnable(Socket socket, 
            GlobalDataHolder globalDataHolder) {
        this.socket = socket;
        this.globalDataHolder = globalDataHolder;
    }
    
    
    @Override
    public void run() {
        InputStream in = null;
        OutputStream out = null;
        boolean responseWritten = false;
        try {
            
            System.out.println("Reading data");
            String command = "";
            in = socket.getInputStream();
            for (int b = 0; ((b = in.read()) >= 0);) {
                //System.out.println(b + " " + (char) b);
                command = command + (char)b;
                if(command.length() == 3) {
                    break;
                }
            }
            
            System.out.println("Got Command " + command);
            out = socket.getOutputStream();
            switch(command) {
                case "inc" :
                    globalDataHolder.incrementCounter();
                    out.write(SUCESSS.getBytes());
                    responseWritten = true;
                    break;
                case "dec" :
                    globalDataHolder.decrementCounter();
                    out.write(SUCESSS.getBytes());
                    responseWritten = true;
                    break;
                case "get" :
                    int count = globalDataHolder.getCount();
                    out.write(count);
                    responseWritten = true;
                    break;
                
               default:
                    out.write(FAILURE.getBytes());
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null) {
                try {
                    if(responseWritten == false) {
                        out.write(FAILURE.getBytes());
                    }
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
