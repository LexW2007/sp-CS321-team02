package cs321.btree;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/** Class representing a B-Tree stored on disk. The B-Tree supports insertion of SSH keys and their counts,
 * as well as dumping the tree to a file or database. The B-Tree is implemented using a RandomAccessFile
 * to read/write nodes from disk, and each node is represented by the BTreeNode class.
 * @author Lex Watts
 */
public class BTree implements BTreeInterface
{
    private int degree;
    private int maxKeys;
    private int maxChildren;
    private int nodeSize; // size of each node in bytes
    private long rootAddress;
    private long nextFreeAddress;
    private long size;              // number of keys in the tree
    private long numberOfNodes;     // number of nodes created
    private RandomAccessFile file;

    
    /** Construct a BTree with the default degree (t = 2).
    * Used by testCreate() in BTreeTest.
    * @param filename Name of the file where the BTree will be stored.
    * @throws BTreeException If the file cannot be created or written to.
    */
    public BTree(String filename) throws BTreeException {
        this(2, filename); // default degree = 2
    }

     /** Construct a BTree with a specific degree.
     * Initializes maxKeys and maxChildren based on degree, nodeSize based on TreeObject.BYTES,
     * RandomAccessFile for disk storage, and Root node at address 0
     * @param degree Minimum degree t of the BTree.
     * @param filename File used for persistent storage.
     * @throws BTreeException If any I/O error occurs.
     */
    public BTree(int degree, String filename) throws BTreeException {
        try {
            this.degree = degree;
            this.maxKeys = 2 * degree - 1;
            this.maxChildren = 2 * degree;
            this.nodeSize = computeNodeSize();

            this.file = new RandomAccessFile(filename, "rw");

            this.nextFreeAddress = 0;

            BTreeNode root = new BTreeNode(degree, true);
            root.myAddress = allocateNodeAddress();
            diskWrite(root);

            this.rootAddress = root.myAddress;
            this.numberOfNodes = 1;
            this.size = 0;

        } catch (IOException e) {
            throw new BTreeException("Error creating BTree");
        }
    }

    public BTree(int degree, int nodeSize) throws BTreeException {
        this.degree = degree;
        this.maxKeys = 2 * degree - 1;
        this.maxChildren = 2 * degree;
        this.nodeSize = nodeSize;
        this.nextFreeAddress = 0;
        this.numberOfNodes = 0;
        this.size = 0;
    }

     /** Allocate the next free byte offset in the file for a new node.
     * Nodes are fixed-size, so we simply advance by nodeSize.
     * @return The file offset where the new node should be written.
     */
    private long allocateNodeAddress() {
        long address = nextFreeAddress;
        nextFreeAddress += nodeSize;
        return address;
    }

     /** Write a BTreeNode to disk at its assigned address.
     * Node layout on disk:
     *   [1 byte]   isLeaf flag
     *   [3 bytes]  padding
     *   [4 bytes]  numKeys
     *   For each key slot (maxKeys):
     *       [64 bytes] padded UTF-8 key string
     *       [8 bytes]  count
     *   For each child pointer (maxChildren):
     *       [8 bytes]  file offset of child node
     * @param node The node to serialize and write.
     * @throws IOException If writing to disk fails.
     */
    private void diskWrite(BTreeNode node) throws IOException {
        file.seek(node.myAddress);

        ByteBuffer buffer = ByteBuffer.allocate(nodeSize);

        // Write header
        buffer.put((byte) (node.isLeaf ? 1 : 0));  // 1 byte
        buffer.put(new byte[3]);                   // padding
        buffer.putInt(node.numKeys);               // 4 bytes

        // Write keys (fixed number)
        for (int i = 0; i < maxKeys; i++) {
            if (i < node.numKeys && node.keys[i] != null) {

                // --- Write 64‑byte padded key ---
                byte[] keyBytes = new byte[64];
                byte[] actual = node.keys[i].getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                System.arraycopy(actual, 0, keyBytes, 0, Math.min(actual.length, 64));
                buffer.put(keyBytes);

                // --- Write 8‑byte count ---
                buffer.putLong(node.keys[i].getCount());

            } else {
                // Empty slot → write 72 bytes of zeros
                buffer.put(new byte[64]);  // empty key
                buffer.putLong(0L);        // empty count
            }
        }

        // Write children (fixed number)
        for (int i = 0; i < maxChildren; i++) {
            buffer.putLong(node.children[i]);
        }

        file.write(buffer.array());
    }

