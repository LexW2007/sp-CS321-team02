package cs321.btree;

/** Class representing a node in the B-Tree
 * The B-Tree is stored on disk, so the node contains a byte offset to its location on disk.
 * @author Lex Watts, Damian Skeen
 */
public class BTreeNode implements KeyInterface<Long>{
    boolean isLeaf;
    int numKeys;
    TreeObject[] keys;      // size = 2t - 1
    long[] children;        // size = 2t
    long myAddress;         // byte offset on disk
    
    public BTreeNode(int degree, boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.numKeys = 0;
        this.keys = new TreeObject[2 * degree - 1];
        this.children = new long[2 * degree];
    }

    @Override
    public Long getKey() {
        return myAddress; // disk offset is the cache key
    }
}
