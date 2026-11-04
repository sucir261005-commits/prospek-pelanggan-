package com.neosantara.prospek.data;

import android.content.Context;

import com.neosantara.prospek.model.LoginRequest;
import com.neosantara.prospek.model.LoginResponse;
import com.neosantara.prospek.model.ProspectListResponse;
import com.neosantara.prospek.model.ProspectPayload;
import com.neosantara.prospek.model.ProspectResponse;
import com.neosantara.prospek.model.ProspectUpdatePayload;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProspectRepository {
    private final ApiService apiService;

    public ProspectRepository(Context context) {
        apiService = ApiClient.getService(context);
    }

    public void login(String email, String password, ResultCallback<LoginResponse> callback) {
        enqueue(apiService.login(new LoginRequest(email, password, "Android UTS")), callback);
    }

    public void getProspects(ResultCallback<ProspectListResponse> callback) {
        enqueue(apiService.getProspects(), callback);
    }

    public void getProspect(long prospectId, ResultCallback<ProspectResponse> callback) {
        enqueue(apiService.getProspect(prospectId), callback);
    }

    public void createProspect(ProspectPayload payload, ResultCallback<ProspectResponse> callback) {
        enqueue(apiService.createProspect(payload), callback);
    }

    public void updateProspect(long prospectId, ProspectUpdatePayload payload, ResultCallback<ProspectResponse> callback) {
        enqueue(apiService.updateProspect(prospectId, payload), callback);
    }

    public void deleteProspect(long prospectId, ResultCallback<Void> callback) {
        enqueueWithoutBody(apiService.deleteProspect(prospectId), callback);
    }

    public void logout(ResultCallback<Void> callback) {
        enqueueWithoutBody(apiService.logout(), callback);
    }

    private void enqueueWithoutBody(Call<Void> call, ResultCallback<Void> callback) {
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable error) {
                callback.onError(networkError(error));
            }
        });
    }

    private <T> void enqueue(Call<T> call, ResultCallback<T> callback) {
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else if (response.isSuccessful()) {
                    callback.onError("Server mengembalikan respons kosong.");
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable error) {
                callback.onError(networkError(error));
            }
        });
    }

    private String networkError(Throwable error) {
        return error.getMessage() == null
            ? "Tidak dapat menghubungi server API."
            : error.getMessage();
    }

    private String errorMessage(Response<?> response) {
        if (response.errorBody() != null) {
            try {
                JSONObject body = new JSONObject(response.errorBody().string());
                String message = body.optString("message");

                if (!message.isBlank()) {
                    return message;
                }
            } catch (IOException | JSONException error) {
                return "Permintaan gagal (" + response.code() + "): " + response.message();
            }
        }

        return "Permintaan gagal (" + response.code() + "): " + response.message();
    }

    public interface ResultCallback<T> {
        void onSuccess(T result);

        void onError(String message);
    }
}
