# demo-spring-cloud-microservices

## Kubernetes

### Building Docker images

Normally, we have to push images to a Docker registry and configure Kubernetes to pull images from the registry. In our case, where we have a local single Node cluster, we can shortcut this process by pointing our Docker client to the Docker engine in Minikube and then running the `docker-compose build` command. This will result in the Docker images being immediately available to Kubernetes. For development, we will be using `latest` as the Docker image version for the microservices.

You can build Docker images from source as follows:

    cd $BOOK_HOME/Chapter16
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
