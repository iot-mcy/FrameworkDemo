package com.mcy.framework.rxjava;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import io.reactivex.disposables.Disposable;

import static android.os.SystemClock.elapsedRealtime;


/**
 * 作者 mcy
 * 日期 2018/8/7 16:24
 */
public final class Disposables {

    private final Collection<Disposable> disposables = new ArrayList<>();

    private void clearDisposed() {
        for (final Iterator<Disposable> it = disposables.iterator(); it.hasNext(); ) {
            final Disposable disposable = it.next();
            if (disposable == null || disposable.isDisposed()) {
                it.remove();
            }
        }
    }

    private transient long lastCleared = elapsedRealtime();

    private void clearDisposed(final long currentTime) {
        final long interval = currentTime - lastCleared;
        if (interval >= 3000) {
            lastCleared = currentTime;
            clearDisposed();
        }
    }

    public void add(@NonNull final Disposable disposable) {
        clearDisposed(elapsedRealtime());
        disposables.add(disposable);
    }

    public void disposeAll() {
        for (final Disposable disposable : disposables) {
            if (disposable == null || disposable.isDisposed()) {
                continue;
            }
            disposable.dispose();
        }
        disposables.clear();
    }
}
