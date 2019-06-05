package com.geeklesson.rxjava;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button) Button button;

    AlertDialog.Builder builder;
    Boolean canceled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        button.setOnClickListener(view -> loadFileImitation(new Handler()));
    }

    private void loadFileImitation(Handler handler) {
        canceled = false;
        Disposable d = getCompletable(handler)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        if (canceled) {
                            showCancel();
                        } else{
                            showSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e.getLocalizedMessage());
                    }
                });
    }

    private Completable getCompletable(Handler handler) {
        return Completable.create(emitter -> {
            //do something aka load file
            handler.post(() -> showConfirmDialog(emitter));
            for (int i = 0; i < 1000000; i++) {
                new Random().nextDouble();
            }
            emitter.onComplete();
        });
    }

    private void showSuccess() {
        Toast.makeText(this, "File load success", Toast.LENGTH_SHORT).show();
    }

    private void showCancel() {
        Toast.makeText(this, "File load cancel", Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showConfirmDialog(CompletableEmitter emitter) {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel load file?");
        builder.setPositiveButton("YES", (dialog, which) -> {
            canceled = true;
            emitter.onComplete();
        });

        builder.setNegativeButton("NO", (dialog, which) -> {
        });

        builder.show();
    }
}
