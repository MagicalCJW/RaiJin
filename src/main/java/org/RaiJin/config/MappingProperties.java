package org.RaiJin.config;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;


@Data
public class MappingProperties {

    private String name;

    private String host = "";

    private List<String> destinations = new ArrayList<>();

    private TimeOutProperties timeout = new TimeOutProperties();

    private Map<String, Object> customConfiguration = new HashMap<>();

    public MappingProperties copy() {
        MappingProperties properties = new MappingProperties();
        properties.setName(name);
        properties.setHost(host);
        properties.setTimeout(timeout);
        properties.setDestinations(destinations==null?null:new ArrayList<>(destinations));
        properties.setCustomConfiguration(customConfiguration==null?null:new HashMap<>(customConfiguration));

        return properties;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, NO_CLASS_NAME_STYLE)
                .append("name", name)
                .append("host", host)
                .append("destinations", destinations)
                .append("timeout", timeout)
                .append("customConfiguration", customConfiguration)
                .toString();
    }

    public static class TimeOutProperties {
        private int connect = 2000;
        private int read = 20000;

        public int getConnect() {
            return connect;
        }

        public void setConnect(int connect) {
            this.connect = connect;
        }

        public int getRead() {
            return read;
        }

        public void setRead(int read) {
            this.read = read;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                    .append("connect", connect)
                    .append("read", read)
                    .toString();
        }
    }
}


