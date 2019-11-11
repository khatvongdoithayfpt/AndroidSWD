package com.example.callback;

import java.io.File;

public interface UploadImageRemoteCallback {
    void uploadDataToRemote(int action, File file);
}
