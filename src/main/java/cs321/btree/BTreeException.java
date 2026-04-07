package cs321.btree;

public class BTreeException extends Exception {
    private static final long serialVersionUID = 3256718485559195187L;

    /**
     * Default constructor.
     */
    BTreeException() {
    }

    /**
     * Constructor that supers a given message.
     *
     * @param msg  Message to be displayed when this is called.
     */
    BTreeException(String msg) {
        super(msg);
    }
}
