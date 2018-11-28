const debug = require('debug')('router:frontend');

const express = require('express');
const bodyParser = require('body-parser')
const router = express.Router();
const Q = require('q');

const PolicyRouter = require('express-policyrouter').PolicyRouter;
const policyRouter = new PolicyRouter(router);
const Operator = require('../services/operator');
const addOp = new Operator('+');
const subOp = new Operator('-');

//====================================
//   Policy Handlers Registraiton
//====================================
const SimpleLogger = require('./policies/logger')
policyRouter.register(new SimpleLogger())

const HealthChecker = require('express-policyrouter').HealthChecker;
policyRouter.register(new HealthChecker('/health/state'));

//====================================
//   Routers Registraiton
//====================================
policyRouter.use('/', bodyParser.json(), {
  name: 'httpSetting.bodyParser.json'
});

policyRouter.get('/health/state', function(req, res, next) {
	// do nothing
}, {
  name: 'health-checker-placeholder'
});

let iCount = 0;

policyRouter.get('/ivt', function(req, res, next) {
  iCount ++;
  debug('>>> welcome to ivt-test: ', iCount);
  res.status(200).send('ivt-test: ' +  iCount);
}, {
  name: 'ivt'
});

policyRouter.post('/api/v1/compute', function(req, res, next) {

	let expression = req.body && req.body.expression;

	if (! expression) {
    res.status(400).json({
	     reason: 'Invalid expression "' + expression + '"'
		});	
		return;
	}
	expression = expression + '=';
	debug('expression: ', expression);

	let promise;
	let l = '';
	let r = '';
	let currentOp;

	for (let i = 0; i < expression.length; i++) {
		const char = expression[i];
		if ('' === char.trim()) {
			continue;	
		}

		if ('-' === char || '+' === char || '=' === char) {
			if (! currentOp) {
				promise = Q({result: parseInt(l)});	
			} else {
				const intR = parseInt(r);
				const opSN = currentOp.clone(); 
				promise = promise.then(function(staggingResult) {
					return opSN.execute(staggingResult.result, intR);	
				});	
				r = '';
			}

			if ('=' === char) {
				continue;
			}

			if ('+' === char) {
				currentOp = addOp;
			}

			if ('-' === char) {
				currentOp = subOp;
			}
			continue;
		}

	  try {
			parseInt(char);
		} catch(err) {
			res.status(400).json({
				reason: 'Invalid parameter: "' + expression	+ '"'
			});
			return;
		}

		if (! currentOp) {
			l += char;	
			continue;
		}
		r += char;
	}

	debug('waiting for execute result' );
	promise.then(function(reval) {
		debug('result: ', reval);
		res.status(200).json(reval);
	}).fail(function(error) {
		debug('Error: ', error);
    res.status(500).json({
	    details: error	
		});	
	});
}, {
  name: 'api.v1.compute'
});

module.exports = router;
