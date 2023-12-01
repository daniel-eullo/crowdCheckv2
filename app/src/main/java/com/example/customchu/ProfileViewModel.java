package com.example.customchu;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {
    private FirebaseUser user = null;
    private Profile profile = null;

    public ProfileViewModel() {

    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public FirebaseUser getUser() {
        return user;
    }
    public Profile getProfile() {
        return profile;
    }
}
