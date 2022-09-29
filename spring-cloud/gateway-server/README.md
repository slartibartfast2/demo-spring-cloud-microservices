## Protecting external communication with HTTPS

We are using HTTPS to encrypt communication. To use HTTPS, we need to do the following:

    Create a certificate: We will create our own self-signed certificate, sufficient for development purposes
    Configure the edge server: It has to be configured to accept only HTTPS-based external traffic using the certificate

The self-signed certificate is created with the following command:

`keytool -genkeypair -alias localhost -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore gatewaytest.p12 -validity 3650`

The command will ask for a number of parameters. When asked for a password, I entered _testtest_. For the rest of the parameters, I simply entered an empty 
value to accept the default value. The certificate file created, gateway.p12, is placed in the gateway-server projects folder, src/main/resources/keystore. 
This means that the certificate file will be placed in the .jar file when it is built and will be available on the classpath 
at runtime at keystore/gateway.p12.

    Providing certificates using the classpath is sufficient during development, but not applicable to other environments, 
    for example, a production environment.

To configure the gateway server to use the certificate and HTTPS, the following is added to configuration under config-repo in the gateway project:

    server.port: 8443
    
    server.ssl:
    key-store-type: PKCS12
    key-store: classpath:keystore/gatewaytest.p12
    key-store-password: testtest
    key-alias: localhost

Some notes from the preceding source code:

*   The path to the certificate is specified in the server.ssl.key-store parameter, and is set to classpath:keystore/gateway.p12.This means that the certificate will be picked up on the classpath from the location keystore/gateway.p12.

*   The password for the certificate is specified in the server.ssl.key-store-password parameter.
*    To indicate that the edge server talks HTTPS and not HTTP, we also change the port from 8060 to 8443 in the server.port parameter.

To implement a better option, to replace the certificate packaged in the .jar file, perform the following steps:

*   Create certificate and set the password to _password_, when asked for it:


        mkdir keystore
        keytool -genkeypair -alias localhost -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore/gateway.p12 -validity 3650

*   Update the Docker Compose file, docker-compose.yml, with environment variables for the location, the password for the new certificate, and a volume that maps to the folder where the new certificate is placed. The configuration of the edge server will look like the following after the change:


        gateway:
          environment:
            - SPRING_PROFILES_ACTIVE=docker
            - SERVER_SSL_KEY_STORE=file:/keystore/gateway.p12
            - SERVER_SSL_KEY_STORE_PASSWORD=password
          volumes:
            - $PWD/keystore:/keystore
          build: spring-cloud/gateway-server
          mem_limit: 350m
          ports:
            - "8443:8443"
          networks:
            - my-network