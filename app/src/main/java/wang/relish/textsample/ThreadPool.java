package wang.relish.textsample;

import java.util.LinkedList;

/**
 * @author Relish Wang
 * @since 2019/03/05
 */
public enum  ThreadPool {
    DB(1);

    private LinkedList<Runnable> mRunnables = new LinkedList<>();
    private int size;

    ThreadPool(int size) {
        this.size = size;
    }

    public void exec(Runnable runnable){

    }
}
