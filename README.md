# demo-spring-cloud-microservices

## Kubernetes

### Building Docker images

Normally, we have to push images to a Docker registry and configure Kubernetes to pull images from the registry. In our case, where we have a local single Node cluster, we can shortcut this process by pointing our Docker client to the Docker engine in Minikube and then running the `docker-compose build` command. This will result in the Docker images being immediately available to Kubernetes. For development, we will be using `latest` as the Docker image version for the microservices.

You can build Docker images from source as follows:

    eval $(minikube docker-env)
    mvn clean package -DskipTests && docker-compose build

If you're using docker desktop you should switch your context to it

    kubectl config use-context docker-desktop

### Resolving Helm chart dependencies

First, we update the dependencies in the components folder:

    for f in k8s/helm/components/*; do helm dep up $f; done

Next, we update the dependencies in the environments folder:

    for f in k8s/helm/environments/*; do helm dep up $f; done

Finally, we verify that the dependencies for the dev-env folder look good:

    helm dep ls k8s/helm/environments/dev-env/


### Deploying to Kubernetes

A deploy to Kubernetes means creating or updating Kubernetes objects. We will use Helm to perform the deployment, per the following steps:

1. **(Skip)** To avoid a slow deployment process due to Kubernetes downloading Docker images (potentially causing the liveness probes we described previously to restart our Pods), run the following docker pull commands to download the images in advance:


    eval $(minikube docker-env)
    docker pull mysql:5.7.32 
    docker pull mongo:4.4.2
    docker pull rabbitmq:3.8.11-management
    docker pull openzipkin/zipkin:2.23.2


2. Before using the Helm charts, render the templates using the helm template command to see what the manifests will look like:


    helm template k8s/helm/environments/dev-env

Note that no interaction was performed with the Kubernetes cluster, so cluster information will be faked, and no tests are run to verify whether the rendered manifest will be accepted by the cluster.

3. To also verify that the Kubernetes cluster will actually accept the rendered manifest, a **dry run** of the installation can be performed by passing `–-dry-run` to the `helm install` command. Passing the `--debug` flag will also show which user-supplied and calculated values Helm will use when rendering the manifests. Run the following command to perform a dry run:


    helm install --dry-run --debug slartibartfast-dev-env \
    k8s/helm/environments/dev-env

4. To initiate the deployment of the complete system landscape including creating the Namespace, slartibartfast, run the following command:


    helm install slartibartfast-dev-env \
    k8s/helm/environments/dev-env \
    -n slartibartfast \
    --create-namespace


5. Set the newly created Namespace as the default Namespace for kubectl:


    kubectl config set-context $(kubectl config current-context) --namespace=slartibartfast

6. To see the Pods starting up, run the command:


    kubectl get pods --watch

This command will continuously report when new Pods are Running, and if something goes wrong it will report the status, for example Error and CrashLoopBackOff. After a while, you will probably see that errors are reported for the gateway and payment-api Pods. The reason for this is that they all depend on external resources that they require to be accessible during the startup. If not, they will crash. The gateway and product composite service depend on the auth server, and the Zipkin server depends on access to RabbitMQ. Typically, they start up faster than the resources they rely on, causing this situation. However, Kubernetes will detect the crashed Pods and they will be restarted. Once the resources are up and running, all Pods will start up and be reported as ready, showing 1/1 in the READY column.

After seeing some output like the above, interrupt the command with `Ctrl+C`.

7. Wait for all the Pods in the Namespace to be ready with the command:


    kubectl wait --timeout=600s --for=condition=ready pod --all

Expect the command to respond with eleven log lines like pod/... condition met, where the three dots (...) are replaced with the name of the actual Pod that is reported to be ready.

8. To see the Docker images that are used, run the following command:


    kubectl get pods -o json | jq .items[].spec.containers[].image

Note that the Docker images have the version tag set to latest for the microservices.

We are now ready to test our deployment!

**Note**: In case you couldn't access your gateway server over NodePort you can use port forwarding, `kubectl port-forward -n slartibartfast service/gateway-server 30443:8443` 

**Note**: To use feign client inside kubernetes environment we had to add dependency to our payment-api-service and authorization-server services.
With kubernetes loadbalancer dependency we got serviceaccount error when starting these 2 app. For this reason we had to create a rolebinding like below;


      kubectl create clusterrolebinding service-reader-pod   --clusterrole=service-reader    --serviceaccount=slartibartfast:default
      kubectl create clusterrolebinding admin --clusterrole=cluster-admin --serviceaccount=slartibartfast:default

### Deploying with Kubernetes ConfigMaps, Secrets, Ingress, and the cert-manager

With the preceding changes described, we are ready to test the system landscape with the Spring Cloud Config Server and the Spring Cloud Gateway replaced by Kubernetes ConfigMaps, Secrets, an Ingress object, and the cert-manager. As before, when we used the Spring Cloud Gateway as the edge server, the external API will be protected by HTTPS. With this deployment, it will be the Ingress controller that uses the certificate provisioned by the cert-manager to protect the external API with HTTPS.

If you're using minikube you can enable Ingress controller with `minikube addons enable ingress` command.


In this section, we will install the cert-manager and add an entry to the `/etc/hosts` file for the hostname `kubernetes.docker.internal`.

Execute the following steps to deploy the system landscape and verify that it works as expected:

1. Install the cert-manager in the cert-manager Namespace and wait for the deployment to complete. Before the cert-manager can be installed, we need to add its Helm repository. Run the following commands:


    helm repo add jetstack https://charts.jetstack.io
    helm repo update
    helm install cert-manager jetstack/cert-manager \
      --namespace cert-manager \
      --create-namespace \
      --version v1.9.1 \
      --set installCRDs=true \
      --wait

**Note:** 

    The cert-manager also comes with a set of Kubernetes Custom Resource Definitions (CRDs), like the Issuer object that was introduced above. 
    CRDs are used in Kubernetes to extend its API, that is, to add new objects to its API. The --set installCRDs=true flag in the command above 
    ensures that these object definitions are installed when installing the cert-manager.

Verify that three Pods are ready in the cert-manager Namespace with the following command:

    kubectl get pods --namespace cert-manager

2. Install ingress-nginx with ingress-nginx namesapce and wait for the deployment to complete.
   

      helm upgrade --install ingress-nginx ingress-nginx \
      --repo https://kubernetes.github.io/ingress-nginx \
      --namespace ingress-nginx --create-namespace

3. **For minikube only** Map minikube.me to the IP address we can use to reach the Minikube instance by adding a line to the `/etc/hosts` file:
On macOS, run the following command to add the line:

    
    sudo bash -c "echo $(minikube ip) minikube.me | tee -a /e

Verify the result with the `cat /etc/hosts` command. Expect a line that contains the IP address described above followed by `minikube.me`; for example, `192.168.64.199 minikube.me`.

**Please note:** `When you use minikube with docker driver (e.g. on an Apple M1 machine), you won’t be able to access the ingress exposed services via minikube IP. You need to run minikube tunnel to make the service avaiable via local ip 127.0.0.1 instead. You will also need to map minikube.data.gov.au to IP 127.0.0.1 instead in /etc/hosts.`

4. You can build Docker images from source as follows:


    eval $(minikube docker-env)
    mvn clean package -DskipTests && docker-compose build

5. **(Skip)**  Pull Docker images to avoid a slow deployment process due to Kubernetes downloading Docker images:


    eval $(minikube docker-env)
    docker pull mysql:5.7.32
    docker pull mongo:4.4.2
    docker pull rabbitmq:3.8.11-management
    docker pull openzipkin/zipkin:2.23.2

6. Resolve the Helm chart dependencies:
   1. First, we update the dependencies in the components folder:

        `for f in k8s/helm/components/*; do helm dep up $f; done`
   2. Next, we update the dependencies in the environments folder:
   
        `for f in k8s/helm/environments/*; do helm dep up $f; done`
   3. Before using the Helm charts, render the templates using the helm template command to see what the manifests will look like:

        `helm template k8s/helm/environments/dev-env`

7. Set the `slartibartfast` namespace as the default namespace for kubectl:

   `kubectl config set-context $(kubectl config current-context) --namespace=slartibartfast`

8. In a separate terminal window, run the following command to monitor how certificate objects are created by the cert-manager:

   `kubectl get certificates -w --output-watch-events`

9. Deploy the system landscape using Helm and wait for all deployments to complete:


      helm install slartibartfast-dev-env \
      k8s/helm/environments/dev-env \
      -n slartibartfast \
      --create-namespace \
      --wait

10. Note how the certificate is created by the cert-manager during the deployment. Expect the following output from the kubectl get certificates command.


      EVENT      NAME              READY   SECRET            AGE
      ADDED      tls-certificate           tls-certificate   0s
      MODIFIED   tls-certificate   False   tls-certificate   0s
      MODIFIED   tls-certificate   False   tls-certificate   0s
      MODIFIED   tls-certificate   False   tls-certificate   0s
      MODIFIED   tls-certificate   False   tls-certificate   0s
      MODIFIED   tls-certificate   True    tls-certificate   0s
      MODIFIED   tls-certificate   True    tls-certificate   0s

11. Stop the kubectl get certificates command with `Ctrl+C`.

**Note:** `If you're using kubernetes with docker-desktop you should use port forwarding with ingress service.
   kubectl port-forward --namespace=ingress-nginx service/ingress-nginx-controller 30443:443`


### Deploying Istio in a Kubernetes cluster

1. Install the istioctl binary with curl and add the istioctl client to your path, on a macOS or Linux system:

        $ curl -sL https://istio.io/downloadIstioctl | sh -
        $ export PATH=$HOME/.istioctl/bin:$PATH

2. Run a precheck to verify that the Kubernetes cluster is ready for installing Istio in it:

       istioctl experimental precheck
3. Install Istio using the demo profile with the following command:

       istioctl install --skip-confirmation \
       --set profile=demo \
       --set meshConfig.accessLogFile=/dev/stdout \
       --set meshConfig.accessLogEncoding=JSON

   The accessLog parameters are used to enable the Istio proxies to log requests that are processed. Once Pods are up and running with Istio proxies installed, 
   the access logs can be inspected with the command kubectl logs <MY-POD> -c istio-proxy.

4. Wait for the Deployment objects and their Pods to be available with the following command:

       kubectl -n istio-system wait --timeout=600s --for=condition=available deployment --all

5. Next, install the extra components with the commands:


      istio_version=$(istioctl version --short --remote=false)
      echo "Installing integrations for Istio v$istio_version"
      
      kubectl apply -n istio-system -f https://raw.githubusercontent.com/istio/istio/${istio_version}/samples/addons/kiali.yaml
      kubectl apply -n istio-system -f https://raw.githubusercontent.com/istio/istio/release-1.15/samples/addons/kiali.yaml
      
      kubectl apply -n istio-system -f https://raw.githubusercontent.com/istio/istio/${istio_version}/samples/addons/jaeger.yaml
      kubectl apply -n istio-system -f https://raw.githubusercontent.com/istio/istio/release-1.15/samples/addons/jaeger.yaml
      
      kubectl apply -n istio-system -f https://raw.githubusercontent.com/istio/istio/${istio_version}/samples/addons/prometheus.yaml
      kubectl apply -n istio-system -f https://raw.githubusercontent.com/istio/istio/release-1.15/samples/addons/prometheus.yaml
      
      kubectl apply -n istio-system -f https://raw.githubusercontent.com/istio/istio/${istio_version}/samples/addons/grafana.yaml
      kubectl apply -n istio-system -f https://raw.githubusercontent.com/istio/istio/release-1.15/samples/addons/grafana.yaml

   **Note:** `If any of these commands fail, try rerunning the failing command. Errors can occur due to timing issues, which can be resolved by running commands again. Specifically, the installation of Kiali can result in error messages starting with unable to recognize. Rerunning the command makes these error messages go away.`

6. Wait a second time for the extra components to be available with the following command:

       kubectl -n istio-system wait --timeout=600s --for=condition=available deployment --all

7. Finally, run the following command to see what we got installed:

       kubectl -n istio-system get deploy

   Expect output similar to this:

       NAME                   READY   UP-TO-DATE   AVAILABLE   AGE
       grafana                1/1     1            1           62s
       istio-egressgateway    1/1     1            1           6m20s
       istio-ingressgateway   1/1     1            1           6m20s
       istiod                 1/1     1            1           6m50s
       jaeger                 1/1     1            1           83s
       kiali                  1/1     1            1           2m11s
       prometheus             1/1     1            1           69s

8. Run the following command to apply the Helm chart:

       helm upgrade --install istio-hands-on-addons k8s/helm/environments/istio-system -n istio-system --wait

   This will result in the gateway being able to route requests for the following hostnames to the corresponding Kubernetes Service:

        kiali.kubernetes.docker.internal requests are routed to kiali:20001
        tracing.kubernetes.docker.internal requests are routed to tracing:80
        prometheus.kubernetes.docker.internal requests are routed to prometheus:9000
        grafana.kubernetes.docker.internal requests are routed to grafana:3000

   To verify that the certificate and secret objects have been created, run the following commands:

        kubectl -n istio-system get secret slartibartfast-certificate
        kubectl -n istio-system get certificate  slartibartfast-certificate

   Expect output like this:

        NAME                         TYPE                DATA   AGE
        slartibartfast-certificate   kubernetes.io/tls   3      4m6s
        NAME                         READY   SECRET                       AGE
        slartibartfast-certificate   True    slartibartfast-certificate   4m11s

9. Remove the line in `/etc/hosts` where `kubernetes.docker.internal` points to the IP address of the Minikube instance (minikube ip). 
Verify that `/etc/hosts` only contains one line that translates `kubernetes.docker.internal` and that it points to the IP address 
of the Istio ingress gateway; the value of $INGRESS_IP:

       kubectl get svc -n istio-system istio-ingressgateway`
       127.0.0.1 kubernetes.docker.internal grafana.kubernetes.docker.internal kiali.kubernetes.docker.internal prometheus.kubernetes.docker.internal tracing kubernetes.docker.internal kibana.kubernetes.docker.internal elasticsearch.kubernetes.docker.internal mail.kubernetes.docker.internal health.kubernetes.docker.internal

You can use port forwarding without minikube;

      kubectl port-forward --namespace=istio-system service/istio-ingressgateway 30443:443

10. Verify that Kiali, Jaeger, Grafana, and Prometheus can be reached through the tunnel with the following commands:

        curl -o /dev/null -sk -L -w "%{http_code}\n" https://kiali.kubernetes.docker.internal:30443/kiali/
        curl -o /dev/null -sk -L -w "%{http_code}\n" https://tracing.kubernetes.docker.internal:30443
        curl -o /dev/null -sk -L -w "%{http_code}\n" https://grafana.kubernetes.docker.internal:30443
        curl -o /dev/null -sk -L -w "%{http_code}\n" https://prometheus.kubernetes.docker.internal:30443/graph#/

Each command should return 200 (OK). If the request sent to Kiali doesn't return 200, it often means that its internal initialization is not complete. Wait a minute and try again in that case.

### Running commands to create the service mesh

Create the service mesh by running the following commands:

1. Build Docker images from source with the following commands:

       eval $(minikube docker-env)
       mvn clean package -DskipTests && docker-compose build

2. Recreate the slartibartfast Namespace, and set it as the default Namespace:

       kubectl delete namespace slartibartfast
       kubectl apply -f k8s/slartibartfast-namespace.yaml
       kubectl config set-context $(kubectl config current-context) --namespace=slartibartfast

  Note that the slartibartfast-namespace.yml file creates the slartibartfast Namespace labeled with istio-injection: enabled.
  This means that Pods created in this Namespace will get istio-proxy containers injected as sidecars automatically.
  
3. Resolve the Helm chart dependencies with the following commands:
  1. First, we update the dependencies in the components folder:

        `for f in k8s/helm/components/*; do helm dep up $f; done`

  2. Next, we update the dependencies in the environments folder:

        `for f in k8s/helm/environments/*; do helm dep up $f; done`
  3. Before using the Helm charts, render the templates using the helm template command to see what the manifests will look like:

        `helm template k8s/helm/environments/dev-env`

4. Deploy the system landscape using Helm and wait for all Deployments to complete:

    `helm install slartibartfast-dev-env \
      k8s/helm/environments/dev-env \
      -n slartibartfast --wait`

   OR

    `helm upgrade --install slartibartfast-dev-env k8s/helm/environments/dev-env`

5. Once the Deployment is complete, verify that we have two containers in each of the microservice Pods:

    `kubectl get pods`

Expect a response along the lines of the following:

     NAME                                    READY   STATUS    RESTARTS   AGE
     authorization-server-76574857b6-rv2sj   2/2     Running   0          35s
     card-api-service-778494dcd5-rpqnq       2/2     Running   0          35s
     merchant-api-service-74cfd4fb99-2ht9d   2/2     Running   0          35s
     payment-api-service-68f64dbfb9-xpt29    2/2     Running   0          35s

Note that the Pods that run our microservices report two containers per Pod; that is, they have the Istio proxy injected as a sidecar!

### Authenticating external requests using OAuth 2.0/OIDC access tokens

Istio Ingress Gateway can require and validate JWT-based OAuth 2.0/OIDC access tokens, in other words, protecting the microservices in the 
service mesh from external unauthenticated requests. Istio can also be configured to perform authorization but, we will not use it.

This is configured in the common Helm chart's template, `_istio_base.yaml`. We inserted `RequestAuthentication` and `AuthorizationPolicy` objects.

From the manifests, we can see the following:

    The RequestAuthentication named payment-request-authentication requires a valid JWT-encoded access token for requests sent to the payment-api service:
        It selects services that it performs request authentication for based on a label selector, app.kubernetes.io/name: payment-api-service.
        It allows tokens from the issuer, http://authorization-server:80.
        It will use the http://authorization-server.slartibartfast.svc.cluster.local/oauth2/jwks URL to fetch a JSON Web Key Set. 
          The key set is used to validate the digital signature of the access tokens.
        It will forward the access token to the underlying services, in our case the payment-api microservice.
    The AuthorizationPolicy named payment-require-jwt is configured to allow all requests to the payment-api service; it will not apply any authorization rules.

It can be a bit hard to understand whether Istio's RequestAuthentication is validating the access tokens or whether it is only the payment-api service 
that is performing the validation. One way to ensure that Istio is doing its job is to change the configuration of RequestAuthentication so that it always rejects access tokens.

To verify that RequestAuthentication is in action, apply the following commands:

Make a normal request:

    ACCESS_TOKEN=$(curl -k https://writer:secretkubernetes.docker.internal:30443/oauth2/token?grant_type=client_credentials -d grant_type=client_credentials -s | jq .access_token -r)
    echo ACCESS_TOKEN=$ACCESS_TOKEN
    curl -k https://kubernetes.docker.internal:30443/payment/1 -H "Authorization: Bearer $ACCESS_TOKEN" -i

Verify that it returns an HTTP response status code 200 (OK).
Edit the RequestAuthentication object and temporarily change the issuer, for example, to `http://authorization-server-x`:

    kubectl edit RequestAuthentication payment-request-authentication

Verify the change:

    kubectl get RequestAuthentication payment-request-authentication -o yaml

Verify that the issuer has been updated, in my case to `http://authorization-server-x`.
Make the request again. It should fail with the HTTP response status code 401 (Unauthorized) and the error message Jwt issuer is not configured:

    curl -k https://kubernetes.docker.internal:30443/payment/1 -H "Authorization: Bearer $ACCESS_TOKEN" -i

Since it takes a few seconds for Istio to propagate the change, the new name of the issuer, you might need to repeat the command a couple of times before it fails.

This proves that Istio is validating the access tokens!
Revert the changed name of the issuer back to http://authorization-server:

    kubectl edit RequestAuthentication payment-request-authentication

Verify that the request works again. First, wait a few seconds for the change to be propagated. Then, run the command:

    curl -k https://kubernetes.docker.internal:30443/payment/1 -H "Authorization: Bearer $ACCESS_TOKEN"

**Note:** You can use `istioctl proxy-config cluster deploy/istio-ingressgateway -n istio-system` command to fetch proxy address for authentication-server. Expect an output like below:

      SERVICE FQDN                                              PORT      SUBSET     DIRECTION     TYPE           DESTINATION RULE
      authorization-server.slartibartfast.svc.cluster.local     80        -          outbound      EDS            authorization-server.slartibartfast
      card-api-service.slartibartfast.svc.cluster.local         80        -          outbound      EDS            card-api-service.slartibartfast
      ...


### Protecting internal communication using mutual authentication (mTLS)

In this section, we will learn how Istio can be configured to automatically protect internal communication within the service mesh using mutual authentication (mTLS). When using mutual authentication, not only does the service prove its identity by exposing a certificate, but the clients also prove their identity to the service by exposing a client-side certificate. This provides a higher level of security compared to normal TLS/HTTPS usage, where only the identity of the service is proven. Setting up and maintaining mutual authentication, that is, the provisioning of new, and rotating of outdated, certificates to the clients, is known to be complex and is therefore seldom used. Istio fully automates the provisioning and rotation of certificates for mutual authentication used for internal communication inside the service mesh. This makes it much easier to use mutual authentication compared to setting it up manually.

To enable the use of mutual authentication managed by Istio, Istio needs to be configured both on the server side, using a policy called `PeerAuthentication`, 
and on the client side, using a `DestinationRule`.

The policy is configured in the `common` Helm chart's template, `_istio_base.yaml`.

`PeerAuthentication` policy is configured to allow both mTLS and plain HTTP requests using the `PERMISSIVE` mode. This enables Kubernetes to call liveness and readiness probes using plain HTTP.

We have also already met the DestinationRule manifests in the Content in the `_istio_dr_mutual_tls.yaml` template section. The central part of the DestinationRule manifests for requiring mTLS looks like this:

      trafficPolicy:
      tls:
      mode: ISTIO_MUTUAL

To verify that the internal communication is protected by mTLS, perform the following steps:

1. Go to the Kiali graph in a web browser (`https://kiali.kubernetes.docker.internal`).
2. Click on the **Display** button and enable the **Security** label. The graph will show a padlock on all communication links that are protected by Istio's automated mutual authentication.

**Note:** If you want to debug `istio ingress` you can change log level with `istioctl proxy-config log deploy/istio-ingressgateway --level debug -n istio-system` command.

**Note 2:** At some point i was getting the following error `Jwks doesn’t have key to match kid or alg from Jwt` .
Without changing anything, after a random amount of time (usually minutes) I can see in the logs that my cached JWT public key is updated:

    2022-10-12T09:09:45.317540Z info model Updated cached JWT public key from "http://authorization-server.slartibartfast.svc.cluster.local/oauth2/jwks"

After the cache is updated I could finally query my service.