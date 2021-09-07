
# Build 

Build parent project first, the parent project needs to be in the maven local repo.
<pre>
git clone https://github.com/quarkiverse/quarkus-operator-sdk.git
cd quarkus-operator-sdk
mvn install
</pre>

Build the httpd operator project
<pre>
cd quarkus-operator-sdk/samples/JAVA_HTTPD_OPERATOR
mvn package
oc create -f target/kubernetes/httpds.httpd.saberkan.io-v1.yml
</pre>

# Run
<pre>
mvn quarkus:dev
oc create -f src/main/resources/kubernetes/httpd.yml
</pre>

