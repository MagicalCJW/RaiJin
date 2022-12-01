package org.RaiJin.common.env;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
@Data
@Builder
public class EnvConfig {

    private String name;
    private boolean debug;
    private String externalApex;
    private String internalApex;
    private String scheme;

    private static Map<String,EnvConfig> map;

    static {
        map = new HashMap<>();
        EnvConfig envConfig = EnvConfig.builder().name(EnvConstant.ENV_DEV)
                .debug(true).externalApex("itachi-v2.local")
                .internalApex(EnvConstant.ENV_DEV)
                .scheme("http")
                .build();
        map.put(EnvConstant.ENV_DEV,envConfig);

        envConfig = EnvConfig.builder().name(EnvConstant.ENV_TEST)
                .debug(true).externalApex("itachi-v2.local")
                .internalApex(EnvConstant.ENV_DEV)
                .scheme("http")
                .build();
        map.put(EnvConstant.ENV_TEST, envConfig);

        envConfig = EnvConfig.builder().name(EnvConstant.ENV_UAT)
                .debug(true).externalApex("itachi-uat.local")
                .internalApex(EnvConstant.ENV_UAT)
                .scheme("http")
                .build();
        map.put(EnvConstant.ENV_UAT, envConfig);

        envConfig = EnvConfig.builder().name(EnvConstant.ENV_PROD)
                .debug(false).externalApex("itachi.com")
                .internalApex(EnvConstant.ENV_PROD)
                .scheme("https")
                .build();
        map.put(EnvConstant.ENV_PROD, envConfig);
    }

    public static EnvConfig getEnvConfig(String env) {
        EnvConfig envConfig = map.get(env);
        if(envConfig==null) {
            envConfig = map.get(EnvConstant.ENV_DEV);
        }
        return envConfig;
    }
}
