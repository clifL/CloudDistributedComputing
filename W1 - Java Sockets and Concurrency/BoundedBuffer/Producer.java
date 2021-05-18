import java.util.Random;

public class Producer implements Runnable {
    private BoundedBufferImpl bb;
    private Random rGen;
    private int counter = 0;

    public Producer(BoundedBufferImpl b) {
        bb = b;
        rGen = new Random();
    }

    public void run() {
        Thread t = Thread.currentThread();
        String myName = t.getName();
        System.out.format("Prod thread %s started\n", myName);
        while (!t.isInterrupted()) {
    	    int msec = rGen.nextInt(5000);
            try {
    	        Thread.sleep(msec);
            } catch (InterruptedException e) {}
            String str = new String(myName + "/" + counter++);
            bb.insert(str);
            System.out.format("Prod thread %s inserted '%s'\n", myName, str);
        }
    }
}
