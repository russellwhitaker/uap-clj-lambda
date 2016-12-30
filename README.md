# uap-clj-lambda

An Apache AWS Lambda wrapper around the [`uap-clj`](https://github.com/russellwhitaker/uap-clj) library providing Browser, O/S, and Device field extraction functions.

## Setup for development

### Running the test suite

The testrunner is [`speclj`](http://speclj.com), `lein` aliased:

```bash
→ lein test

event handling
  single full lookup
  - takes a useragent string and returns browser, device, and o/s fields
  single browser lookup
  - takes a useragent string and returns browser fields
  single device lookup
  - takes a useragent string and returns device fields
  single o/s lookup
  - takes a useragent string and returns o/s fields
  multiple mixed lookup
  - handles multiple mixed queries in one payload

Finished in 0.04076 seconds
5 examples, 0 failures
```

### Java version dependencies

This Lambda function runs in Amazon AWS Lambda's `java8` runtime, and has been shown to work
with these builds under both Java v1.7 and v1.8 SDKs (v1.8 strongly recommended):

```bash
→ java -version
java version "1.7.0_51"
Java(TM) SE Runtime Environment (build 1.7.0_51-b13)
Java HotSpot(TM) 64-Bit Server VM (build 24.51-b03, mixed mode)

→ java -version
java version "1.8.0_102"
Java(TM) SE Runtime Environment (build 1.8.0_102-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14, mixed mode)
```

## Operational deployment

### Deploy uberjar-packaged function to Amazon AWS Lambda

```bash
→ lein build
Compiling uap-clj-lambda.core
Created /<pathname>/uap-clj-lambda/target/uap-clj-lambda-1.0.0.jar
Created /<pathname>/uap-clj-lambda/target/simple-useragent-lambda.jar
```

`simple-useragent-lambda.jar` is the uberjar you'll upload to AWS when you create your Lambda function using the `aws` CLI (select `--timeout` and `--memory-size` according to your needs):

```bash
→ aws lambda create-function \
    --region <aws_region> \
    --function-name my-useragent-lookup \
    --zip-file fileb://$(pwd)/target/simple-useragent-lambda.jar \
    --role arn:aws:iam::<amazon_id>:role/<lambda_exec_role> \
    --handler uap-clj-lambda.core.UseragentLookup \
    --runtime java8 \
    --timeout 60 \
    --memory-size 512 \
    --description "Look up one or more useragent strings and output all or one of browser, device, and o/s information maps"
```

A successful `create-function` will emit something like this:
```json
{
    "CodeSha256": "SOMESHA256ENCODEDSTRING",
    "FunctionName": "my-useragent-lookup",
    "CodeSize": 20076098,
    "MemorySize": 512,
    "FunctionArn": "arn:aws:lambda:<aws_region>:<amazon_id>:function:my-useragent-lookup",
    "Version": "$LATEST",
    "Role": "arn:aws:iam::<amazon_id>:role/<lambda_exec_role>",
    "Timeout": 60,
    "LastModified": "2016-12-30T00:48:43.692+0000",
    "Handler": "uap-clj-lambda.core.UseragentLookup",
    "Runtime": "java8",
    "Description": "Look up one or more useragent strings and output all or one of browser, device, and o/s information maps"
}
```

You can test with the CLI `invoke` method. Since the payload can be cumbersome to escape properly on the commandline, you might create a file `test_payload.json` with contents like:

```json
{"queries":
  [{"ua":"Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4",
    "lookup":"useragent"},
   {"ua":"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.19) Gecko/2010031218 FreeBSD/i386 Firefox/3.0.19,gzip(gfe),gzip(gfe)",
    "lookup":"browser"},
   {"ua":"Mozilla/5.0 (Linux; U; Android 4.0.3; en-us; Amaze_4G Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
    "lookup":"device"},
   {"ua":"UCWEB/2.0 (Linux; U; Adr 2.3.6; en-US; HUAWEI_Y210-0251) U2/1.0.0 UCBrowser/8.6.0.318 U2/1.0.0 Mobile",
    "lookup":"os"}]}
```
Then invoke the function using the JSON contents of the test payload as input:

```bash
→ aws lambda invoke \
    --invocation-type RequestResponse \
    --function-name my-useragent-lookup --region <aws_region> \
    --log-type Tail \
    --payload file://test_payload.json \
    output.log
```

A successful `invoke` will emit something like this:
```json
{
    "LogResult": "U1RBUlQgUmVxdWVzdElkOiAzMDYwYzY1Mi1jZTUxLTExZTYtOTI0Ni1mOTZkZmRjZDNlYTcgVmVyc2lvbjogJExBVEVTVAoxNi0xMi0zMCAwNTozMToxOCBpcC0xMC0yMS0xMDItMTAuZWMyLmludGVybmFsIElORk8gW3VhcC1jbGotbGFtYmRhLmNvcmU6MzddIC0gezpyZXN1bHRzIFt7OnVhICJMZW5vdm8tQTI4OHRfVEQvUzEwMCBMaW51eC8yLjYuMzUgQW5kcm9pZC8yLjMuNSBSZWxlYXNlLzAyLjI5LjIwMTIgQnJvd3Nlci9BcHBsZVdlYmtpdDUzMy4xIE1vYmlsZSBTYWZhcmkvNTMzLjEgRmx5Rmxvdy8xLjQiLCA6YnJvd3NlciB7OmZhbWlseSAiQmFpZHUgRXhwbG9yZXIiLCA6bWFqb3IgIjEiLCA6bWlub3IgIjQiLCA6cGF0Y2ggIiJ9LCA6b3MgezpmYW1pbHkgIkFuZHJvaWQiLCA6bWFqb3IgIjIiLCA6bWlub3IgIjMiLCA6cGF0Y2ggIjUiLCA6cGF0Y2hfbWlub3IgIiJ9LCA6ZGV2aWNlIHs6ZmFtaWx5ICJMZW5vdm8gQTI4OHRfVEQiLCA6YnJhbmQgIkxlbm92byIsIDptb2RlbCAiQTI4OHRfVEQifX0gezpicm93c2VyIHs6ZmFtaWx5ICJGaXJlZm94IiwgOm1ham9yICIzIiwgOm1pbm9yICIwIiwgOnBhdGNoICIxOSJ9LCA6dWEgIk1vemlsbGEvNS4wIChYMTE7IFU7IExpbnV4IGk2ODY7IGVuLVVTOyBydjoxLjkuMC4xOSkgR2Vja28vMjAxMDAzMTIxOCBGcmVlQlNEL2kzODYgRmlyZWZveC8zLjAuMTksZ3ppcChnZmUpLGd6aXAoZ2ZlKSJ9IHs6ZGV2aWNlIHs6ZmFtaWx5ICJIVEMgQW1hemUgNEciLCA6YnJhbmQgIkhUQyIsIDptb2RlbCAiQW1hemUgNEcifSwgOnVhICJNb3ppbGxhLzUuMCAoTGludXg7IFU7IEFuZHJvaWQgNC4wLjM7IGVuLXVzOyBBbWF6ZV80RyBCdWlsZC9JTUw3NEspIEFwcGxlV2ViS2l0LzUzNC4zMCAoS0hUTUwsIGxpa2UgR2Vja28pIFZlcnNpb24vNC4wIE1vYmlsZSBTYWZhcmkvNTM0LjMwIn0gezpvcyB7OmZhbWlseSAiQW5kcm9pZCIsIDptYWpvciAiMiIsIDptaW5vciAiMyIsIDpwYXRjaCAiNiIsIDpwYXRjaF9taW5vciAiIn0sIDp1YSAiVUNXRUIvMi4wIChMaW51eDsgVTsgQWRyIDIuMy42OyBlbi1VUzsgSFVBV0VJX1kyMTAtMDI1MSkgVTIvMS4wLjAgVUNCcm93c2VyLzguNi4wLjMxOCBVMi8xLjAuMCBNb2JpbGUifV19CkVORCBSZXF1ZXN0SWQ6IDMwNjBjNjUyLWNlNTEtMTFlNi05MjQ2LWY5NmRmZGNkM2VhNwpSRVBPUlQgUmVxdWVzdElkOiAzMDYwYzY1Mi1jZTUxLTExZTYtOTI0Ni1mOTZkZmRjZDNlYTcJRHVyYXRpb246IDU0OS45NyBtcwlCaWxsZWQgRHVyYXRpb246IDYwMCBtcyAJTWVtb3J5IFNpemU6IDUxMiBNQglNYXggTWVtb3J5IFVzZWQ6IDEwMCBNQgkK",
    "StatusCode": 200
}
```

You can have a look at the non-encoded version of the log output in the file you specified:

```json
→ cat output.log
{"results":[{"ua":"Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4","browser":{"family":"Baidu Explorer","major":"1","minor":"4","patch":""},"os":{"family":"Android","major":"2","minor":"3","patch":"5","patch_minor":""},"device":{"family":"Lenovo A288t_TD","brand":"Lenovo","model":"A288t_TD"}},{"browser":{"family":"Firefox","major":"3","minor":"0","patch":"19"},"ua":"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.19) Gecko/2010031218 FreeBSD/i386 Firefox/3.0.19,gzip(gfe),gzip(gfe)"},{"device":{"family":"HTC Amaze 4G","brand":"HTC","model":"Amaze 4G"},"ua":"Mozilla/5.0 (Linux; U; Android 4.0.3; en-us; Amaze_4G Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"},{"os":{"family":"Android","major":"2","minor":"3","patch":"6","patch_minor":""},"ua":"UCWEB/2.0 (Linux; U; Adr 2.3.6; en-US; HUAWEI_Y210-0251) U2/1.0.0 UCBrowser/8.6.0.318 U2/1.0.0 Mobile"}]}
```

## Future / Enhancements

- add hook for AWS SNS notification
- add dispatch to new memoized versions of [uap-clj](http://github.com/russellwhitaker/uap-clj) functions (`uap-clj` >= v1.3.1)
- add hook to populate completed queries into AWS ElastiCache for performance improvement on subsequent invocations 

Pull requests will be very happily considered.

__Maintained by Russell Whitaker__

## License

The MIT License (MIT)

Copyright (c) 2015-2016 Russell Whitaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
