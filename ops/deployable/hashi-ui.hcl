job "hashiUIJob" {
  region = "bj"
  datacenters = [
    "dc-dev-01"]
  type = "service"

  group "server" {
    count = 1

    task "hashiUITask" {
      driver = "raw_exec"

      constraint {
        attribute = "${attr.kernel.name}"
        value = "linux"
      }

      config {
        command = "/usr/local/bin/hashi-ui-linux-amd64"
				args = ["--nomad-enable", "--consul-enable"]
      }

      service {
        name = "hashi-ui"
        port = "http"

        check {
          type = "http"
          path = "/"
          interval = "10s"
          timeout = "2s"
        }
      }

      env {
        NOMAD_ENABLE = 1
        NOMAD_ADDR = "http://127.0.0.1:4646"
        CONSUL_ENABLE = 1
      }

      resources {
        cpu = 500
        memory = 300 
        network {
          mbits = 5
          port "http" {
            static = 3000
          }
        }
      }
    }
  }
}