    /** Read a BTreeNode from disk at the given address.
     * Reconstructs the node by reversing the serialization
     * performed in diskWrite().
     * @param address File offset where the node is stored.
     * @return A fully reconstructed BTreeNode.
     * @throws IOException If reading from disk fails.
     */
    private BTreeNode diskRead(long address) throws IOException {
        file.seek(address);

        ByteBuffer buffer = ByteBuffer.allocate(nodeSize);
        file.read(buffer.array());

        boolean isLeaf = buffer.get() == 1;
        buffer.get(new byte[3]); // padding
        int numKeys = buffer.getInt();

        BTreeNode node = new BTreeNode(degree, isLeaf);
        node.myAddress = address;
        node.numKeys = numKeys;

        // Read keys
        for (int i = 0; i < maxKeys; i++) {

            // --- Read 64‑byte padded key ---
            byte[] keyBytes = new byte[64];
            buffer.get(keyBytes);
            String key = new String(keyBytes, java.nio.charset.StandardCharsets.UTF_8).trim();

            // --- Read 8‑byte count ---
            long count = buffer.getLong();

            if (i < numKeys) {
                node.keys[i] = new TreeObject(key, count);
            }
        }

        // Read children
        for (int i = 0; i < maxChildren; i++) {
            node.children[i] = buffer.getLong();
        }

        return node;
    }

    /** Compute the fixed size (in bytes) of a serialized BTreeNode.
     * This depends on: maxKeys (2t - 1), maxChildren (2t), and TreeObject.BYTES (72 bytes per key)
     * @return Total number of bytes required for one node.
     */
    private int computeNodeSize() {
        int size = 0;

        size += 1;  // isLeaf
        size += 3;  // padding
        size += 4;  // numKeys

        size += maxKeys * TreeObject.BYTES; // 72 bytes per key (64 for key + 8 for count)
        size += maxChildren * Long.BYTES;   // 8 bytes per child pointer

        return size;
    }

    /** @inheritDoc */
    @Override
    public long getSize() {
        return size;
    }

    /** @inheritDoc */
    @Override
    public int getDegree() {
        return degree;
    }

    /** @inheritDoc */
    @Override
    public long getNumberOfNodes() {
        return numberOfNodes;
    }

    /** @inheritDoc */
    @Override
    public int getHeight() {
        if (size == 0) {
            return 0; // empty tree has height 0
        }
        int height = 0;
        try {
            BTreeNode node = diskRead(rootAddress);
            while (!node.isLeaf) {
                height++;
                node = diskRead(node.children[0]); // always go to leftmost child
            }
        } catch (IOException e) {
            // In case of I/O error, we can return -1 or throw a runtime exception
            return -1;
        }
        return height;
    }

    /** @inheritDoc */
    @Override
    public void insert(TreeObject obj) throws IOException {
        BTreeNode root = diskRead(rootAddress);

        // If key already exists, increment count and return (size unchanged)
        TreeObject existing = search(obj.getKey());
        if (existing != null) {
            existing.setCount(existing.getCount() + obj.getCount());
            updateNodeContainingKey(rootAddress, obj.getKey(), existing);
            return;
        }

        // If root is full, split it
        if (root.numKeys == maxKeys) {
            BTreeNode newRoot = new BTreeNode(degree, false);
            newRoot.myAddress = allocateNodeAddress();
            newRoot.children[0] = rootAddress;

            splitChild(newRoot, 0, root);

            // Decide which child to insert into
            int i = (obj.getKey().compareTo(newRoot.keys[0].getKey()) > 0) ? 1 : 0;
            BTreeNode child = diskRead(newRoot.children[i]);
            insertNonFull(child, obj);
            diskWrite(child);

            diskWrite(newRoot);
            rootAddress = newRoot.myAddress;
            numberOfNodes++;
        } else {
            insertNonFull(root, obj);
            diskWrite(root);
        }

        size++;
    }

