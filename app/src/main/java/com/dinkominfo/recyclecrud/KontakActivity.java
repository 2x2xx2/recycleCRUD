package com.dinkominfo.recyclecrud;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dinkominfo.recyclecrud.model.Kontak;
import com.dinkominfo.recyclecrud.util.RequestPost;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class KontakActivity extends AppCompatActivity implements View.OnClickListener {
    private Boolean edit;
    private String idkontak="";
    private ProgressDialog pDialog;
    private RequestPost RP;
    private EditText nama,email,notelp,alamat;
    private ImageView imageView ;
    private static final int PREVIEW_REQUEST_CODE = 1;
    private static final int SAVE_REQUEST_CODE = 2;
    private String photoPath;
    private File photoFile=null;
    private Uri fileUri;
    private String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontak);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CRUD");
        getSupportActionBar().setSubtitle("Training dinkominfo Surabaya");
        nama = (EditText) findViewById(R.id.nama);
        email = (EditText) findViewById(R.id.email);
        notelp = (EditText) findViewById(R.id.notelp);
        alamat = (EditText) findViewById(R.id.alamat);
        imageView = (ImageView) findViewById(R.id.foto);
        imageView.setOnClickListener(this);
        Button btn = (Button) findViewById(R.id.simpan);
        btn.setOnClickListener(this);
        Intent i = getIntent();
        edit = i.getExtras().getBoolean("edit",false);
        if (edit){
            idkontak=i.getExtras().getString("idkontak");
            nama.setText(i.getExtras().getString("nama"));
            email.setText(i.getExtras().getString("email"));
            notelp.setText(i.getExtras().getString("notelp"));
            alamat.setText(i.getExtras().getString("alamat"));
            btn.setText("Simpan");
            Picasso.with(imageView.getContext())
                    .load(i.getExtras().getString("foto"))
                    .into(imageView);
        }else{
            btn.setText("Tambah");
            Picasso.with(imageView.getContext())
                    .load(R.drawable.image_holder)
                    .into(imageView);
        }

    }

    void update(){
        if (!isFinishing()) {
            pDialog = new ProgressDialog(KontakActivity.this);
            pDialog.setMessage("Please wait....");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        JSONObject json = new JSONObject();
        JSONObject filenya = new JSONObject();
        try {

            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("action", "QueryKontak");
                if(edit) {
                    jsonParam.put("method", "updateRecord");
                }else{
                    jsonParam.put("method", "createRecord");
                }

                JSONObject data = new JSONObject();
                if (edit){
                    data.put("idkontak", idkontak);
                }
                if (!(photoPath ==null) && !photoPath.equalsIgnoreCase("")){
                    filenya.put("namafoto", photoPath);
                    data.put("foto", file+".jpg");
                }
                data.put("nama", nama.getText().toString());
                data.put("email", email.getText().toString());
                data.put("notelp", notelp.getText().toString());
                data.put("alamat", alamat.getText().toString());
                JSONArray arr2 = new JSONArray();
                arr2.put(data);
                jsonParam.put("data", arr2);
                json.put("param",jsonParam);
                Log.i("JSON nya: ",json.toString());
                Log.i("Filenya: ",filenya.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RP = new RequestPost("router.php",json,filenya,  getApplicationContext());
            RP.execPostuploadCall(new Callback() {
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
                                int success = result.getInt("success");
                                if (success==1){
                                    Intent i = new Intent(KontakActivity.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }else{
                                    Toast t=Toast.makeText(getApplicationContext(),"Gagal menyimpan",Toast.LENGTH_SHORT);
                                    t.show();
                                }
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(KontakActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.simpan){
            update();
        }else if(view.getId()==R.id.foto){
            takePhoto();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        if (requestCode == SAVE_REQUEST_CODE &&resultCode == RESULT_OK) {
            if (photoFile != null) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = fileUri;
                intent.setData(contentUri);
                this.sendBroadcast(intent);
                Toast toast = Toast.makeText(getApplicationContext(),"File tersimpan di "+photoPath,Toast.LENGTH_SHORT);
                toast.show();
                Log.i("masuk sini",photoPath);
                Picasso.with(imageView.getContext())
                        .load(fileUri)
                        .into(imageView);
            }
        }
    }
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                photoFile = filename();
                fileUri = Uri.fromFile(photoFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(),"No SD card",Toast.LENGTH_SHORT);
                toast.show();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent,SAVE_REQUEST_CODE);
            }
        }
    }

    private File filename() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        file = "training-dinkominfo" +"_" + time;
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(file, ".jpg", dir);
        photoPath = image.getAbsolutePath();
        return image;
    }
}
