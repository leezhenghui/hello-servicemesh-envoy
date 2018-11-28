'use strict'

const express = require('express')
const http = require('http')
const program = require('commander')
const debug = require('debug')('frontend:app');

//====================
// program parser
//====================
program
  .version('1.0.0')
  .usage('[options] ')
  .option('-h, --hostname [hostname]', 'Hostname or IP address for the service to bind, if not presented, use the one specfied in conf file')
  .option('-p, --port [port]', 'The service listening port, if not presented, use the one specified in conf file')
  .option('-l, --log-level [log-level]', 'The service log-level')
  .option('-m, --mode [mode]', 'The service process mode, production, stage or test')
  .parse(process.argv)

let transportConf = {
  host: program.hostname,
  port: program.port
}

const app = express();
const router = require('./routes');

app.use('/', router);

let hostInEnv = process.env.NOMAD_IP_http;
let portInEnv = process.env.NOMAD_PORT_http;

let host = hostInEnv || 'localhost'; 
let port = portInEnv || 8080; 

let server = http.createServer(app)
server.listen(port, host)
debug(`Server start up on listening [host:${host}, port:${port}]`)
