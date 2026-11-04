package com.neosantara.prospek.ui;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neosantara.prospek.data.ProspectRepository;
import com.neosantara.prospek.data.SessionManager;
import com.neosantara.prospek.model.LoginResponse;
import com.neosantara.prospek.model.Prospect;
import com.neosantara.prospek.model.ProspectListResponse;
import com.neosantara.prospek.model.ProspectPayload;
import com.neosantara.prospek.model.ProspectResponse;
import com.neosantara.prospek.model.ProspectUpdatePayload;

import java.util.Collections;
import java.util.List;

public class ProspectViewModel extends AndroidViewModel {
    private final ProspectRepository repository;
    private final SessionManager sessionManager;
    private final MutableLiveData<List<Prospect>> prospects = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Prospect> selectedProspect = new MutableLiveData<>();
    private final MutableLiveData<Boolean> authenticated = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public ProspectViewModel(@NonNull Application application) {
        super(application);
        repository = new ProspectRepository(application);
        sessionManager = new SessionManager(application);
        authenticated.setValue(!TextUtils.isEmpty(sessionManager.getToken()));

        if (Boolean.TRUE.equals(authenticated.getValue())) {
            loadProspects();
        }
    }

    public LiveData<List<Prospect>> getProspects() {
        return prospects;
    }

    public LiveData<Prospect> getSelectedProspect() {
        return selectedProspect;
    }

    public LiveData<Boolean> getAuthenticated() {
        return authenticated;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void login(String email, String password) {
        loading.setValue(true);
        repository.login(email, password, new ProspectRepository.ResultCallback<>() {
            @Override
            public void onSuccess(LoginResponse result) {
                loading.setValue(false);

                if (TextUtils.isEmpty(result.token)) {
                    message.setValue("Token tidak ditemukan pada respons server.");
                    return;
                }

                sessionManager.saveToken(result.token);
                authenticated.setValue(true);
                message.setValue("Login berhasil.");
                loadProspects();
            }

            @Override
            public void onError(String error) {
                loading.setValue(false);
                message.setValue(error);
            }
        });
    }

    public void loadProspects() {
        loading.setValue(true);
        repository.getProspects(new ProspectRepository.ResultCallback<>() {
            @Override
            public void onSuccess(ProspectListResponse result) {
                loading.setValue(false);
                prospects.setValue(result.data == null ? Collections.emptyList() : result.data);
            }

            @Override
            public void onError(String error) {
                loading.setValue(false);
                message.setValue(error);
            }
        });
    }

    public void loadProspect(long prospectId) {
        loading.setValue(true);
        repository.getProspect(prospectId, new ProspectRepository.ResultCallback<>() {
            @Override
            public void onSuccess(ProspectResponse result) {
                loading.setValue(false);

                if (result.data == null) {
                    message.setValue("Detail prospek tidak ditemukan pada respons server.");
                    return;
                }

                selectedProspect.setValue(result.data);
            }

            @Override
            public void onError(String error) {
                loading.setValue(false);
                message.setValue(error);
            }
        });
    }

    public void clearSelectedProspect() {
        selectedProspect.setValue(null);
    }

    public void createProspect(ProspectPayload payload) {
        loading.setValue(true);
        repository.createProspect(payload, new ProspectRepository.ResultCallback<>() {
            @Override
            public void onSuccess(ProspectResponse result) {
                loading.setValue(false);
                message.setValue("Prospek berhasil ditambahkan.");
                loadProspects();
            }

            @Override
            public void onError(String error) {
                loading.setValue(false);
                message.setValue(error);
            }
        });
    }

    public void updateProspect(long prospectId, ProspectUpdatePayload payload) {
        loading.setValue(true);
        repository.updateProspect(prospectId, payload, new ProspectRepository.ResultCallback<>() {
            @Override
            public void onSuccess(ProspectResponse result) {
                loading.setValue(false);
                message.setValue("Prospek berhasil diperbarui.");
                loadProspects();
            }

            @Override
            public void onError(String error) {
                loading.setValue(false);
                message.setValue(error);
            }
        });
    }

    public void deleteProspect(long prospectId) {
        loading.setValue(true);
        repository.deleteProspect(prospectId, new ProspectRepository.ResultCallback<>() {
            @Override
            public void onSuccess(Void result) {
                loading.setValue(false);
                message.setValue("Prospek berhasil dihapus.");
                loadProspects();
            }

            @Override
            public void onError(String error) {
                loading.setValue(false);
                message.setValue(error);
            }
        });
    }

    public void logout() {
        loading.setValue(true);
        repository.logout(new ProspectRepository.ResultCallback<>() {
            @Override
            public void onSuccess(Void result) {
                finishLogout();
            }

            @Override
            public void onError(String error) {
                loading.setValue(false);
                message.setValue(error);
            }
        });
    }

    private void finishLogout() {
        sessionManager.clearToken();
        prospects.setValue(Collections.emptyList());
        authenticated.setValue(false);
        loading.setValue(false);
        message.setValue("Logout berhasil.");
    }
}
