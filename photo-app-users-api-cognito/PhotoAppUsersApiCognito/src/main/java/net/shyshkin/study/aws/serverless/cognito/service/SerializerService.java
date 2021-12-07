package net.shyshkin.study.aws.serverless.cognito.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializerService {

    private static final Gson INSTANCE = new GsonBuilder().serializeNulls().create();

    public static Gson instance() {
        return INSTANCE;
    }
}
