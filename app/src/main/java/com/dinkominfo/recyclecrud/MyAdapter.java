package com.dinkominfo.recyclecrud;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.dinkominfo.recyclecrud.model.Kontak;
import com.dinkominfo.recyclecrud.util.RequestPost;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
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
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Kontak> kt = new ArrayList<>();
    private Typeface font;
    private Activity mActivity;
    ProgressDialog pDialog;
    private MyAdapter adapter;


    public MyAdapter(Activity mActivity, List<Kontak> kt) {
        this.kt = kt;
        this.mActivity=mActivity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        font = Typeface.createFromAsset(mActivity.getAssets(), "fontawesome-webfont.ttf" );
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.tvnama.setText(kt.get(position).getNama());
        //viewHolder.tvemail.setText(kt.get(position).getEmail());
        viewHolder.tvnotelp.setText(kt.get(position).getNotelp());
        //viewHolder.tvalamat.setText(kt.get(position).getAlamat());
        Picasso.with(viewHolder.imageView.getContext())
                .load(kt.get(position).getFoto())
                .resize(dp2px(220), 0)
                .into(viewHolder.imageView);
    }
    public int dp2px(int dp) {
        WindowManager wm = (WindowManager) mActivity.getBaseContext()
                .getSystemService(mActivity.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        display.getMetrics(displaymetrics);
        return (int) (dp * displaymetrics.density + 0.5f);
    }
    @Override
    public int getItemCount() {
        return kt.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvnama,tvemail,tvnotelp,tvalamat;
        ImageView imageView;
        public ViewHolder(View view){
            super(view);
            tvnama = (TextView) view.findViewById(R.id.nama);
            // tvemail = (TextView) view.findViewById(R.id.email);
             tvnotelp = (TextView) view.findViewById(R.id.notelp);
            // tvalamat = (TextView) view.findViewById(R.id.alamat);
            CardView cardView = (CardView) view.findViewById(R.id.cv);
            cardView.setOnClickListener(this);
            imageView = (ImageView) view.findViewById(R.id.iv);
            Button delete = (Button) view.findViewById(R.id.delete);
            delete.setTypeface(font);
            Button edit = (Button) view.findViewById(R.id.edit);
            edit.setTypeface(font);
            delete.setOnClickListener(this);
            edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Kontak item = kt.get(getAdapterPosition()-1);
            if (view.getId()==R.id.delete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage("Yakin mau dihapus?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteRecord(String.valueOf(item.getIdkontak()));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create();
                builder.show();
            }else if (view.getId()==R.id.edit){
                Intent i = new Intent(mActivity,KontakActivity.class);
                i.putExtra("edit",true);
                i.putExtra("idkontak",String.valueOf(item.getIdkontak()));
                i.putExtra("nama",item.getNama());
                i.putExtra("email",item.getEmail());
                i.putExtra("notelp",item.getNotelp());
                i.putExtra("alamat",item.getAlamat());
                i.putExtra("foto",item.getFoto());
                mActivity.startActivity(i);
                mActivity.finish();
            }else if (view.getId()==R.id.cv){
                Intent i = new Intent(mActivity,KontakView.class);
                i.putExtra("idkontak",String.valueOf(item.getIdkontak()));
                i.putExtra("nama",item.getNama());
                i.putExtra("email",item.getEmail());
                i.putExtra("notelp",item.getNotelp());
                i.putExtra("alamat",item.getAlamat());
                i.putExtra("foto",item.getFoto());
                mActivity.startActivity(i);
                mActivity.finish();
            }
        }
    }
    void deleteRecord(String id) {
        RequestPost RP;
        if (!mActivity.isFinishing()){
            pDialog = new ProgressDialog(mActivity);
            pDialog.setMessage("Please wait....");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        try {
            JSONObject json = new JSONObject();
            json.put("action", "QueryKontak");
            json.put("method", "deleteRecord");
            JSONObject data = new JSONObject();
            data.put("idkontak", id);
            JSONArray arr2 = new JSONArray();
            arr2.put(data);
            json.put("data", arr2);
            RP = new RequestPost("router.php", json, mActivity.getApplicationContext());
            RP.execPostCall(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (pDialog != null) pDialog.dismiss();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    if (response.isSuccessful()) {
                        final String jsonData = response.body().string();
                        try {
                            JSONObject obj = new JSONObject(jsonData);
                            JSONObject result = obj.getJSONObject("result");
                            int success = result.getInt("success");
                            if (success==1){
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((MainActivity) mActivity).onRefresh();
                                    }
                                });
                            }
                        }catch(Exception e){
                            if (pDialog != null) pDialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                }
            });
        } catch (Exception e) {
            if (pDialog != null) pDialog.dismiss();
            e.printStackTrace();
        }
    }
}
