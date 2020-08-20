package com.jackdonaldson.majorwork2019.fragment;

import com.jackdonaldson.majorwork2019.Notifications.MyResponse;
import com.jackdonaldson.majorwork2019.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA42TCO-Y:APA91bFmJOLsdJYVWUbsWoh1phYep1t_lt1kvXxHsHhtaTGDoF7spc5e4CMMn8T81jqMfot5WYxSi_In_IbsjTS1BujRgqkwIvqN_TsbQ8Gn-_tgaI7BYPP9hjAVj6Pm1kloGLsn8QqB"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
