package org.RaiJin.core.mappings;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.exception.RaiJinException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MappingValidator {

    public void validate(List<MappingProperties> mappings) {
        if(!CollectionUtils.isEmpty(mappings)) {
            mappings.forEach(this::correctMapping);
            int numberOfNames = mappings.stream().map(MappingProperties::getName).collect(Collectors.toSet()).size();
            if(numberOfNames < mappings.size()) {
                throw new RaiJinException("Duplicated route names in mappings");
            }
            int numberOfHosts = mappings.stream().map(MappingProperties::getHost).collect(Collectors.toSet()).size();
            if(numberOfHosts<mappings.size()) {
                throw new RaiJinException("Duplicated source hosts in mappings");
            }
            mappings.sort((m1,m2)->m2.getHost().compareTo(m1.getHost()));
        }
    }

    protected void correctMapping(MappingProperties mapping) {
        this.validateName(mapping);
        this.validateDestinations(mapping);
        this.validateHost(mapping);
        this.validateTimeOut(mapping);
    }

    protected void validateName(MappingProperties mapping) {
        if(StringUtils.isEmpty(mapping.getName())) {
            throw new RaiJinException("Empty name for mapping" + mapping);
        }
    }

    protected void validateDestinations(MappingProperties mapping) {
        if(CollectionUtils.isEmpty(mapping.getDestinations())) {
            throw new RaiJinException("No destination for mapping" + mapping);
        }

        List<String> correctedHosts = new ArrayList<>(mapping.getDestinations().size());
        mapping.getDestinations().forEach(des -> {
            if(StringUtils.isEmpty(des)) {
                throw new RaiJinException("Empty destination for mapping" + mapping);
            }
            if(!des.matches(".+://.+")) {
                des = "http://" + des;
            }
            des = StringUtils.removeEnd(des, "/");
            correctedHosts.add(des);
        });
        mapping.setDestinations(correctedHosts);
    }

    protected void validateHost(MappingProperties mapping) {
        if(StringUtils.isBlank(mapping.getHost())) {
            throw new RaiJinException("No Source host for mapping"+mapping);
        }
    }

    protected void validateTimeOut(MappingProperties mapping) {
        int connectTimeOut = mapping.getTimeout().getRead();
        if(connectTimeOut < 0) {
            throw new RaiJinException("Invalid connect timeout value:" + connectTimeOut);
        }
        int readTimeOut = mapping.getTimeout().getRead();
        if(readTimeOut < 0) {
            throw new RaiJinException("Invalid read timeout value"+ readTimeOut);
        }
    }
}
