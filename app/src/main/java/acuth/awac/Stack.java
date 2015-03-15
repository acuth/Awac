package acuth.awac;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 08/03/15.
 */
public class Stack {
    private List<Frame> mFrames;

    Stack() {
        mFrames = new ArrayList<Frame>();
    }

    Frame peek() {
        return mFrames.get(mFrames.size()-1);
    }

    void clear() {
        mFrames.clear();
    }

    void push(Frame frame) {
        mFrames.add(frame);
    }

    Frame pop() {
        mFrames.remove(mFrames.size()-1);
        return mFrames.size() == 0 ? null : mFrames.get(mFrames.size()-1);
    }

    int depth() {
        return mFrames.size()-1;
    }
}
