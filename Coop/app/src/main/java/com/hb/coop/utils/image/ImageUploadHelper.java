package com.hb.coop.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.hb.coop.data.api.response.ImageUploadResponse;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.http.*;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by buihai on 9/4/17.
 */

public class ImageUploadHelper implements ImageUpload {

    private static final int PERCENT_QUALITY = 100;

    private Service mService;

    public ImageUploadHelper(Retrofit retrofit) {
        mService = retrofit.create(Service.class);
    }

    private Observable<RequestBody> createUploadFunction(File file) {
        return Observable.create(emitter -> {
            try {
                Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, PERCENT_QUALITY, stream);
                byte[] buffer = stream.toByteArray();
                RequestBody requestBody = RequestBody
                        .create(MediaType.parse("application/octet-stream"), buffer);
                emitter.onNext(requestBody);
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        });
    }

    @Override
    public Observable<ImageUploadResponse> uploadAvatar(File file) {

        String fileNames = file.getName();
        Observable<RequestBody> func = createUploadFunction(file);
        return func.flatMap(rb -> {
            String header = "name=file; filename=" + fileNames + ".jpg";
            return mService.uploadAvatar(header, rb);
        });
    }

    @Override
    public Observable<ImageUploadResponse> uploadImage(File file) {
        String fileNames = file.getName();
        Observable<RequestBody> func = createUploadFunction(file);
        return func.flatMap(rb -> {
            String header = "name=file; filename=" + fileNames + ".jpg";
            return mService.uploadImage(header, rb);
        });
    }

    @Override
    public Observable<ImageUploadResponse> uploadGasolineImage(int storeId, File file) {
        String fileNames = file.getName();
        Observable<RequestBody> func = createUploadFunction(file);
        return func.flatMap(rb -> {
            String header = "name=file; filename=" + fileNames + ".jpg";
            return mService.uploadImageGasoline(header, storeId, rb);
        });
    }

    @Override
    public Observable<ImageUploadResponse> uploadMedicalImage(int medicalId, File file) {
        String fileNames = file.getName();
        Observable<RequestBody> func = createUploadFunction(file);
        return func.flatMap(rb -> {
            String header = "name=file; filename=" + fileNames + ".jpg";
            return mService.uploadImageMedical(header, medicalId, rb);
        });
    }

    @Override
    public Observable<ImageUploadResponse> uploadGasolineAccreditationImage(int storeId, File file) {
        String fileNames = file.getName();
        Observable<RequestBody> func = createUploadFunction(file);
        return func.flatMap(rb -> {
            String header = "name=file; filename=" + fileNames + ".jpg";
            return mService.uploadImageGasolineAccreditation(header, storeId, rb);
        });
    }

    @Override
    public Observable<ImageUploadResponse> uploadMedicalAccreditationImage(int medicalId, File file) {
        String fileNames = file.getName();
        Observable<RequestBody> func = createUploadFunction(file);
        return func.flatMap(rb -> {
            String header = "name=file; filename=" + fileNames + ".jpg";
            return mService.uploadImageMedicalAccreditation(header, medicalId, rb);
        });
    }

    interface Service {
        @Headers({"Content-Type: image/png"})
        @PUT("v1/user/query/info/update_avatar")
        Observable<ImageUploadResponse> uploadAvatar(
                @Header(value = "Content-Disposition") String contentDisposition,
                @Body RequestBody photo);

        @Headers({"Content-Type: image/png"})
        @PUT("v1/user/query/upload_image")
        Observable<ImageUploadResponse> uploadImage(
                @Header(value = "Content-Disposition") String contentDisposition,
                @Body RequestBody photo);

        @Headers({"Content-Type: image/png"})
        @PUT("v1/user/query/gasoline_store/{id}/upload_image")
        Observable<ImageUploadResponse> uploadImageGasoline(
                @Header(value = "Content-Disposition") String contentDisposition,
                @Path("id") int storeId,
                @Body RequestBody photo);

        @Headers({"Content-Type: image/png"})
        @PUT("v1/user/query/medical/{id}/upload_image")
        Observable<ImageUploadResponse> uploadImageMedical(
                @Header(value = "Content-Disposition") String contentDisposition,
                @Path("id") int medicalId,
                @Body RequestBody photo);

        @Headers({"Content-Type: image/png"})
        @PUT("v1/user/query/gasoline_store_accreditation/{id}/upload_image")
        Observable<ImageUploadResponse> uploadImageGasolineAccreditation(
                @Header(value = "Content-Disposition") String contentDisposition,
                @Path("id") int storeId,
                @Body RequestBody photo);

        @Headers({"Content-Type: image/png"})
        @PUT("v1/user/query/medical_accreditation/{id}/upload_image")
        Observable<ImageUploadResponse> uploadImageMedicalAccreditation(
                @Header(value = "Content-Disposition") String contentDisposition,
                @Path("id") int medicalId,
                @Body RequestBody photo);
    }
}
