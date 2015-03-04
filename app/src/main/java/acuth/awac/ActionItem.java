package acuth.awac;

/**
 * Created by adrian on 02/03/15.
 */
public class ActionItem {
    public final String mLabel;
    public final String mAction;

    ActionItem(String label,String action) {
        mLabel = label;
        mAction = action;
    }

    public String toString() {
        return "{ActionItem label:"+mLabel+" action:"+mAction+"}";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionItem)) return false;
        ActionItem that = (ActionItem) o;
        return this.mLabel.equals(that.mLabel) && this.mAction.equals(that.mAction);
    }

    @Override
    public int hashCode() {
        String s = mLabel+":"+mAction;
        return s.hashCode();
    }
}
