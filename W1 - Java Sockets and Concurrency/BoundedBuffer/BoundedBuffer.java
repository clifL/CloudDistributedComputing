/**
 * Interface that must be supported by any implementation of a Bounded Buffer
 *
 * In addition, an implementation must provide two constructors:
 * 	BBImpl(int size);	create a BB that can hold "size" elements
 * 	BBImpl();               create a BB of a default size
 */

public interface BoundedBuffer {
    /**
     * method to insert "o" at the end of the end of the buffer
     * if no room, must wait until another thread has invoked remove()
     *
     * @param o Object to insert
     */ 
    public void insert(Object o);

    /**
     * method to remove "o" from the beginning of the buffer
     * if empty, must wait until another thread has invoked insert()
     *
     * @result Object removed
     */
    public Object remove();
}
