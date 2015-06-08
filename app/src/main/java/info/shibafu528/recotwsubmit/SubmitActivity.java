package info.shibafu528.recotwsubmit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SubmitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDialog(0);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
            case 0:
                return new AlertDialog.Builder(this)
                        .setTitle("recotwに記録")
                        .setMessage(String.format("以下のツイートをrecotwに登録します。本当にいいね？\n@%s:%s",
                                getIntent().getStringExtra("user_screen_name"),
                                getIntent().getStringExtra(Intent.EXTRA_TEXT)))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showDialog(1);
                            }
                        })
                        .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        })
                        .create();
            case 1:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("recotwに登録");
                dialog.setMessage("黒歴史を送信中です。しばらくお待ちください...");
                dialog.setIndeterminate(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                new AsyncTask<Long, Void, String>() {
                    @Override
                    protected String doInBackground(Long... params) {
                        try {
                            URL url = new URL("http://api.recotw.black/1/tweet/record_tweet");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setUseCaches(false);
                            conn.setRequestMethod("POST");
                            PrintWriter writer = new PrintWriter(conn.getOutputStream());
                            writer.print("id=" + params[0]);
                            writer.close();
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String result = "";
                            String s;
                            while ((s = br.readLine()) != null) {
                                result += "\n" + s;
                            }
                            Log.d("Response", result);
                            return "黒歴史を登録しました！";
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "通信エラーで登録失敗しました";
                        }
                        return "原因不明のエラーで登録失敗しました";
                    }

                    @Override
                    protected void onPostExecute(String string) {
                        super.onPostExecute(string);
                        dismissDialog(1);
                        Toast.makeText(SubmitActivity.this, string, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }.execute(Long.valueOf(getIntent().getStringExtra("id")));
                return dialog;
        }
        return super.onCreateDialog(id, args);
    }
}
