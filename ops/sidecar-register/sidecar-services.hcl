job "sidecar-job" {
  region = "bj"
  datacenters = ["dc-dev-01"]
  type = "batch"
  constraint {
    attribute = "${attr.kernel.name}"
    value     = "linux"
  }

  group "sidecar-group" {
    count = 1 

    task "addsvc-sidecar-task" {
			leader = true
      driver = "raw_exec"
			config {
				command = "/usr/bin/curl"
				args = ["-X", "PUT", "-H", "Content-Type: application/json", "-d", "{\"name\": \"add-svc-proxy\",\"kind\": \"connect-proxy\",\"proxy\": {\"destination_service_name\": \"add-svc\",\"local_service_address\": \"10.10.10.150\",\"local_service_port\": 9090},\"port\": 21000}",  "http://localhost:8500/v1/agent/service/register"]
			}   
      resources {
        cpu    = 50 # 50 MHz
        memory = 10 # 10 MB
      }
    } # end of addsvc-sidecar-task
    
		task "subsvc-sidecar-task" {
      driver = "raw_exec"
			config {
				command = "/usr/bin/curl"
				args = ["-X", "PUT", "-H", "Content-Type: application/json", "-d", "{\"name\": \"sub-svc-proxy\",\"kind\": \"connect-proxy\",\"proxy\": {\"destination_service_name\": \"sub-svc\",\"local_service_address\": \"10.10.10.150\",\"local_service_port\": 8080},\"port\": 21001}",  "http://localhost:8500/v1/agent/service/register"]
			}   
      resources {
        cpu    = 50 # 50 MHz
        memory = 10 # 10 MB
      }
    } # end of subsvc-sidecar-task

		task "addsvc-ref-sidecar-task" {
      driver = "raw_exec"
			config {
				command = "/usr/bin/curl"
				args = ["-X", "PUT", "-H", "Content-Type: application/json", "-d", "{\"name\": \"add-svc-ref-proxy\",\"kind\": \"connect-proxy\",\"proxy\": {\"destination_service_name\": \"frontend\",\"local_service_address\": \"10.10.10.150\",\"local_service_port\": 7070,\"upstreams\": [{\"destination_type\": \"service\",\"destination_name\": \"add-svc\",\"local_bind_address\": \"127.0.0.1\",\"local_bind_port\": 9191}]},\"port\": 21002}",  "http://localhost:8500/v1/agent/service/register"]
			}   
      resources {
        cpu    = 50 # 50 MHz
        memory = 10 # 10 MB
      }
    } # end of addsvc-ref-sidecar-task
		
		task "subsvc-ref-sidecar-task" {
      driver = "raw_exec"
			config {
				command = "/usr/bin/curl"
				args = ["-X", "PUT", "-H", "Content-Type: application/json", "-d", "{\"name\": \"sub-svc-ref-proxy\",\"kind\": \"connect-proxy\",\"proxy\": {\"destination_service_name\": \"frontend\",\"local_service_address\": \"10.10.10.150\",\"local_service_port\": 7070,\"upstreams\": [{\"destination_type\": \"service\",\"destination_name\": \"sub-svc\",\"local_bind_address\": \"127.0.0.1\",\"local_bind_port\": 8181}]},\"port\": 21003}",  "http://localhost:8500/v1/agent/service/register"]
			}   
      resources {
        cpu    = 50 # 50 MHz
        memory = 10 # 10 MB
      }
    } # end of subsvc-ref-sidecar-task

  }
}
