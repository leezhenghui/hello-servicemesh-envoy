/**
 * This is a adapter class with intention to adopt the legacy simple logger
 * the new policy router framework. The adapter will translate/delegate the
 * request to the legacy facility to keep consist behavior after move to 
 * the new policy router framework.
 *
 * @Author lizh
 *
 */

'use strict'

const PolicyHandler = require('express-policyrouter').PolicyHandler;
const debug = require('debug')('router:policy:logger');

class SimpleLogger extends PolicyHandler {
  constructor() {
    super();
  }

  getName() {
    return 'SimpleLogger';
  }

  begin(context, done) {
    try {
      let req = context.req;
      let res = context.res;
      let now = new Date();
      debug('[HTTP-Entry] resource-path: <'
        + context.req.originalUrl
        + '>; method: <'
        + context.req.method
        + '>; headers: <'
        + JSON.stringify(context.req.headers)
        + '> @' + now.toISOString());

    } catch (err) {
      debug.error('Error occurs during HTTP  enter, due to: ', err);
    } finally {
      done();
    }
  }

  end(context, done) {
    try {
      let req = context.req;
      let res = context.res;
      let now = new Date();
      debug('[HTTP-Exit] resource-path: <'
        + context.req.originalUrl
        + '>; method: <'
        + context.req.method
				+ '>; statusCode: <'
				+ ((context.res.statusCode) ? context.res.statusCode : '-')
        + '>; headers: <'
        + JSON.stringify(context.req.headers)
				+ '>; user: <'
				+ ((context.req.user) ? JSON.stringify(context.req.user) : '-')
        + '> @' + now.toISOString());
    } catch (err) {
      debug.error('Error occurs during HTTP exit, due to: ', err);
    } finally {
      done();
    }

  }

  preInvoke(context, cont) {
    let now = new Date();

    let protocol = context.req.protocol;
    if (context.req.headers && context.req.headers['x-forwarded-protocol']) {
      protocol = context.req.headers['x-forwarded-protocol'];
    }

    debug('[Entry] resource-path: <'
      + context.req.originalUrl
      + '>; protocol: <'
      + protocol
      + '>; method: <'
      + context.req.method
      + '>; user: <'
      + ((context.req.user) ? JSON.stringify(context.req.user) : '-')
      + '>; middleware-fn: <'
      + context.bindings.name
      + '> @' + now.toISOString());
    cont();
  }

  postInvoke(context, cont) {
    let now = new Date();
    let protocol = context.req.protocol;
    if (context.req.headers && context.req.headers['x-forwarded-protocol']) {
      protocol = context.req.headers['x-forwarded-protocol'];
    }
    debug('[Exit] resource-path: <'
      + context.req.originalUrl
      + '>; protocol: <'
      + protocol
      + '>; method: <'
      + context.req.method
      + '>; statusCode: <'
      + ((context.res.statusCode) ? context.res.statusCode : '-')
      + '>; user: <'
      + ((context.req.user) ? JSON.stringify(context.req.user) : '-')
      + '>; middleware-fn: <'
      + context.bindings.name
      + '> @' + now.toISOString());
    cont();
  }

}

module.exports = exports = SimpleLogger;