    private boolean updateNodeContainingKey(long address, String key, TreeObject updated) throws IOException {
        BTreeNode node = diskRead(address);

        int i = 0;
        while (i < node.numKeys && key.compareTo(node.keys[i].getKey()) > 0) {
            i++;
        }

        if (i < node.numKeys && key.equals(node.keys[i].getKey())) {
            node.keys[i] = updated;
            diskWrite(node);
            return true;
        }

        if (!node.isLeaf) {
            return updateNodeContainingKey(node.children[i], key, updated);
        } else {
            return false; // key not found in leaf (should not happen since we checked existence before)
        }
    }
    
    


     /** Insert a key into a node that is guaranteed not to be full.
     * If the node is a leaf:
     *   - Shift keys to make room
     *   - Insert the new key
     * If the node is internal:
     *   - Descend into the correct child
     *   - Split the child first if it is full
     * @param node The node to insert into.
     * @param obj The key/count pair to insert.
     * @throws IOException If disk I/O fails.
     */
    private void insertNonFull(BTreeNode node, TreeObject obj) throws IOException {
        int i = node.numKeys - 1;

        if (node.isLeaf) {
            // Shift keys to make room
            while (i >= 0 && obj.getKey().compareTo(node.keys[i].getKey()) < 0) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }

            // Insert new key
            node.keys[i + 1] = obj;
            node.numKeys++;

            diskWrite(node);
        } else {
            // Find child to descend into
            while (i >= 0 && obj.getKey().compareTo(node.keys[i].getKey()) < 0) {
                i--;
            }
            i++;

            BTreeNode child = diskRead(node.children[i]);

            // If child is full, split it
            if (child.numKeys == maxKeys) {
                splitChild(node, i, child);

                // After split, decide which of the two children to descend into
                if (obj.getKey().compareTo(node.keys[i].getKey()) > 0) {
                    i++;
                }
                child = diskRead(node.children[i]);
            }

            insertNonFull(child, obj);
        }
    }

    /**
     * Split a full child node during insertion.
     * Standard B-Tree split operation:
     *   - Create a new node z
     *   - Move t-1 keys from child to z
     *   - Move t children (if internal)
     *   - Promote the median key to the parent
     *   - Update parent pointers
     * @param parent The parent node.
     * @param index Index of the child being split.
     * @param child The full child node.
     * @throws IOException If disk I/O fails.
     */
    private void splitChild(BTreeNode parent, int index, BTreeNode child) throws IOException {
        int t = degree;

        // Create new node z
        BTreeNode z = new BTreeNode(t, child.isLeaf);
        z.myAddress = allocateNodeAddress();

        // Move last t-1 keys from child to z
        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = child.keys[j + t];
            child.keys[j + t] = null;
        }

        // If internal, move children
        if (!child.isLeaf) {
            for (int j = 0; j < t; j++) {
                z.children[j] = child.children[j + t];
                child.children[j + t] = 0;
            }
        }

        z.numKeys = t - 1;
        child.numKeys = t - 1;

        // Shift parent's children to make room
        for (int j = parent.numKeys; j >= index + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[index + 1] = z.myAddress;

        // Shift parent's keys to make room
        for (int j = parent.numKeys - 1; j >= index; j--) {
            parent.keys[j + 1] = parent.keys[j];
        }

        // Promote median key
        parent.keys[index] = child.keys[t - 1];
        child.keys[t - 1] = null;

        parent.numKeys++;

        // Write all nodes
        diskWrite(child);
        diskWrite(z);
        diskWrite(parent);

        numberOfNodes++;
    }

    /** @inheritDoc */
   @Override
    public void dumpToFile(PrintWriter out) throws IOException {
        if (size == 0) return;
        dumpNodeToFile(rootAddress, out);
    }

    private void dumpNodeToFile(long address, PrintWriter out) throws IOException {
        BTreeNode node = diskRead(address);
        for (int i = 0; i < node.numKeys; i++) {
            if (!node.isLeaf) {
                dumpNodeToFile(node.children[i], out);
            }
            if (node.keys[i] != null) {
                out.println(node.keys[i].getKey() + "," + node.keys[i].getCount());
            }
        }
        if (!node.isLeaf) {
            dumpNodeToFile(node.children[node.numKeys], out);
        }
    }


    /** @inheritDoc */
    @Override
    public void dumpToDatabase(String dbName, String tableName) throws IOException {
        String filename = dbName + "_" + tableName + ".csv";
        try (PrintWriter pw = new PrintWriter(new java.io.FileWriter(filename))) {
            dumpToFile(pw);
        }
    }


    /** @inheritDoc */
    @Override
    public TreeObject search(String key) throws IOException {
        BTreeNode node = diskRead(rootAddress);
        return searchRecursive(node, key);
    }

    private TreeObject searchRecursive(BTreeNode node, String key) throws IOException {
        int i = 0;

        // Find the first key greater than or equal to the search key
        while (i < node.numKeys && key.compareTo(node.keys[i].getKey()) > 0) {
            i++;
        }

        // If we found the key, return it
        if (i < node.numKeys && key.equals(node.keys[i].getKey())) {
            return node.keys[i];
        }

        // If this is a leaf node, the key is not found
        if (node.isLeaf) {
            return null;
        }

        // Otherwise, descend into the appropriate child
        BTreeNode child = diskRead(node.children[i]);
        return searchRecursive(child, key);
    }

    /** @inheritDoc */
    @Override
    public void delete(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * Perform an inorder traversal of the B-Tree to retrieve all keys in sorted order.
     * @return
     * @throws IOException
     */
    public String[] getSortedKeyArray() throws IOException {
        // If tree is empty, return empty array
        if (size == 0) {
            return new String[0];
        }

        ArrayList<String> keys = new ArrayList<>();
        BTreeNode root = diskRead(rootAddress);

        inorderTraversal(root, keys);

        return keys.toArray(new String[0]);
    }

    /**
     * Recursive helper method for inorder traversal of the B-Tree. 
     * Visits left child, then key, then right child.
     * @param node
     * @param keys
     * @throws IOException
     */
    private void inorderTraversal(BTreeNode node, ArrayList<String> keys) throws IOException {

        int i;

        // Traverse children and keys in sorted order
        for (i = 0; i < node.numKeys; i++) {

            // Visit left child
            if (!node.isLeaf) {
                BTreeNode child = diskRead(node.children[i]);
                inorderTraversal(child, keys);
            }

            // Visit key
            keys.add(node.keys[i].getKey());
        }

        // Visit last child
        if (!node.isLeaf) {
            BTreeNode child = diskRead(node.children[i]);
            inorderTraversal(child, keys);
        }
    }

    public void inOrderTraversal(java.util.function.Consumer<TreeObject> visitor) throws IOException {
        inOrderRecursive(rootAddress, visitor);
    }

    private void inOrderRecursive(long nodeAddr, java.util.function.Consumer<TreeObject> visitor) throws IOException {
        BTreeNode node = diskRead(nodeAddr);

        for (int i = 0; i < node.numKeys; i++) {

            // Visit left child
            if (!node.isLeaf) {
                inOrderRecursive(node.children[i], visitor);
            }

            // Visit key (TreeObject)
            visitor.accept(node.keys[i]);

        }

        // Visit last child
        if (!node.isLeaf) {
            inOrderRecursive(node.children[node.numKeys], visitor);
        }
    }
}
