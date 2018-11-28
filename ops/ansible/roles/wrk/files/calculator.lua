-- example HTTP POST script which demonstrates setting the
-- HTTP method, body, and adding a header

wrk.method = "POST"
-- wrk.body   = "foo=bar&baz=quux"
-- wrk.headers["Content-Type"] = "application/x-www-form-urlencoded"
wrk.headers["Content-Type"] = "application/json"
wrk.body   = "{ \"expression\": \"1+1+2+3+4+5+6-1-2-3-4-5-6+9+8-4-5\"}"
