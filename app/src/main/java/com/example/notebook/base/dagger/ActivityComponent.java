package com.example.notebook.base.dagger;

public interface ActivityComponent<A> {
    void inject(A activity);
}
