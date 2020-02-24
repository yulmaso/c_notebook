package com.example.notebook.base.mvp;

public abstract class BasePresenter <V extends MvpView> implements MvpPresenter<V> {

    private V view;

    @Override
    public void attachView(V mvpView) {
        view = mvpView;
    }

    @Override
    public void detachView() {
        view = null;
    }

    public V getView() {
        return view;
    }

    public boolean isViewAttached() {
        return view != null;
    }

    @Override
    public void destroy() {

    }
}
