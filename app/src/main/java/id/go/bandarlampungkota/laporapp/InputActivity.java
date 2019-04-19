package id.go.bandarlampungkota.laporapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.go.bandarlampungkota.laporapp.db.AppDatabase;
import id.go.bandarlampungkota.laporapp.http.ApiClient;
import id.go.bandarlampungkota.laporapp.http.ApiInterface;
import id.go.bandarlampungkota.laporapp.model.Lapor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputActivity extends AppCompatActivity {

    ImageView imgLapor;
    EditText txtKeterangan, txtLokasi;
    RadioGroup rbGroupp;
    RadioButton rbPileg, rbPilpres;
    Button btnSimpan, btnHapus, btnKirim;
    //foto
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;
    private Uri file;
    private String imagePath;
    Lapor lapor;
    private ProgressDialog mProgressDialog;
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = ApiClient.getClient().create(ApiInterface.class);
        setContentView(R.layout.activity_input);
        imgLapor = (ImageView) findViewById(R.id.img_lapor);
        txtKeterangan = (EditText) findViewById(R.id.txt_keterangan);
        txtLokasi = (EditText) findViewById(R.id.txt_lokasi);
        rbGroupp = (RadioGroup) findViewById(R.id.rb_group);
        rbPileg = (RadioButton) findViewById(R.id.rb_pileg);
        rbPilpres = (RadioButton) findViewById(R.id.rb_pilpres);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        btnHapus = (Button) findViewById(R.id.btn_hapus);
        btnKirim = (Button) findViewById(R.id.btn_kirim);

        Intent intent = getIntent();
        lapor = (Lapor) intent.getSerializableExtra("lapor");
        //Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (lapor != null) {
            if (lapor.path != null) {
                Glide.with(this).load(lapor.path).into(imgLapor);
            }
            txtLokasi.setText(lapor.lokasi);
            txtKeterangan.setText(lapor.keterangan);
            if (lapor.pil.equalsIgnoreCase("pilpres")) {
                rbPilpres.setChecked(true);
                rbPileg.setChecked(false);
            } else {
                rbPilpres.setChecked(false);
                rbPileg.setChecked(true);
            }
            imagePath = lapor.path;
            btnHapus.setVisibility(View.VISIBLE);
            btnKirim.setVisibility(View.VISIBLE);
            if(lapor.sent == 1){
                btnSimpan.setVisibility(View.GONE);
                btnHapus.setVisibility(View.GONE);
                btnKirim.setVisibility(View.GONE);
            }
        }

        imgLapor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDialog();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lapor == null) {
                    lapor = new Lapor();
                    lapor.sent = 0;
                    lapor.path = imagePath;
                    lapor.keterangan = txtKeterangan.getText().toString();
                    lapor.lokasi = txtLokasi.getText().toString();
                    RadioButton selected = (RadioButton) findViewById(rbGroupp.getCheckedRadioButtonId());
                    lapor.pil = selected.getText().toString();
                    lapor.waktu = new Date();
                    AppDatabase.getInstance(getApplicationContext()).laporDao().insert(lapor);
                } else {
                    lapor.sent = 0;
                    lapor.path = imagePath;
                    lapor.keterangan = txtKeterangan.getText().toString();
                    lapor.lokasi = txtLokasi.getText().toString();
                    RadioButton selected = (RadioButton) findViewById(rbGroupp.getCheckedRadioButtonId());
                    lapor.pil = selected.getText().toString();
                    lapor.waktu = new Date();
                    AppDatabase.getInstance(getApplicationContext()).laporDao().update(lapor);
                }

                Intent intent = new Intent(InputActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder =
                        new android.app.AlertDialog.Builder(InputActivity.this);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Hapus data?");
                builder.setPositiveButton("Hapus",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                AppDatabase.getInstance(getApplicationContext()).laporDao().delete(lapor);
                                Intent intent = new Intent(InputActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });

                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload(lapor);
            }
        });

    }

    ////////////////////////////////////////// camera/gallery //////////////////////////////////////////
    //foto
    private void chooseDialog() {
        CharSequence menu[] = new CharSequence[]{"Take From Galery", "Open Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a Picture");
        builder.setItems(menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    gallery();
                } else {
                    takePicture();
                }
            }
        });
        builder.show();

    }

    //camera
    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        //Toast.makeText(this,file.toString(),Toast.LENGTH_SHORT).show();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent, 100);
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    private void gallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }


    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("EditProfileActivity", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String saveImage(Bitmap image, String fileName) {
        String savedImagePath = null;
        String imageFileName = "JPEG_" + fileName + ".jpg";
        File storageDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                //perkecil
                image.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Add the image to the system gallery
            galleryAddPic(savedImagePath);
            //Toast.makeText(DetailEventActivity.this, "IMAGE SAVED", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    ////////////////////////////////////////// camera/gallery //////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(file).into(imgLapor);
                imagePath = file.getPath();
            }
        } else {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(data.getData()).into(imgLapor);
                imagePath = getRealPathFromURI(this, data.getData());
            }
        }
        Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show();
    }

    private void upload(final Lapor lapor) {
        File file = new File(lapor.path);//path image
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("berkas", file.getName(), reqFile);
        RequestBody lokasi = RequestBody.create(MediaType.parse("text/plain"), lapor.lokasi);
        RequestBody keterangan = RequestBody.create(MediaType.parse("text/plain"), lapor.keterangan);
        RequestBody waktu = RequestBody.create(MediaType.parse("text/plain"),
                new SimpleDateFormat("dd-mm-yyyy").format(new Date()));
        RequestBody pil = RequestBody.create(MediaType.parse("text/plain"), lapor.pil);
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "image");
        mProgressDialog = new ProgressDialog(InputActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Mengirim data...");
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        Call<String> call = api.upload(body, lokasi, keterangan, waktu, pil,type);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String>
                    response) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                String result = response.body();
                if (response.isSuccessful()) {
                    //simpan local db
                    lapor.sent = 1;
                    AppDatabase.getInstance(getApplicationContext()).laporDao().update(lapor);
                    Intent intent = new Intent(InputActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(InputActivity.this, "upload gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }
}
