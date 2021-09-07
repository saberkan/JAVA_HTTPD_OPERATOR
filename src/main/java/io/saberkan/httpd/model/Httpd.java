package io.saberkan.httpd.model;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("httpd.saberkan.io")
@Version("v1alpha1")
@ShortNames("httpd")
public class Httpd extends CustomResource<HttpdSpec, HttpdStatus> implements Namespaced {

}
