job "apiGatewayJob" {
  # The "region" parameter specifies the region in which to execute the job. If
  # omitted, this inherits the default region name of "global".
  region = "bj"

  # The "datacenters" parameter specifies the list of datacenters which should
  # be considered when placing this task. This must be provided.
  datacenters = ["dc-dev-01"]

  # The "type" parameter controls the type of job, which impacts the scheduler's
  # decision on placement. This configuration is optional and defaults to
  # "service". For a full list of job types and their differences, please see
  # the online documentation.
  #
  # For more information, please see the online documentation at:
  #
  #     https://www.nomadproject.io/docs/jobspec/schedulers.html
  #
  type = "service"

  # The "constraint" stanza defines additional constraints for placing this job,
  # in addition to any resource or driver constraints. This stanza may be placed
  # at the "job", "group", or "task" level, and supports variable interpolation.
  #
  # For more information and examples on the "constraint" stanza, please see
  # the online documentation at:
  #
  #     https://www.nomadproject.io/docs/job-specification/constraint.html
  #
  constraint {
    attribute = "${attr.kernel.name}"
    value     = "linux"
  }

  # The "group" stanza defines a series of tasks that should be co-located on
  # the same Nomad client. Any task within a group will be placed on the same
  # client.
  #
  # For more information and examples on the "group" stanza, please see
  # the online documentation at:
  #
  #     https://www.nomadproject.io/docs/job-specification/group.html
  #
  group "apiGatewayGroup" {
    # The "count" parameter specifies the number of the task groups that should
    # be running under this group. This value must be non-negative and defaults
    # to 1.
    count = 1 

    # The "task" stanza creates an individual unit of work, such as a Docker
    # container, web application, or batch processing.
    #
    # For more information and examples on the "task" stanza, please see
    # the online documentation at:
    #
    #     https://www.nomadproject.io/docs/job-specification/task.html
    #
    task "apiGatewayTask" {

			leader = true

      # The "driver" parameter specifies the task driver that should be used to
      # run the task.
      # driver = "java"
      driver = "raw_exec"

      # The "config" stanza specifies the driver configuration, which is passed
      # directly to the driver to start the task. The details of configurations
      # are specific to each driver, so please see specific driver
      # documentation for more information.
      # config {
			# 	jar_path    = "local/api.gateway-1.0.0.jar"
			# 	jvm_options = ["-Xmx512m", "-Xms128m", "-Dspring.profiles.active=dev"]
      # }
			config {
				command = "java"
				args = ["-Xmx512m", "-Xms128m", "-Dspring.profiles.active=dev", "-jar", "local/api.gateway-1.0.0.jar"]
			}   

      # The "artifact" stanza instructs Nomad to download an artifact from a
      # remote source prior to starting the task. This provides a convenient
      # mechanism for downloading configuration files or data needed to run the
      # task. It is possible to specify the "artifact" stanza multiple times to
      # download multiple artifacts.
      #
      # For more information and examples on the "artifact" stanza, please see
      # the online documentation at:
      #
      #     https://www.nomadproject.io/docs/job-specification/artifact.html
      #
      artifact {
				source = "http://localhost/public/pkgs/api.gateway-1.0.0.jar"
				destination = "local/"
				options {
					archive = true 
        }
       }

      # The "logs" stanza instructs the Nomad client on how many log files and
      # the maximum size of those logs files to retain. Logging is enabled by
      # default, but the "logs" stanza allows for finer-grained control over
      # the log rotation and storage configuration.
      #
      # For more information and examples on the "logs" stanza, please see
      # the online documentation at:
      #
      #     https://www.nomadproject.io/docs/job-specification/logs.html
      #
      logs {
        max_files     = 4 
        max_file_size = 50 
      }

      # The "resources" stanza describes the requirements a task needs to
      # execute. Resource requirements include memory, network, cpu, and more.
      # This ensures the task will execute on a machine that contains enough
      # resource capacity.
      #
      # For more information and examples on the "resources" stanza, please see
      # the online documentation at:
      #
      #     https://www.nomadproject.io/docs/job-specification/resources.html
      #
      resources {
        cpu    = 500 # 500 MHz
        memory = 512 # 512 MB
        network {
          port "http" {
            static = 2809 
					}
        }
      }

      # The "service" stanza instructs Nomad to register this task as a service
      # in the service discovery engine, which is currently Consul. This will
      # make the service addressable after Nomad has placed it on a host and
      # port.
      #
      # For more information and examples on the "service" stanza, please see
      # the online documentation at:
      #
      #     https://www.nomadproject.io/docs/job-specification/service.html
      #
      service {
				name = "api-gateway"
        tags = ["spring-boot", "java", "gateway"]
        port = "http"
        check {
					type = "http"
					port = "http"
					path = "/actuator/health"
					interval = "10s"
					timeout = "5s"
        }
      }
    } # end of apiGatewayTask
  }
}
