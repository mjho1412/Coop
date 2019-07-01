package com.hb.coop.utils.http.converter.gson;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.hb.coop.utils.Compression;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import timber.log.Timber;

import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

final class AppGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final MediaType MEDIA_TYPE = MediaType.get("application/json");

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    private Inflater decompresser = new Inflater();
    private byte[] output = new byte[100];

    AppGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {

        MediaType contentType = value.contentType();
        if (contentType.equals(MEDIA_TYPE)) {

            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                T result = adapter.read(jsonReader);
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw new JsonIOException("JSON document was not fully consumed.");
                }
                return result;
            } finally {
                value.close();
            }
        }

        T result;
        JsonReader jsonReader;

        try {
            byte[] buffer = value.bytes();
//            decompresser.setInput(buffer, 0, buffer.length);
//            int l = 0;
//            try {
//                output = new byte[buffer.length * 2];
//                l = decompresser.inflate(output);
//                decompresser.end();
//            } catch (DataFormatException e) {
//                try {
//                    throw new Exception("Unzip error");
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//            }
//
//            byte[] out = new byte[l];
//            System.arraycopy(output, 0, out, 0, l);
//            Timber.d("Length: %d", l);


            byte[] out;
            try {
                out = Compression.decompress(buffer);
            } catch (DataFormatException e) {
                throw new JsonIOException("JSON document was not fully consumed.");
            }

            String temp = new String(out, "utf-8");
            Timber.d("response: %s", temp);

            Reader reader = new InputStreamReader(new ByteArrayInputStream(out));
            jsonReader = gson.newJsonReader(reader);
            result = adapter.read(jsonReader);
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonIOException("JSON document was not fully consumed.");
            }
            return result;
        } finally {
            value.close();
        }

    }

    public byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;


            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        } catch (OutOfMemoryError error) {
            return null;
        }
    }
}
