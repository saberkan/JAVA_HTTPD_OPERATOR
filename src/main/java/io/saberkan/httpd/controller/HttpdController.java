package io.saberkan.httpd.controller;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.*;
import io.saberkan.httpd.model.Httpd;
import io.saberkan.httpd.model.HttpdSpec;
import io.saberkan.httpd.model.HttpdStatus;

//API used : https://github.com/fabric8io/kubernetes-client/blob/master/doc/CHEATSHEET.md#deployment
@Controller(namespaces = Controller.WATCH_CURRENT_NAMESPACE)
public class HttpdController implements ResourceController<Httpd> {
    private static final Logger LOG = Logger.getLogger(HttpdController.class);

    @Inject
    KubernetesClient kubeClient;

    @Override
    public DeleteControl deleteResource(Httpd httpd, Context<Httpd> context) {
        LOG.debug("deleting httpd: " + httpd.toString());
        kubeClient.apps().deployments().inNamespace(httpd.getMetadata().getNamespace()).withName(httpd.getMetadata().getName()).delete();
        return DeleteControl.DEFAULT_DELETE;
    }

    @Override
    public UpdateControl<Httpd> createOrUpdateResource(Httpd httpd, Context<Httpd> context) {
        LOG.debug("httpd input: " + httpd.toString());

        //Get CR Information
        final HttpdSpec httpdCurrentSpec = httpd.getSpec();
        LOG.debug("httpdSpec: " + httpdCurrentSpec.toString());

        HttpdStatus httpdCurrentStatus = httpd.getStatus();
        //Already created
        if (httpdCurrentStatus != null && HttpdStatus.State.CREATED == httpdCurrentStatus.getState())
            //TODO: Even when created it may need to be updated, to be handled next
            return UpdateControl.noUpdate();

        //Newly created
        try {
            LOG.debug("Processing");
            HttpdStatus httpdStatus = new HttpdStatus();
            httpdStatus.setState(HttpdStatus.State.PROCESSING);
            httpd.setStatus(httpdStatus);

            LOG.debug("Reading input from yaml");
            InputStream deploymentStream = getClass().getClassLoader().getResourceAsStream("kubernetes/deployment.yml");
            LOG.debug("deploymentStream: " + deploymentStream.toString());

            Deployment deployment = kubeClient.apps().deployments().load(deploymentStream).get();
            LOG.debug("deployment: " + deploymentStream.toString());

            deployment.getMetadata().setName(httpd.getMetadata().getName());
            deployment.getMetadata().setNamespace(httpd.getMetadata().getNamespace());
            deployment.getSpec().setReplicas(httpd.getSpec().getReplicas());

            //TODO: Add svc and route

            kubeClient.resource(deployment).createOrReplace();

            httpdStatus.setMessage("Httpd: " + httpd.getMetadata().getName() + " has been successfully created");
            httpdStatus.setState(HttpdStatus.State.CREATED);
            httpdStatus.setError(false);
        } catch (Exception e) {
            LOG.debug("Error updating httpd: " + httpd.getMetadata().getName() + ", exception:  " + e.getMessage());
            httpdCurrentStatus.setMessage("Error updating httpd " + httpd.getMetadata().getName() + ": " + e.getMessage());
            httpdCurrentStatus.setState(HttpdStatus.State.ERROR);
            httpdCurrentStatus.setError(true);
        }

        return UpdateControl.updateStatusSubResource(httpd);
    }
}
