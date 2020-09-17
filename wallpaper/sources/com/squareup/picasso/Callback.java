package com.squareup.picasso;

public interface Callback {
    void onError(Exception exc);

    void onSuccess();

    public static class EmptyCallback implements Callback {
        public void onSuccess() {
        }

        public void onError(Exception e) {
        }
    }
}
