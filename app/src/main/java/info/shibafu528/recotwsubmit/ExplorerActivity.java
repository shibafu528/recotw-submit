package info.shibafu528.recotwsubmit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class ExplorerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String screenName = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        Uri uri = Uri.parse("http://recotw.chitoku.jp/?username=" + screenName);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
        finish();
    }
}
