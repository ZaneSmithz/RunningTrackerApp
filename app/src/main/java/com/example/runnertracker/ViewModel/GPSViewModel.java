package com.example.runnertracker.ViewModel;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.runnertracker.Model.GPSService;

public class GPSViewModel extends AndroidViewModel {

    public GPSViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<GPSService.MyBinder> mBinder = new MutableLiveData<>();
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            GPSService.MyBinder binder = (GPSService.MyBinder) iBinder;
            mBinder.postValue(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinder.postValue(null);
        }
    };

    public MutableLiveData<GPSService.MyBinder> getBinder() {
        return mBinder;
    }
    public ServiceConnection getServiceConnection()
    {
        return serviceConnection;
    }

}
