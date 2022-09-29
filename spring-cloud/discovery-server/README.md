## Securing access to the discovery server

We will use HTTP Basic authentication to restrict access to the APIs and web pages on the discovery server, Netflix Eureka. This means that we will require a user to supply a username and password to get access. Changes are required both on the Eureka server and in the Eureka clients, described as follows.

To protect the Eureka server, the following changes have been applied in the source code:

1. In `pom.xml`, a dependency has been added for Spring Security:

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

2. Security configuration has been added to the SecurityConfig class:

* The user is defined as follows:

        public void configure(AuthenticationManagerBuilder auth) throws Exception {
          auth.inMemoryAuthentication()
           .passwordEncoder(NoOpPasswordEncoder.getInstance())
           .withUser(username).password(password)
           .authorities("USER");
        }

* The username and password are injected into the constructor from the configuration file:

        @Autowired
        public SecurityConfig(
          @Value("${app.eureka-username}") String username,
          @Value("${app.eureka-password}") String password
        ) {
          this.username = username;
          this.password = password;
        }

* All APIs and web pages are protected using HTTP Basic authentication by means of the following definition:

        protected void configure(HttpSecurity http) throws Exception {
          http
            .authorizeRequests()
              .anyRequest().authenticated()
              .and()
              .httpBasic();
        }

* Credentials for the user are set up in the configuration file, bootstrap.yml:


        app:
            eureka-username: u
            eureka-password: p

* Finally, the test class, EurekaServerApplicationTests, uses the credentials from the configuration file when testing the APIs of the Eureka server:


      @Value("${app.eureka-username}")
      private String username;
    
      @Value("${app.eureka-password}")
      private String password;
    
      @Autowired
      public void setTestRestTemplate(TestRestTemplate testRestTemplate) {
          this.testRestTemplate = testRestTemplate.withBasicAuth(username, password);
      }

The above are the steps required for restricting access to the APIs and web pages of the discovery server, Netflix Eureka. It will now use HTTP Basic authentication and require a user to supply a username and password to get access. The last step is to configure Netflix Eureka clients so that they pass credentials when accessing the Netflix Eureka server.

For Eureka clients, the credentials can be specified in the connection URL for the Eureka server. This is specified in each client's configuration file, application.yml, as follows:

    app:
        eureka-username: u
        eureka-password: p
    
    eureka:
        client:
            serviceUrl:
                defaultZone: "http://${app.eureka-username}:${app.eureka-password}@${app.eureka-server:8061/eureka/"