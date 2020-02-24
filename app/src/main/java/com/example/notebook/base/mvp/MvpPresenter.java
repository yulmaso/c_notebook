package com.example.notebook.base.mvp;

public interface MvpPresenter <V extends MvpView> {

    void attachView(V mvpView);

    void viewIsReady();

    void detachView();

    void destroy();

}
