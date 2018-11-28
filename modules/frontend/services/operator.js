'use strict'

const agent = require('superagent')
const Q = require('q')
const debug = require('debug')('services:operator');

const METHOD_GET = 'get'
const METHOD_POST = 'post'
const METHOD_PUT = 'put'
const METHOD_DELETE = 'delete'
const METHOD_PATCH = 'patch'

const REQ_DEFAULT_TIMEOUT = 2 * 60 * 1000

class Stub {

	static get METHOD_GET() {
    return 'get';	
	}
	
	static get METHOD_POST() {
    return 'post';	
	}

	static get METHOD_PUT() {
    return 'put';	
	}
	
	static get METHOD_DELETE() {
    return 'delete';	
	}
	
	static get METHOD_PATCH() {
    return 'patch';	
	}

  constructor(addr) {
		this.addr= addr;
  }

  invoke(method, path, inputs) {
    const self = this;
    const defer = Q.defer();

    try {
			path = path || '/';
			if ('/' !== path.charAt(0)) {
		    path = '/' + path;	
			}
			debug('addr: "' + self.addr + '"; path: "' + path + '"');
      const url = self.addr + path;
			method = method || Stub.METHOD_GET
      let request;
      switch (method.trim().toLowerCase()) {
        case Stub.METHOD_GET:
          request = agent.get(url);
          break;
        case Stub.METHOD_POST:
          request = agent.post(url);
          break;
        case Stub.METHOD_PUT:
          request = agent.put(url);
          break;
        case Stub.METHOD_DELETE:
          request = agent.del(url);
          break;
        case Stub.METHOD_PATCH:
          request = agent.patch(url);
          break;
        default:
					throw new Error('Unsupported HTTP Method: "' + method + '"');
      }
      request.set('Content-Type', 'application/json')
      if (inputs) {
        request.send(JSON.stringify(payload));
			}
      request.timeout({
        deadline: REQ_DEFAULT_TIMEOUT
      })
      request.then(res => {
        defer.resolve({result: parseInt(res.text.trim())})
      }).catch(error => {
        if (error.errno && error.errno === 'ETIME')
          error = {
            errorCode: 'ERPCTIME',
            httpCode: 504,
            reason: `${path} error : ${error.message}` || `rpc timeout for path ${path}`
          }
        defer.reject(error)
      })
    } catch (error) {
      defer.reject(error)
    }
    return defer.promise
  }
}

class Operator {
	constructor(op) {
    if (! op) throw new Error('Invalid operator: "' + op + '"');	
		let addr;
		if ('+' === op) {
			addr = process.env.ADD_SVC_ADDR; 
		} else if ('-' === op) {
			addr = process.env.SUB_SVC_ADDR;
		} else {
			throw new Error('Unsupported operator: "' + op + '"');
		}
		debug('Operator Service Addr:', addr);
		this.opInvoker = new Stub(addr);	
		this.op = op;
	}

	execute(l, r) {
		const path = '/api/v1/execute/?l=' + l + '&r=' + r;
		return this.opInvoker.invoke(Stub.METHOD_GET, path, null);
	}

	clone() {
		const self = this;
    return new Operator(self.op);	
	}
}

module.exports = Operator;

//==========================
//      Test
//==========================

// const addOp = new Operator('+');
// addOp.execute(1, 1).then(function(result) {
// 	console.log('-->', result);
// }).fail(function(error) {
//   console.error(error);
// });
