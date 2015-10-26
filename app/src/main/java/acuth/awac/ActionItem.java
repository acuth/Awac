package acuth.awac;

import org.json.JSONObject;

/**
 * Created by adrian on 02/03/15.
 */
public class ActionItem {
    public final String mLabel;
    public final String mIcon;
    public final String mAction;

    private ActionItem(String label, String icon, String action) {
        mLabel = label;
        mIcon = icon;
        mAction = action;
    }

    public String toString() {
        return "{ActionItem label:" + mLabel + " icon:" + mIcon + " action:" + mAction + "}";
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

    static ActionItem fromJson(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String label = jsonObj.getString("label");
            String icon = jsonObj.has("icon") ? jsonObj.getString("icon") : null;
            String action = jsonObj.getString("action");
            return new ActionItem(label, icon, action);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
