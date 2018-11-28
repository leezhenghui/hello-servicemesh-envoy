job "envoyJob" {
  region = "bj"
  datacenters = ["dc-dev-01"]
  type = "service"
  constraint {
    attribute = "${attr.kernel.name}"
    value     = "linux"
  }

  group "envoyGroup" {
    count = 1 

    task "addsvc-envoy-task" {
			leader = true
      driver = "raw_exec"
			config {
				command = "/opt/consul/bin/consul"
				args = ["connect", "envoy", "-sidecar-for", "add-svc", "-proxy-id", "add-svc-proxy", "-admin-bind", "localhost:19000", "-envoy-binary=/opt/envoy/bin/envoy", "--", "-l", "debug"]
			}   
      resources {
        cpu    = 50 # 50 MHz
        memory = 10 # 10 MB
      }
    } # end of addsvc-envoy-task
    
		task "subsvc-envoy-task" {
      driver = "raw_exec"
			config {
				command = "/opt/consul/bin/consul"
				args = ["connect", "envoy", "-sidecar-for", "sub-svc", "-proxy-id", "sub-svc-proxy",  "-admin-bind", "localhost:19001", "-envoy-binary=/opt/envoy/bin/envoy", "--", "-l", "debug"]
			}   
      resources {
        cpu    = 50 # 50 MHz
        memory = 10 # 10 MB
      }
    } # end of addsvc-envoy-task
		
		task "addsvc-ref-envoy-task" {
      driver = "raw_exec"
			config {
				command = "/opt/consul/bin/consul"
        # -proxy-id is mandantory because there are two sidecar tasks for frontend service
				args = ["connect", "envoy", "-sidecar-for", "frontend", "-proxy-id", "add-svc-ref-proxy",  "-admin-bind", "localhost:19002", "-envoy-binary=/opt/envoy/bin/envoy", "--", "-l", "debug"]
			}   
      resources {
        cpu    = 50 # 50 MHz
        memory = 10 # 10 MB
      }
    } # end of addsvc-ref-envoy-task
		
		task "subsvc-ref-envoy-task" {
      driver = "raw_exec"
			config {
				command = "/opt/consul/bin/consul"
        # -proxy-id is mandantory because there are two sidecar tasks for frontend service
				args = ["connect", "envoy", "-sidecar-for", "frontend", "-proxy-id", "sub-svc-ref-proxy",  "-admin-bind", "localhost:19003", "-envoy-binary=/opt/envoy/bin/envoy", "--", "-l", "debug"]
			}   
      resources {
        cpu    = 50 # 50 MHz
        memory = 10 # 10 MB
      }
    } # end of subsvc-envoy-task
  }
}
