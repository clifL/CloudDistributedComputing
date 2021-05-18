
import java.util.Random;

public class Consumer implements Runnable {
    private BoundedBufferImpl bb;
    private Random rGen;

    public Consumer(BoundedBufferImpl b) {
        bb = b;
        rGen = new Random();
    }

    public void run() {
        Thread t = Thread.currentThread();
        String myName = t.getName();
	System.out.format("Cons thread %s started\n", myName);
        while (!t.isInterrupted()) {
            int msec = rGen.nextInt(5000);
            try {
	        Thread.sleep(msec);
            } catch (InterruptedException e) {
            }
	    String str = (String)bb.remove();
	    System.out.format("Cons thread %s removed '%s'\n", myName, str);
        }
    }
}
