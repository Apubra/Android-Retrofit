package com.example.imagepicker;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface JsonPlaceHolderApi {
//    @Multipart
//    @POST("Test")
//    Call<ResponseBody> postUploadFile(
//            @Part("document") ResponseBody document,
//            @Part MultipartBody.Part photo
//            );

    @GET("Test")
    Call<UploadFile> getPosts();

    @Multipart
    @POST("Test")
    Call<UploadFile> createPost(@Part MultipartBody.Part file);
}
