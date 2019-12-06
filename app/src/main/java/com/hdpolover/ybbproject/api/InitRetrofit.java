package com.hdpolover.ybbproject.api;

import android.util.Log;

import com.hdpolover.ybbproject.models.WordPressPostModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InitRetrofit {

    //API
    private Retrofit retrofit;
    private ApiInterface apiInterface;

    //Interface
    private OnRetrofitSuccess onRetrofitSuccess;

    public InitRetrofit() {
        retrofit = ApiClient.getRetrofit();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    public void getInfoPost() {
        apiInterface.getInfoPost().enqueue(new Callback<ArrayList<WordPressPostModel>>() {
            @Override
            public void onResponse(Call<ArrayList<WordPressPostModel>> call, Response<ArrayList<WordPressPostModel>> response) {
                ArrayList<WordPressPostModel> list = new ArrayList<>();
                if(response.code() == 200) {
                    list.addAll(response.body());
                }
                onRetrofitSuccess.onSuccessGetData(list);
                Log.e("success", response.toString());
            }

            @Override
            public void onFailure(Call<ArrayList<WordPressPostModel>> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
            }
        });
    }

    public void getBlogPost() {
        apiInterface.getBlogPost().enqueue(new Callback<ArrayList<WordPressPostModel>>() {
            @Override
            public void onResponse(Call<ArrayList<WordPressPostModel>> call, Response<ArrayList<WordPressPostModel>> response) {
                ArrayList<WordPressPostModel> list = new ArrayList<>();
                if(response.code() == 200) {
                    list.addAll(response.body());
                }
                onRetrofitSuccess.onSuccessGetData(list);
                Log.e("success", response.toString());
            }

            @Override
            public void onFailure(Call<ArrayList<WordPressPostModel>> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
            }
        });
    }

    //Interface
    public interface OnRetrofitSuccess {
        void onSuccessGetData(ArrayList arrayList);
    }

    public void setOnRetrofitSuccess(OnRetrofitSuccess onRetrofitSuccess) {
        this.onRetrofitSuccess = onRetrofitSuccess;
    }
}
