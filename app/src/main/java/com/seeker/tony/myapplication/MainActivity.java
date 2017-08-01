package com.seeker.tony.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.meiyou.jet.annotation.JFindViewOnClick;
import com.meiyou.jet.process.Jet;
import com.meiyou.protocol.ProtocolProxy;
import com.seeker.tony.myapplication.common.Mock;
import com.seeker.tony.myapplication.protocol.TestInterface;

import java.util.HashMap;
import java.util.Map;

//@JUri("")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    @JFindViewOnClick(R.id.btn_findView)
    Button btnHello;

    @JFindViewOnClick(R.id.btn_findview_onclick)
    Button btnWorld;

    String temp = "";

    @JFindViewOnClick(R.id.btn_implement)
    Button btn_implement;

    @JFindViewOnClick(R.id.btn_log)
    Button btn_log;
    @JFindViewOnClick(R.id.btn_init)
    Button btn_init;

    String uri = "";
//    Router route = Router.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Jet.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onClick(View view) {
        int id = view.getId();

        ProtocolProxy proxy = ProtocolProxy.getInstance();
        switch (id) {
            case R.id.btn_findView:
                try {
                    proxy.create(TestInterface.class).test();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_findview_onclick:

                break;
            case R.id.btn_implement:

                break;
            case R.id.btn_log:

//                testLog(10);
//                testAOP();
                break;
            case R.id.btn_init:
                temp();
                break;
        }

    }

    private void temp() {

        ProtocolProxy.getInstance().init(this);
//        try {
//            Router.getInstance().registerAll();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        testUrl();
        createUri();
    }

    //    @Test("")
    private void test() {
//        HashMap map = RouterTable.map;
//        Log.w(TAG, "test: " + map.size());
//        URI uri = new URI("");
//        Uri.parse()
//        HelloChina helloChina = new HelloChina();

    }

    public void testUrl() {
        HashMap<String, Object> sampleMap = Mock.getSampleMap();
//        LinkedHashMap<String,Object> linkedHashMap=new LinkedHashMap<>(sampleMap);
        Uri uri = Uri.parse("meiyou:///home");
        Uri.Builder builder = uri.buildUpon();
        for (Map.Entry<String, Object> entry : sampleMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            builder.appendQueryParameter(key, value.toString());
        }
        uri = builder.build();
        String s = uri.toString();
        Log.e(TAG, "testUrl: " + s);

    }

    public void createUri() {
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme("meiyou");
        builder.authority("lingan.com");
        builder.path("/virtualpath/settings.xml");
        builder.appendQueryParameter("key", "value");
        builder.appendQueryParameter("data", "" + 1);
        builder.appendQueryParameter("chinese", "hell dodod");
        builder.appendQueryParameter("chinese2", "中文");

        Uri uri = builder.build();
        String s = uri.toString();
        Log.e(TAG, "testUrl: " + s);

//        URL url = new URL(uri.toString());
    }
}
