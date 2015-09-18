package com.example.yum.testsibal;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity {
    private Button logbtn;
    private EditText idE;
    public String id;
    public String pass;
    public TextView txt;
    public EditText passE;
    String a;
    WebServerSender webServerSender;
    TextView Join;

    Bitmap bitmap;


    ImageView cloud1;
    ImageView cloud2;
    ImageView cloud3;

    ImageView background;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateThread();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        });

        thread.start();
    }

    private void updateThread() {
        TranslateAnimation moveAnimation = new TranslateAnimation(-300, 800, 0, 0);
        moveAnimation.setDuration(30000);
        moveAnimation.setRepeatCount(Animation.INFINITE);
        cloud1.setAnimation(moveAnimation);

        moveAnimation = new TranslateAnimation(-500, 900, 0, 0);
        moveAnimation.setDuration(37000);
        moveAnimation.setRepeatCount(Animation.INFINITE);
        cloud2.setAnimation(moveAnimation);

        moveAnimation = new TranslateAnimation(-600, 1200, 0, 0);
        moveAnimation.setDuration(28000);
        moveAnimation.setRepeatCount(Animation.INFINITE);
        cloud3.setAnimation(moveAnimation);

    }

    @Override
    protected void onPause() {
        background = (ImageView) findViewById(R.id.background);
        ((BitmapDrawable) background.getDrawable()).getBitmap().recycle();
        ImageView[] dum = {cloud1, cloud2, cloud3};
        for (int i = 0; i < dum.length; i++) {
            Drawable d = dum[i].getDrawable();
            if (d instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                bitmap.recycle();
                bitmap = null;
            }
            d.setCallback(null);
        }
    }

    protected void drawBigImage(ImageView imageView, int resId) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 1;
            options.inPurgeable = true;
            Bitmap src = BitmapFactory.decodeResource(getResources(), resId, options);
            Bitmap resize = Bitmap.createScaledBitmap(src, options.outWidth, options.outHeight, true);
            imageView.setImageBitmap(resize);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("sibalerror", e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.login);
        } catch (Exception e) {
            Log.e("sibalerror", e.toString());
        }

        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.login_background);
        //background.setImageBitmap(bitmap);
        cloud1 = (ImageView) findViewById(R.id.cloud1);
        cloud2 = (ImageView) findViewById(R.id.cloud2);
        cloud3 = (ImageView) findViewById(R.id.cloud3);
        background = (ImageView) findViewById(R.id.background);


        //현재 메모리 확인
//        Log.v("Dalvik", "   ");
//        Log.v("Dalvik", "Dalvik heap 영역 최대 크기 : " + String.valueOf(Runtime.getRuntime().maxMemory()));
//        Log.v("Dalvik", "Dalvik heap 영역 크기 : " + String.valueOf(Runtime.getRuntime().totalMemory()));
//        Log.v("Dalvik", "Dalvik heap free 크기 : " + String.valueOf(Runtime.getRuntime().freeMemory()));
//        Log.v("Dalvik", "Dalvik heap allocated 크기 : " + String.valueOf(
//                (Runtime.getRuntime().totalMemory()) - (Runtime.getRuntime().freeMemory())));
//        Log.v("Dalvik", "프로세스 당 메모리 크기의 한계 : " + String.valueOf(((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass()));


        logbtn = (Button) findViewById(R.id.Loginbtn);
        Drawable buttonBg = logbtn.getBackground();
        buttonBg.setAlpha(50);
        webServerSender = new WebServerSender("http://linux.kim82536.pe.kr:5000", "login", "POST");
        Join = (TextView) findViewById(R.id.join);
        Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent;
                myintent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(myintent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            }
        });
        logbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                idE = (EditText) findViewById(R.id.id_input);
                Sendmsg sendmsg = new Sendmsg();
                id = idE.getText().toString();
                passE = (EditText) findViewById(R.id.pw_input);
                pass = passE.getText().toString();
                txt = (TextView) findViewById(R.id.text);
                webServerSender.add("email", id);
                webServerSender.add("password", pass);

                sendmsg.execute();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class Sendmsg extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... param) {

            try {
                webServerSender.send();
                a = webServerSender.receiveResult();
                System.out.println("request : " + a);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            return a;

        }

        protected void onPostExecute(String s) {
            json(s);


        }

        void json(String s) {

            boolean check = false;
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            try {
                JSONObject jsonObject = new JSONObject(s);
                check = jsonObject.getBoolean("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (check == true) {
                Toast.makeText(getApplicationContext(), "성공적", Toast.LENGTH_LONG).show();
                finish();
                startActivity(mainIntent);

            } else {
                Toast.makeText(getApplicationContext(), "실패적", Toast.LENGTH_LONG).show();
                finish();
                startActivity(mainIntent);

            }

            webServerSender.clearArg();
        }


    }
}