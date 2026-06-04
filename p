[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO] 
[INFO] piedrazul-system                                                   [pom]
[INFO] user-service                                                       [jar]
[INFO] availability-service                                               [jar]
[INFO] booking-service                                                    [jar]
[INFO] 
[INFO] --------------< com.groupsoft.piedrazul:piedrazul-system >--------------
[INFO] Building piedrazul-system 1.0.0-SNAPSHOT                           [1/4]
[INFO]   from pom.xml
[INFO] --------------------------------[ pom ]---------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for piedrazul-system 1.0.0-SNAPSHOT:
[INFO] 
[INFO] piedrazul-system ................................... FAILURE [  0.025 s]
[INFO] user-service ....................................... SKIPPED
[INFO] availability-service ............................... SKIPPED
[INFO] booking-service .................................... SKIPPED
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.831 s
[INFO] Finished at: 2026-05-28T22:00:17-05:00
[INFO] ------------------------------------------------------------------------
[ERROR] Unknown lifecycle phase "user-service". You must specify a valid lifecycle phase or a goal in the format <plugin-prefix>:<goal> or <plugin-group-id>:<plugin-artifact-id>[:<plugin-version>]:<goal>. Available lifecycle phases are: pre-clean, clean, post-clean, validate, initialize, generate-sources, process-sources, generate-resources, process-resources, compile, process-classes, generate-test-sources, process-test-sources, generate-test-resources, process-test-resources, test-compile, process-test-classes, test, prepare-package, package, pre-integration-test, integration-test, post-integration-test, verify, install, deploy, pre-site, site, post-site, site-deploy. -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/LifecyclePhaseNotFoundException
