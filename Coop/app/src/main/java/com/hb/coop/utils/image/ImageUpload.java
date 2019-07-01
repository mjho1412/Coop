package com.hb.coop.utils.image;

import com.hb.coop.data.api.response.ImageUploadResponse;
import io.reactivex.Observable;

import java.io.File;

/**
 * Created by buihai on 9/4/17.
 */

public interface ImageUpload {

    Observable<ImageUploadResponse> uploadAvatar(File file);

    Observable<ImageUploadResponse> uploadImage(File file);

    Observable<ImageUploadResponse> uploadGasolineImage(int storeId, File file);

    Observable<ImageUploadResponse> uploadMedicalImage(int medicalId, File file);

    Observable<ImageUploadResponse> uploadGasolineAccreditationImage(int storeId, File file);

    Observable<ImageUploadResponse> uploadMedicalAccreditationImage(int medicalId, File file);

}
