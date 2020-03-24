package com.defalt.lelangonline.ui.items.add;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.defalt.lelangonline.R;
import com.defalt.lelangonline.data.JSONParser;
import com.defalt.lelangonline.ui.SharedFunctions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemsAddActivity extends AppCompatActivity {

    private static final int CAMERA_PIC_REQUEST = 22;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        mContext = getApplicationContext();

        EditText nameEditText = findViewById(R.id.name);
        EditText descEditText = findViewById(R.id.description);
        EditText categoryEditText = findViewById(R.id.category);
        EditText valueEditText = findViewById(R.id.value);
        ImageView thumbImageView = findViewById(R.id.thumbnail);
        thumbImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Ambil Foto", "Pilih dari Galeri"};
                AlertDialog.Builder imgDialog = new AlertDialog.Builder(ItemsAddActivity.this);
                imgDialog.setTitle(R.string.item_post_img_hint);
                imgDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Ambil Foto")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, 1);
                        } else if (options[item].equals("Pilih dari Galeri")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        }
                    }
                });
                imgDialog.show();
            }
        });

        //SharedFunctions.loseFocusListener(nameEditText, mContext);
        //SharedFunctions.loseFocusListener(descEditText, mContext);
        /*final EditText categoryEditText = findViewById(R.id.category);
        final EditText priceInitEditText = findViewById(R.id.price_init);
        final EditText priceStartEditText = findViewById(R.id.price_start);
        final EditText priceEndEditText = findViewById(R.id.price_limit);
        final Button addButton = findViewById(R.id.add_item);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                View focused = getCurrentFocus();
                if (focused != null) {
                    focused.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
                }
                new AddItem().execute(
                        nameEditText.getText().toString(),
                        descEditText.getText().toString(),
                        categoryEditText.getText().toString(),
                        priceInitEditText.getText().toString(),
                        priceStartEditText.getText().toString(),
                        priceEndEditText.getText().toString()
                );

            }
        });*/
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    class AddItem extends AsyncTask<String, String, String> {

        private Boolean isSuccess = false;
        private String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nama_barang", args[0]));
            params.add(new BasicNameValuePair("deskripsi", args[1]));
            params.add(new BasicNameValuePair("kategori", args[2]));
            params.add(new BasicNameValuePair("harga_awal", args[3]));
            params.add(new BasicNameValuePair("harga_start", args[4]));
            params.add(new BasicNameValuePair("harga_limit", args[5]));

            String url = "https://dev.projectlab.co.id/mit/1317003/create_item.php";
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            if (json != null) {
                try {
                    int success = json.getInt("success");
                    message = json.getString("message");

                    if (success == 1) {
                        isSuccess = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            Toast.makeText(ItemsAddActivity.this, message, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

}
