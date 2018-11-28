services {
	name = "client"
	port = 8080
	connect {
		sidecar_service {
			proxy {
				upstreams {
					destination_name = "echo"
					local_bind_port = 9191
				}
			}
		}
	}
}

services {
	name = "echo"
	port = 9090
	connect {
		sidecar_service {}
	}
}
