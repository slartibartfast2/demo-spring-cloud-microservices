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