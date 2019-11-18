package cl.udelvd.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class SingleLiveEvent<T> extends MutableLiveData<T> {

    private AtomicBoolean status = new AtomicBoolean(false);

    public void observe(@NonNull LifecycleOwner owner,
                        @NonNull final Observer<? super T> observer) {

        if (hasActiveObservers()) {
            Log.w("SINGLE_LIVE_DATA", "Multiple observers registered but only one will be " +
                    "notified of changes.");
        }

        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                if (status.compareAndSet(true, false)) {
                    observer.onChanged(t);
                }
            }
        });
    }

    public void setValue(T t) {
        status.set(true);
        super.setValue(t);
    }

    public void call() {
        setValue(null);
    }


}
