package com.example.imagepicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private CircleImageView ProfileImage;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;

    // Request
    Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Token a325e14e80c0de70a1469d6bd8bf62ef4babd53a")
                                .build();

                        return chain.proceed(newRequest);
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.219:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        //////////////////////////////////////////////////////////////

        ProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent();
                gallery.setType("*/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Sellect Picture"), PICK_IMAGE);
            }
        });
    }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();

                Toast.makeText(MainActivity.this, "fileType", Toast.LENGTH_LONG).show();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ProfileImage.setImageBitmap(bitmap);
                    UploadFunction(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // File Upload Function

    private void UploadFunction(Intent data) {
//        Call<UploadFile> call = jsonPlaceHolderApi.getPosts();
//        imageUri = data.getData();
        String MyFile = getRealPathFromURI(getApplicationContext(),data.getData());
        File file = new File(MyFile);
        Toast.makeText(MainActivity.this, "Size---"+Integer.parseInt(String.valueOf(file.length()/1024)), Toast.LENGTH_LONG).show();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipartBody =MultipartBody.Part.createFormData("document",file.getName(),requestFile);


        Call<UploadFile> call = jsonPlaceHolderApi.createPost(multipartBody);

        call.enqueue(new Callback<UploadFile>() {
            @Override
            public void onResponse(Call<UploadFile> call, Response<UploadFile> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Error---"+response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                UploadFile postResponse = response.body();

                Toast.makeText(MainActivity.this, "Success---"+postResponse.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<UploadFile> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail---"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // Extra
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    }
