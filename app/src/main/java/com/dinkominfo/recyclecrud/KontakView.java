package com.dinkominfo.recyclecrud;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class KontakView extends AppCompatActivity {
    TextView tvnama,tvemail,tvnotelp,tvalamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontak_view);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CRUD");
        getSupportActionBar().setSubtitle("Training dinkominfo Surabaya");
        tvnama = (TextView) findViewById(R.id.nama);
        tvemail = (TextView) findViewById(R.id.email);
        tvnotelp = (TextView) findViewById(R.id.notelp);
        tvalamat = (TextView) findViewById(R.id.alamat);
        CardView cardView = (CardView) findViewById(R.id.cv);
        Intent i = getIntent();
        tvnama.setText(i.getExtras().getString("nama"));
        tvemail.setText(i.getExtras().getString("email"));
        tvnotelp.setText(i.getExtras().getString("notelp"));
        tvalamat.setText(i.getExtras().getString("alamat"));
        ImageView imageView = (ImageView) findViewById(R.id.iv);
        Picasso.with(imageView.getContext())
                .load(i.getExtras().getString("foto"))
                .resize(dp2px(220), 0)
                .into(imageView);
    }
    public int dp2px(int dp) {
        WindowManager wm = (WindowManager) getBaseContext()
                .getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        display.getMetrics(displaymetrics);
        return (int) (dp * displaymetrics.density + 0.5f);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(KontakView.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
