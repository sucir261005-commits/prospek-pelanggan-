package com.neosantara.prospek.data;

import com.neosantara.prospek.model.LoginRequest;
import com.neosantara.prospek.model.LoginResponse;
import com.neosantara.prospek.model.ProspectListResponse;
import com.neosantara.prospek.model.ProspectPayload;
import com.neosantara.prospek.model.ProspectResponse;
import com.neosantara.prospek.model.ProspectUpdatePayload;

import retrofit2.http.DELETE;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("logout")
    Call<Void> logout();

    @GET("prospects")
    Call<ProspectListResponse> getProspects();

    @GET("prospects/{id}")
    Call<ProspectResponse> getProspect(@Path("id") long prospectId);

    @POST("prospects")
    Call<ProspectResponse> createProspect(@Body ProspectPayload payload);

    @PUT("prospects/{id}")
    Call<ProspectResponse> updateProspect(@Path("id") long prospectId, @Body ProspectUpdatePayload payload);

    @DELETE("prospects/{id}")
    Call<Void> deleteProspect(@Path("id") long prospectId);
}
