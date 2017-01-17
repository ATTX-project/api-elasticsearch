# Setup of the service component that provides a public interface

Feature: Setting up public API component
   As a platform admin
   In order to provide public interface to the data
   I want API component up and with all the bell and whistles running

    Scenario: check if all the required plugins are installed
        Given component is running
        When listing available plugins
        Then all the required plugins should be installed successfully.
        