package com.dinkominfo.recyclecrud;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dinkominfo.recyclecrud.model.Kontak;
import com.dinkominfo.recyclecrud.util.RequestPost;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/*
Create by Ryan Fabella (ryanthe@gmail.com)
 */
public class MainActivity extends AppCompatActivity {
    private XRecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private List<Kontak> kt = new ArrayList<>();
    private int refreshTime = 0;
    private int times = 0;
    private RequestPost RP;
    int currentPage = 0;
    private int totalqty=0;
    private int limit = 50;
    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CRUD");
        getSupportActionBar().setSubtitle("Training dinkominfo Surabaya");
        mRecyclerView = (XRecyclerView)this.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getData();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refreshTime ++;
                times = 0;
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        kt.clear();
                        //Log.i("onRefresh","dipanggil");
                        setCurrentPage(0);
                        if (getApplicationContext()!=null) {
                            getData();
                        }
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    }
                }, 1000);            //refresh data here
            }

            @Override
            public void onLoadMore() {
                if((totalqty/limit) > getCurrentPage()){
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            //Log.i("currpage = ",String.valueOf(getCurrentPage()));
                            //Log.i("nilai sisa",String.valueOf(totalqty/limit));
                            //Log.i("onLoadMore","dipanggil");
                            setCurrentPage(getCurrentPage()+1);
                            if (getApplicationContext()!=null) {
                                getData();
                            }
                            mRecyclerView.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.refreshComplete();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.loadMoreComplete();
                        }
                    }, 1000);
                }
                times ++;
            }
        });
        mAdapter = new MyAdapter(MainActivity.this,kt);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setRefreshing(true);
    }
    public int getCurrentPage() {
        return this.currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    void getData(){
        if (!isFinishing()) {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait....");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        try {
            JSONObject json = new JSONObject();
            JSONObject data = new JSONObject();
            try {
                int start = (getCurrentPage()) * this.limit;
                json = new JSONObject();
                json.put("action", "QueryKontak");
                json.put("method", "getResults");
                JSONArray arr2 = new JSONArray();
                data.put("start", start);
                data.put("limit", limit);
                arr2.put(data);
                json.put("data", arr2);
                Log.i("JSON nya",json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RP = new RequestPost("router.php",json,  getApplicationContext());
            RP.execPostCall(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    final String jsonData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject obj = new JSONObject(jsonData);
                                JSONObject result = obj.getJSONObject("result");
                                String hasil = result.getString("hasil");
                                totalqty = result.getInt("totalCount");
                                List<Kontak> kont = JSON.parseObject(hasil, new TypeReference<List<Kontak>>(){});
                                mAdapter.notifyDataSetChanged();
                                mRecyclerView.refreshComplete();
                                kt.addAll(kont);
                                if (pDialog != null) pDialog.dismiss();
                            } catch (JSONException e) {
                                if (pDialog != null) pDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (IOException e) {
            if (pDialog != null) pDialog.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.AddKontak:
                Intent i = new Intent(MainActivity.this,KontakActivity.class);
                i.putExtra("edit",false);
                startActivity(i);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    public void onRefresh(){
        kt.clear();
        setCurrentPage(0);
        if (getApplicationContext()!=null) {
            getData();
        }
        mAdapter.notifyDataSetChanged();
        mRecyclerView.refreshComplete();
    }
}


