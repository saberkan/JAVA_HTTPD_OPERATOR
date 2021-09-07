package io.saberkan.httpd.model;

public class HttpdSpec {

    private int replicas;
    //TODO: add httpdConfigMap to customize configuration

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    @Override
    public String toString() {
        return "HttpdSpec{" +
                "replicas=" + replicas +
                '}';
    }
}
