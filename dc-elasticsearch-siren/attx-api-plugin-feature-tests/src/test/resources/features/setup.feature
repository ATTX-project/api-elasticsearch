# Setup of the service component that provides a public interface


Feature: Setting up public API component
   As a platform admin
   In order to provide public interface to the data
   I want API component up and with all the bell and whistles running


   Scenario: platform admin starts the component
      Given runtime environment is in place
      And component image is available
      When component is finished with startup
      Then components API should be accessible via HTTP
#      And component should show up in the service registry as running

    Scenario: platform admin stops the component
        Given component is running        
        When component is sent a stop signal
        Then component's api should not available anymore
#        And component should be removed from the service registry

    Scenario: check if all the required plugins are installed
        Given component is running
        When listing available plugins
        Then all the required plugins should be installed succesfully
        