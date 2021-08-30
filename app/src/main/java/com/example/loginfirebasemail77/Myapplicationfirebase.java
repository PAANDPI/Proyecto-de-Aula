package com.example.loginfirebasemail77;

import com.google.firebase.database.FirebaseDatabase;

public class Myapplicationfirebase extends  android.app.Application{
@Override
    public void onCreate() {
    super.onCreate();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
}
}
