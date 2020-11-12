package simpledb;

import java.util.*;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends Operator {

    private static final long serialVersionUID = 1L;

    private JoinPredicate predicate;
    private DbIterator leftChild;
    private DbIterator rightChild;
    private Tuple rightTuple;

    /**
     * Constructor. Accepts two children to join and the predicate to join them
     * on
     *
     * @param p      The predicate to use to join the children
     * @param child1 Iterator for the left(outer) relation to join
     * @param child2 Iterator for the right(inner) relation to join
     */
    public Join(JoinPredicate p, DbIterator child1, DbIterator child2) {
        // some code goes here
        this.predicate = p;
        this.leftChild = child1;
        this.rightChild = child2;
    }

    public JoinPredicate getJoinPredicate() {
        // some code goes here
        return this.predicate;
    }

    /**
     * @return the field name of join field1. Should be quantified by
     * alias or table name.
     */
    public String getJoinField1Name() {
        // some code goes here
        return this.leftChild.getTupleDesc().getFieldName(this.predicate.getField1());
    }

    /**
     * @return the field name of join field2. Should be quantified by
     * alias or table name.
     */
    public String getJoinField2Name() {
        // some code goes here
        return this.rightChild.getTupleDesc().getFieldName(this.predicate.getField2());
    }

    /**
     * @see simpledb.TupleDesc#merge(TupleDesc, TupleDesc) for possible
     * implementation logic.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return TupleDesc.merge(this.leftChild.getTupleDesc(), this.rightChild.getTupleDesc());
    }

    public void open() throws DbException, NoSuchElementException, TransactionAbortedException {
        // some code goes here
        this.leftChild.open();
        this.rightChild.open();
        super.open();
    }

    public void close() {
        // some code goes here
        super.close();
        this.leftChild.close();
        this.rightChild.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        this.leftChild.rewind();
        this.rightChild.rewind();
        this.rightTuple = null;
    }

    /**
     * Returns the next tuple generated by the join, or null if there are no
     * more tuples. Logically, this is the next tuple in r1 cross r2 that
     * satisfies the join predicate. There are many possible implementations;
     * the simplest is a nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of Join
     * are simply the concatenation of joining tuples from the left and right
     * relation. Therefore, if an equality predicate is used there will be two
     * copies of the join attribute in the results. (Removing such duplicate
     * columns can be done with an additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     *
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        if (this.rightTuple == null && this.rightChild.hasNext()) {
            this.rightTuple = this.rightChild.next();
        }
        while (this.rightTuple != null) {
            while (leftChild.hasNext()) {
                Tuple leftTuple = this.leftChild.next();
                if (this.predicate.filter(leftTuple, rightTuple)) {
                    // TODO: 2020/11/8 merge function
                    return Tuple.merge(leftTuple, rightTuple);
                }
            }
            this.leftChild.rewind();
            if (this.rightChild.hasNext()) {
                this.rightTuple = this.rightChild.next();
            } else {
                this.rightTuple = null;
            }
        }
        return null;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        return new DbIterator[] { this.leftChild, this.rightChild };
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        if (children.length >= 2) {
            this.leftChild = children[0];
            this.rightChild = children[1];
        }
    }

}