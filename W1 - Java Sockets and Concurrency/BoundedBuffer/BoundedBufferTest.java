/**
 * Test program to exercise the BoundedBuffer implementation
 * DAS4 warmup exercise
 */
import java.util.Random;

public class BoundedBufferTest {

    private static final int numProducers = 3;
    private static final int numConsumers = 2;
		private static int size = 10;
		
    public static void main(String args[]) {
        
        if (args.length > 0) 
        	size = Integer.parseInt(args[0]);
        	
        
        BoundedBufferImpl bb = new BoundedBufferImpl(size);
        System.out.format("Bounded buffer	size: %d\n\n", size);
        	
        for (int i = 0; i < numConsumers; i++) {
            Consumer t = new Consumer(bb);
	    new Thread(t).start();
	}
	for (int i = 0; i < numProducers; i++) {
	    Producer t = new Producer(bb);
	    new Thread(t).start();
	}
    }
}
