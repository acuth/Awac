package acuth.awac;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by adrian on 28/02/15.
 */
public class WebViewFragment extends Fragment {
        private View mRootView;
        private String mUrl;

        public WebViewFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            System.out.println("WebViewFragment.onCreateView()");
            System.out.println(" - mRootView="+mRootView);

            if (mRootView == null) {
                mRootView = inflater.inflate(R.layout.web_view_fragment, container, false);
                System.out.println(" - load(" + mUrl + ")");
                ((WebView) mRootView.findViewById(R.id.web_view)).loadUrl(mUrl);
            }

            return mRootView;
        }

        public void init(String url) {
            mUrl = url;
        }
}
