package org.RaiJin.core.balancer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public String chooseDestination(List<String> destinations) {
        int hostIndex = destinations.size()==1?0: ThreadLocalRandom.current().nextInt(0,destinations.size());
        return destinations.get(hostIndex);
    }
}
